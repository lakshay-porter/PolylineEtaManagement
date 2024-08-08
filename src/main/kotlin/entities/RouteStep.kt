package entities

import PolylineUtils
import PorterLatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteStep(
    @SerialName("polyline") val polyline: String = "",
    @SerialName("distance") val distance: Long = 0,
    @SerialName("duration") val duration: Long = 0,
) {
    val polylineList: List<PorterLatLong> by lazy {
        PolylineUtils.decode(polyline)
    }
    val startLocation: PorterLatLong? by lazy {
        polylineList.firstOrNull()
    }
    val endLocation: PorterLatLong? by lazy {
        polylineList.lastOrNull()
    }
}
