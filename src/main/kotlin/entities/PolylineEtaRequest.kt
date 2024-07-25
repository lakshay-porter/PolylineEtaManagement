package entities

import PorterLatLong

data class PolylineEtaRequest(
    val origin: PorterLatLong,
    val waypoints: List<PorterLatLong> = emptyList(),
    val destination: PorterLatLong,
    val stopOver: Boolean = true,
)