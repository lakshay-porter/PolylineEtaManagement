package entities

import PorterLatLong
import util.KDNode

data class PolylineData(
    val legs: List<RouteLeg>,
    val distance: Int,
    val duration: Long,
    val durationInTraffic: Long,
    val tree: KDNode,
) {

    override fun toString(): String {
        return "PolylineData(" +
                "\n\t\tdistance=$distance," +
                "\n\t\tduration=$duration," +
                "\n\t\tdurationInTraffic=$durationInTraffic," +
                "\n\t\tlegs.size=${legs.size}," +
                "\n\t\tlegs=${legs}," +
                "\n\t\tpolylineList.size=${polylineList.size}"
    }

    fun toPolylineEtaDetails(): PolylineEtaDetails {
        return PolylineEtaDetails(
            duration = duration,
            durationInTraffic = durationInTraffic,
            polyline = polylineList,
            driverSnappedLocation = polylineList.firstOrNull()
        )
    }

    val polylineList: List<PorterLatLong> by lazy {
        legs.flatMap { it.polylineList }
    }
}