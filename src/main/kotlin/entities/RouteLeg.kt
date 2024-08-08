package entities

import PorterLatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteLeg(
    @SerialName("steps") val steps: List<RouteStep> = emptyList(),
    @SerialName("distance") val distance: Long = 0,
    @SerialName("duration") val duration: Long = 0,
    @SerialName("duration_in_traffic") val durationInTraffic: Long = 0
) {
    val polylineList: List<PorterLatLong> by lazy {
        steps.flatMap { it.polylineList }
    }

    override fun toString(): String {
        return "entities.RouteLeg(steps=${steps.size})"
    }
}