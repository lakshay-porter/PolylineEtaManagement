import kotlin.math.*

data class PorterLatLong(
    val lat: Double,
    val lng: Double,
    val provider: String? = null,
){
    override fun toString(): String {
        return "($lat,$lng)"
    }
}

fun PorterLatLong.getHaversineDistance(other: PorterLatLong): Double {
    val radius = 6371.0

    val radLat1 = this.lat.toRadian()
    val radLat2 = other.lat.toRadian()
    val dLat = (other.lat - this.lat).toRadian()
    val dLon = (other.lng - this.lng).toRadian()

    val a = sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(radLat1) * cos(radLat2)
    val c = 2 * asin(sqrt(a))
    return radius * c * 1000.0
}

fun Double.toRadian(): Double = this / 180 * PI

fun PorterLatLong.getLinearDistance(other: PorterLatLong): Double {
    val dLat = other.lat.toRadian() - this.lat.toRadian()
    val dLng = other.lng.toRadian() - this.lng.toRadian()
    return sqrt(dLat * dLat + dLng * dLng)
}