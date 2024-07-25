package usecases

import PolylineEtaService
import PorterLatLong
import entities.PolylineEtaDetails
import entities.PolylineEtaRequest

class PolylineEtaCacheManager(
    private val polylineEtaService: PolylineEtaService,
) {
    private var cachedPolylineEtaDetails: PolylineEtaDetails? = null

    suspend fun getPolylineEtaDetails(
        crn: String,
        origin: PorterLatLong,
        destination: PorterLatLong,
        shouldClearAndUpdateCache: Boolean = false,
    ): PolylineEtaDetails? {
        //TODO to be implemented on android front
        return if (!shouldClearAndUpdateCache && cachedPolylineEtaDetails != null) cachedPolylineEtaDetails
        else {
            val polylineDetails = fetchUpdatedPolyline(origin, destination)
            cachedPolylineEtaDetails = polylineDetails
            polylineDetails
        }
    }

    suspend fun savePolylineEtaDetails(polyline: PolylineEtaDetails) {
        //TODO to be implemented on android front
        cachedPolylineEtaDetails = polyline
    }

    private suspend fun fetchUpdatedPolyline(
        origin: PorterLatLong,
        destination: PorterLatLong,
    ): PolylineEtaDetails {
        val request = PolylineEtaRequest(origin = origin, destination = destination)
        return polylineEtaService.getPolyLineEta(request).toPolylineEtaDetails()
    }


}