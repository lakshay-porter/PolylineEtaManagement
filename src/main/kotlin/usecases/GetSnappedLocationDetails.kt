package usecases

import CalculatedPolylineResultData
import NextLocationOnPolyLine
import PorterLatLong
import SnappedLocation
import entities.PolylineEtaDetails
import getHaversineDistance
import getLinearDistance
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.system.measureTimeMillis

class GetSnappedLocationDetails {

    suspend operator fun invoke(
        driverLocation: PorterLatLong,
        polylineEtaDetails: PolylineEtaDetails,
    ): CalculatedPolylineResultData = withContext(Default) {
        var minDistance = Double.MAX_VALUE
        var result = CalculatedPolylineResultData.defaultData
        val time = measureTimeMillis {
            for (legIndex in 0 until polylineEtaDetails.legs.size) {
                val leg = polylineEtaDetails.legs[legIndex]
                for (stepIndex in 0 until leg.steps.size) {
                    val step = leg.steps[stepIndex]
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
                                legIndex = legIndex,
                                stepIndex = stepIndex
                            )
                        }
                    }
                }
            }
        }
        println("Time taken for snapping: $time")
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