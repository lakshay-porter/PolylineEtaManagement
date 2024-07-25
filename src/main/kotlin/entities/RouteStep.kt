package entities

import PolylineUtils
import PorterLatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteStep(
    @SerialName("polyline") val polyline: String,
    @SerialName("distance") val distance: Long,
    @SerialName("duration") val duration: Long,
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
