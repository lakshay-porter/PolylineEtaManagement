package usecases

import PolylineEtaService
import PorterLatLong
import entities.PolylineData
import entities.PolylineEtaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PolylineEtaRepository(
    private val polylineEtaService: PolylineEtaService,
    private val buildPolylineData: BuildPolylineData,
) {
    private var cachedPolylineData: PolylineData? = null

    suspend fun getPolylineData(
        crn: String,
        origin: PorterLatLong,
        destination: PorterLatLong,
        shouldClearAndUpdateCache: Boolean = false,
    ): PolylineData? {
        //TODO to be implemented on android front
        return if (!shouldClearAndUpdateCache && cachedPolylineData != null) cachedPolylineData
        else {
            val polylineDetails = fetchUpdatedPolyline(origin, destination)
            cachedPolylineData = polylineDetails
            polylineDetails
        }
    }

    private suspend fun fetchUpdatedPolyline(
        origin: PorterLatLong,
        destination: PorterLatLong,
    ): PolylineData = withContext(Dispatchers.IO) {
        val request = PolylineEtaRequest(origin = origin, destination = destination)
        buildPolylineData(polylineEtaService.getPolyLineEta(request))
    }


}