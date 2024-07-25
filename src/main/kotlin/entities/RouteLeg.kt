import entities.RouteStep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteLeg(
    @SerialName("steps") val steps: List<RouteStep>,
    @SerialName("distance") val distance: Long,
    @SerialName("duration") val duration: Long,
    @SerialName("duration_in_traffic") val durationInTraffic: Long
){
    val polylineList: List<PorterLatLong> by lazy {
        steps.flatMap { it.polylineList }
    }
}