import entities.PolylineData
import entities.PolylineEtaDetails
import entities.TrimmedDurationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import usecases.ComputePolylineEtaData
import usecases.GetRemainingDuration
import usecases.GetSnappedLocationDetails
import usecases.PolylineEtaRepository

typealias SnappedLocation = PorterLatLong
typealias NextLocationOnPolyLine = PorterLatLong

class PolylineEtaManagerImpl
constructor(
    private val polylineEtaRepository: PolylineEtaRepository,
    private val getSnappedLocationDetails: GetSnappedLocationDetails,
    private val getRemainingDuration: GetRemainingDuration,
    private val computePolylineEtaData: ComputePolylineEtaData,
) : PolylineEtaManager {


    private var lastSnappedLocationMutable: PorterLatLong? = null
    override val lastSnappedLocation: PorterLatLong?
        get() = lastSnappedLocationMutable

    override suspend fun onLocationUpdate(
        startLocation: PorterLatLong,
        endLocation: PorterLatLong,
        driverLocation: PorterLatLong,
        crn: String,
    ): PolylineEtaDetails? =
        withContext(Dispatchers.Default) { onLocationUpdateActual(driverLocation, endLocation, crn) }

    private suspend fun onLocationUpdateActual(
        driverLocation: PorterLatLong,
        endLocation: PorterLatLong,
        crn: String,
    ): PolylineEtaDetails? {
        // check if driver deviated from path
        // fetch if required and update polyline on map
        // calculate updated ETA for updated polyline
        // snapping to polyline

        val polylineData =
            polylineEtaRepository.getPolylineData(crn, driverLocation, endLocation) ?: return null

        val snappedLocationDetails = getSnappedLocationDetails(driverLocation, polylineData)
        lastSnappedLocationMutable = snappedLocationDetails.snappedLocation
        println("snappedLocationDetails: $snappedLocationDetails")

        if (snappedLocationDetails.deviation > POLYLINE_SNAP_DISTANCE_THRESHOLD)
            return getNewPolylineData(crn, driverLocation, endLocation)?.toPolylineEtaDetails()

        val trimmedPolyLine = getTrimmedPolyLine(polylineData, snappedLocationDetails)
        val durationResult = getRemainingDuration(polylineData, snappedLocationDetails)
        computePolylineEtaData(polylineData, snappedLocationDetails, durationResult)
        return getResult(snappedLocationDetails, trimmedPolyLine, durationResult)
    }

    private suspend fun getNewPolylineData(crn: String, driverLocation: PorterLatLong, endLocation: PorterLatLong) =
        polylineEtaRepository.getPolylineData(crn, driverLocation, endLocation, true)

    private fun getTrimmedPolyLine(
        polylineData: PolylineData,
        snappedLocationDetails: CalculatedPolylineResultData
    ) = polylineData.polylineList.dropWhile { it != snappedLocationDetails.nextPointInPolylineData }

    private fun getResult(
        snappedLocationDetails: CalculatedPolylineResultData,
        trimmedPolyLine: List<PorterLatLong>,
        durationResult: TrimmedDurationResult
    ): PolylineEtaDetails {
        val polyline = if (snappedLocationDetails.snappedLocation == snappedLocationDetails.nextPointInPolylineData)
            trimmedPolyLine
        else listOf(snappedLocationDetails.snappedLocation) + trimmedPolyLine
        return PolylineEtaDetails(
            polyline = polyline,
            duration = durationResult.duration,
            durationInTraffic = durationResult.durationInTraffic,
            driverSnappedLocation = snappedLocationDetails.snappedLocation,
        )
    }

    companion object {
        private const val POLYLINE_SNAP_DISTANCE_THRESHOLD: Int = Int.MAX_VALUE
    }

}

