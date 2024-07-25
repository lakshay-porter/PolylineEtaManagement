package entities

import PolylineUtils
import PorterLatLong
import RouteLeg
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolylineEtaResponse(
    @SerialName("legs") val legs: List<RouteLeg>,
    @SerialName("polyline") val overviewPolyline: String,
) {
    fun toPolylineData(): PolylineData = PolylineData(
        legs = legs,
        distance = legs.sumOf { it.distance.toInt() },
        duration = legs.sumOf { it.duration },
        durationInTraffic = legs.sumOf { it.durationInTraffic }
    )

    private fun getSimplifiedRoute(): List<PorterLatLong> {
        val overviewPolylineList = PolylineUtils.decode(overviewPolyline)
//        val simplified = PolylineUtils.simplify(overviewPolylineList, 0.0001)
//        println("not simplified=${overviewPolylineList.size}")
//        println("simplified=${simplified.size}")
        return overviewPolylineList
    }
}

//
//private fun List<PolylineUtils.PorterLatLong>.toPorterLatLong(): List<PorterLatLong> {
//    return this.map { PorterLatLong(it.lat, it.lng) }
//}
