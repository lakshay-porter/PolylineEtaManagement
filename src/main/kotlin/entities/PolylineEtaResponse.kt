package entities

import PolylineUtils
import PorterLatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolylineEtaResponse(
    @SerialName("legs") val legs: List<RouteLeg> = emptyList(),
    @SerialName("polyline") val overviewPolyline: String = "",
) {

    private fun getSimplifiedRoute(): List<PorterLatLong> {
        val overviewPolylineList = PolylineUtils.decode(overviewPolyline)
//        val simplified = PolylineUtils.simplify(overviewPolylineList, 0.0001)
//        println("not simplified=${overviewPolylineList.size}")
//        println("simplified=${simplified.size}")
        return overviewPolylineList
    }
}

//
// private fun List<PolylineUtils.PorterLatLong>.toPorterLatLong(): List<PorterLatLong> {
//    return this.map { PorterLatLong(it.lat, it.lng) }
//}
