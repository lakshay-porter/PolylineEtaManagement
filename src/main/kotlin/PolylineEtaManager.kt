import entities.PolylineEtaDetails

interface PolylineEtaManager {
    val lastSnappedLocation: PorterLatLong?
    suspend fun onLocationUpdate(
        startLocation: PorterLatLong,
        endLocation: PorterLatLong,
        driverLocation: PorterLatLong,
        crn: String
    ): PolylineEtaDetails?
}
