package usecases

import PolylineEtaService
import PorterLatLong
import entities.PolylineData
import entities.PolylineEtaRequest

class PolylineEtaRepository(
    private val polylineEtaService: PolylineEtaService,
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
    ): PolylineData {
        val request = PolylineEtaRequest(origin = origin, destination = destination)
        return polylineEtaService.getPolyLineEta(request).toPolylineData()
    }


}