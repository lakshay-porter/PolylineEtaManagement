package entities

import PorterLatLong
import RouteLeg

data class PolylineEtaDetails(
    val legs: List<RouteLeg>,
    val overviewPolyline: List<PorterLatLong>,
    val distance: Int,
    val duration: Long,
    val durationInTraffic: Long
) {

    override fun toString(): String {
        return "PolylineEtaDetails(" +
                "\n\t\tdistance=$distance," +
                "\n\t\tduration=$duration," +
                "\n\t\tdurationInTraffic=$durationInTraffic," +
//                "\n\t\toverviewPolyline=$overviewPolyline," +
                "\n\t\tpolylineList.size=${polylineList.size}," +
                "\n\t\toverviewPolyline.size=${overviewPolyline.size})"
    }

    val polylineList: List<PorterLatLong> by lazy {
        legs.flatMap { it.polylineList }
    }
}