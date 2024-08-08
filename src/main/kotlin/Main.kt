import kotlinx.coroutines.runBlocking
import usecases.*
import util.KDTreeManager
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) = runBlocking {

    val list = PolylineEtaService().temp().sortedBy {
        it.customerTs
    }
    list.forEach {
        println("${it.lat},${it.lng},${it.customerTs},${it.driverTs},${it.customerTs!! - it.driverTs!!}")
    }






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
    val kdTreeManager = KDTreeManager()
    val buildKDTree = BuildKDTree(kdTreeManager)
    val polylineEtaManager = PolylineEtaManagerImpl(
        PolylineEtaRepository(
            polylineEtaService = PolylineEtaService(),
            buildPolylineData = BuildPolylineData(buildKDTree)
        ),
        getRemainingDuration = GetRemainingDuration(),
        computePolylineEtaData = ComputePolylineEtaData(),
        getSnappedLocationDetailsUsingKdTree = GetSnappedLocationDetailsUsingKdTree(kdTreeManager),
    )
    val time = measureTimeMillis {
        val result = polylineEtaManager.onLocationUpdate(
            startLocation = PorterLatLong(0.0, 0.0),
            endLocation = PorterLatLong(5.0, 7.0),
            driverLocation = PorterLatLong(26.50100, 74.56425),
            crn = "123456"
        )
        println("Result: $result")
    }
    println("Total Time taken: $time ms")
    println("Ending Process")
}