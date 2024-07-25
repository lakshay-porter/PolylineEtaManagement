import kotlinx.coroutines.runBlocking
import usecases.GetSnappedLocationDetails
import usecases.PolylineEtaCacheManager
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) = runBlocking {
    println("Starting Process")
    val points = listOf(
        PorterLatLong(27.18266, 75.9582),
        PorterLatLong(27.17853, 75.95931)
    )
    val encoded = PolylineUtils.encode(
        points
    )
    val distance = points[0].getHaversineDistance(points[1])
    println("Encoded: $encoded, Distance: $distance")
    val polylineEtaManager = PolylineEtaManagerImpl(
        PolylineEtaCacheManager(polylineEtaService = PolylineEtaService()),
        getSnappedLocationDetails = GetSnappedLocationDetails(),
    )
    val time = measureTimeMillis {
        val result = polylineEtaManager.onLocationUpdate(
            startLocation = PorterLatLong(0.0, 0.0),
            endLocation = PorterLatLong(5.0, 7.0),
            driverLocation = PorterLatLong(27.17853,75.95931),
        )
        println("Result: $result")
    }
    println("Total Time taken: $time ms")
    println("Ending Process")
}