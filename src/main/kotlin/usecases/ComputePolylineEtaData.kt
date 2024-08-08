package usecases

import CalculatedPolylineResultData
import entities.PolylineData
import entities.RouteLeg
import entities.TrimmedDurationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComputePolylineEtaData {
    suspend operator fun invoke(
        polylineData: PolylineData,
        snappedLocationDetails: CalculatedPolylineResultData,
        durationResult: TrimmedDurationResult,
    ): PolylineData = withContext(Dispatchers.Default) {
        val remainingLegs = polylineData.legs.drop(snappedLocationDetails.legIndex)
        val remainingSteps = remainingLegs.first().steps.drop(snappedLocationDetails.stepIndex + 1)
        val calculatedLegs = mutableListOf<RouteLeg>()
        val trafficMultiplier = remainingLegs.first().durationInTraffic.toFloat() / remainingLegs.first().duration


        calculatedLegs.add(
            RouteLeg(
                steps = remainingSteps,
                distance = remainingSteps.sumOf { it.distance },
                duration = remainingSteps.sumOf { it.duration },
                durationInTraffic = remainingSteps.sumOf { (trafficMultiplier * it.duration).toLong() },
            ),
        )
        if (remainingLegs.size > 1)
            calculatedLegs.addAll(remainingLegs.drop(1))

        PolylineData(
            legs = calculatedLegs,
            distance = calculatedLegs.sumOf { it.distance }.toInt(),
            duration = durationResult.duration,
            durationInTraffic = durationResult.durationInTraffic,
            tree = polylineData.tree
        )
    }
}
