package entities

import PorterLatLong


data class PolylineEtaDetails(
    val polyline: List<PorterLatLong>,
    val duration: Long,
    val durationInTraffic: Long,
) {
    val etaMinutes: Long get() = duration / 60
    val etaMinutesInTraffic: Long get() = durationInTraffic / 60

    override fun toString(): String = "PolylineEtaDetails(" +
            "\n\tpolyline.size=${polyline.size}," +
            "\n\tduration=$duration," +
            "\n\tdurationInTraffic=$durationInTraffic," +
            "\n\tetaMinutes=$etaMinutes," +
            "\n\tetaMinutesInTraffic=$etaMinutesInTraffic" +
            "\n)"
}