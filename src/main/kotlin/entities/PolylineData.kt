package entities

import PorterLatLong
import RouteLeg

data class PolylineData(
    val legs: List<RouteLeg>,
    val distance: Int,
    val duration: Long,
    val durationInTraffic: Long
) {

    override fun toString(): String {
        return "PolylineData(" +
                "\n\t\tdistance=$distance," +
                "\n\t\tduration=$duration," +
                "\n\t\tdurationInTraffic=$durationInTraffic," +
                "\n\t\tpolylineList.size=${polylineList.size}"
    }

    fun toPolylineEtaDetails(): PolylineEtaDetails {
        return PolylineEtaDetails(
            duration = duration,
            durationInTraffic = durationInTraffic,
            polyline = polylineList
        )
    }

    val polylineList: List<PorterLatLong> by lazy {
        legs.flatMap { it.polylineList }
    }
}