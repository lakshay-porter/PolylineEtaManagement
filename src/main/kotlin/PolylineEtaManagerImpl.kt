import entities.PolylineEtaDetails
import entities.TrimmedDurationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import usecases.GetSnappedLocationDetails
import usecases.PolylineEtaCacheManager
import kotlin.system.measureTimeMillis

typealias SnappedLocation = PorterLatLong
typealias NextLocationOnPolyLine = PorterLatLong

class PolylineEtaManagerImpl
constructor(
    private val polylineEtaCacheManager: PolylineEtaCacheManager,
    private val getSnappedLocationDetails: GetSnappedLocationDetails,
    private val crn: String,
) : PolylineEtaManager {


    private var lastSnappedLocationMutable: PorterLatLong? = null
    override val lastSnappedLocation: PorterLatLong?
        get() = lastSnappedLocationMutable

    override suspend fun onLocationUpdate(
        startLocation: PorterLatLong,
        endLocation: PorterLatLong,
        driverLocation: PorterLatLong,
    ): PolylineEtaDetails? = withContext(Dispatchers.Default) {
        val polylineDetails = onLocationUpdateActual(driverLocation, endLocation)
        polylineDetails?.let { polylineEtaCacheManager.savePolylineEtaDetails(polyline = it) }
        polylineDetails
    }

    private suspend fun onLocationUpdateActual(
        driverLocation: PorterLatLong,
        endLocation: PorterLatLong
    ): PolylineEtaDetails? {
        // check if driver deviated from path
        // fetch if required and update polyline on map
        // calculate updated ETA for updated polyline
        // snapping to polyline

        val polylineEtaDetails =
            polylineEtaCacheManager.getPolylineEtaDetails(driverLocation, endLocation) ?: return null
        val sumOfDurations = polylineEtaDetails.legs.sumOf { leg ->
            leg.steps.sumOf { step ->
                step.duration
            }
        }
        println("sumOfDurations: $sumOfDurations")
        val snappedLocationDetails = getSnappedLocationDetails(driverLocation, polylineEtaDetails)
        println("snappedLocationDetails: $snappedLocationDetails")

        if (snappedLocationDetails.deviation > POLYLINE_SNAP_DISTANCE_THRESHOLD)
            return polylineEtaCacheManager.getPolylineEtaDetails(
                driverLocation,
                endLocation,
                shouldClearAndUpdateCache = true
            )
        val trimmedPolyLine =
            polylineEtaDetails.polylineList.dropWhile { it != snappedLocationDetails.nextPointInPolylineData }

        println("trimmedPolyLine: ${trimmedPolyLine.size}")
        println("Original Polyline: ${polylineEtaDetails.polylineList.size}")
        val timeTaken = measureTimeMillis {
            val durationResult = getDuration(polylineEtaDetails, snappedLocationDetails)
            println("Duration Result: $durationResult")
        }
        println("Time taken to calculate duration: $timeTaken ms")


        return if (snappedLocationDetails.snappedLocation == snappedLocationDetails.nextPointInPolylineData)
            polylineEtaDetails.copy(overviewPolyline = trimmedPolyLine)
        else polylineEtaDetails.copy(overviewPolyline = listOf(snappedLocationDetails.snappedLocation) + trimmedPolyLine)
    }

    private fun getDuration(
        polylineEtaDetails: PolylineEtaDetails,
        snappedLocationDetails: CalculatedPolylineResultData,
    ): TrimmedDurationResult {
        var duration = 0L
        var durationInTraffic = 0L

        val currentLeg = polylineEtaDetails.legs[snappedLocationDetails.legIndex]
        val currentStep = currentLeg.steps[snappedLocationDetails.stepIndex]
        val trafficMultiplier = currentLeg.durationInTraffic.toFloat() / currentLeg.duration

        //add remaining duration in the current step
        val index = currentStep.polylineList.indexOf(snappedLocationDetails.snappedLocation)

        if (index != -1 && index + 1 < currentStep.polylineList.size) {
            val remainingPolyline = currentStep.polylineList.subList(index, currentStep.polylineList.size)
            val remainingDistance = 100 //todo
            val remainingDuration = (remainingDistance / currentStep.distance.toFloat() * currentStep.duration).toLong()
            val remainingDurationInTraffic = (remainingDuration * trafficMultiplier).toLong()
            duration += remainingDuration
            durationInTraffic += remainingDurationInTraffic
        }

        //sum of remaining steps in the current leg
        if (snappedLocationDetails.stepIndex + 1 < currentLeg.steps.size)
            for (i in snappedLocationDetails.stepIndex + 1 until currentLeg.steps.size) {
                duration += currentLeg.steps[i].duration
                durationInTraffic += (currentLeg.steps[i].duration * trafficMultiplier).toLong()
            }

        //sum of remaining legs
        if (snappedLocationDetails.legIndex + 1 < polylineEtaDetails.legs.size)
            for (i in snappedLocationDetails.legIndex + 1 until polylineEtaDetails.legs.size) {
                duration += polylineEtaDetails.legs[i].duration
                durationInTraffic += polylineEtaDetails.legs[i].durationInTraffic
            }
        return TrimmedDurationResult(duration = duration, durationInTraffic = durationInTraffic)
    }


    companion object {
        private const val POLYLINE_SNAP_DISTANCE_THRESHOLD: Int = Int.MAX_VALUE
    }

}

