package usecases

import CalculatedPolylineResultData
import NextLocationOnPolyLine
import PorterLatLong
import SnappedLocation
import entities.PolylineData
import entities.RouteLocation
import getHaversineDistance
import getLinearDistance
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import util.KDTreeManager
import kotlin.math.pow

class GetSnappedLocationDetailsUsingKdTree constructor(private val kdTreeManager: KDTreeManager) {

    suspend operator fun invoke(
        polylineData: PolylineData,
        driverLocation: PorterLatLong,
    ): CalculatedPolylineResultData = withContext(Default) {
        val driverRouteLocation = RouteLocation(driverLocation, -1, -1)
        val routeLocation = kdTreeManager.nearestLocation(polylineData.tree, driverRouteLocation)
            ?: error("Could not find route location for driver location: $driverLocation")
        var minDistance = Double.MAX_VALUE
        var result = CalculatedPolylineResultData.defaultData
        val step = polylineData.legs[routeLocation.legIndex].steps[routeLocation.stepIndex]

        for (i in 0 until step.polylineList.size - 1) {
            val start = step.polylineList[i]
            val end = step.polylineList[i + 1]
            val (closestPoint, nextPoint) = computeClosestAndNextLocation(driverLocation, start, end)
            val distance = driverLocation.getLinearDistance(closestPoint)
            if (distance < minDistance) {
                minDistance = distance
                result = CalculatedPolylineResultData(
                    snappedLocation = closestPoint,
                    nextPointInPolylineData = nextPoint,
                    deviation = distance,
                    legIndex = routeLocation.legIndex,
                    stepIndex = routeLocation.stepIndex
                )
            }
        }

        val haversineDistance = driverLocation.getHaversineDistance(result.snappedLocation)
        result.copy(deviation = haversineDistance)
    }

    private fun computeClosestAndNextLocation(
        point: PorterLatLong,
        start: PorterLatLong,
        end: PorterLatLong,
    ): Pair<SnappedLocation, NextLocationOnPolyLine> {
        val lengthSquared = (end.lat - start.lat).pow(2) + (end.lng - start.lng).pow(2)
        if (lengthSquared == 0.0) return start to start

        val t =
            ((point.lat - start.lat) * (end.lat - start.lat)
                    + (point.lng - start.lng) * (end.lng - start.lng)) / lengthSquared

        return when {
            t < 0 -> start to start
            t > 1 -> end to end
            else -> PorterLatLong(
                lat = start.lat + t * (end.lat - start.lat),
                lng = start.lng + t * (end.lng - start.lng)
            ) to end
        }
    }

}