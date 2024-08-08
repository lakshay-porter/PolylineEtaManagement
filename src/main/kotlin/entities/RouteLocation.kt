package entities

import PorterLatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteLocation constructor(
    @SerialName("latlng") val latlng: PorterLatLong,
    @SerialName("stepIndex") val stepIndex: Int,
    @SerialName("legIndex") val legIndex: Int,
)