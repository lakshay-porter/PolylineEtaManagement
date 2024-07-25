package usecases

import CalculatedPolylineResultData
import entities.PolylineData
import entities.TrimmedDurationResult
import getHaversineDistance
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext

class GetRemainingDuration {
    suspend operator fun invoke(
        polylineData: PolylineData,
        snappedLocationDetails: CalculatedPolylineResultData,
    ): TrimmedDurationResult = withContext(Default) {
        var duration = 0L
        var durationInTraffic = 0L

        val currentLeg = polylineData.legs[snappedLocationDetails.legIndex]
        val currentStep = currentLeg.steps[snappedLocationDetails.stepIndex]
        val trafficMultiplier = currentLeg.durationInTraffic.toFloat() / currentLeg.duration

        //add remaining duration in the current step
        val index = currentStep.polylineList.indexOf(snappedLocationDetails.snappedLocation)

        if (index != -1 && index + 1 < currentStep.polylineList.size) {
            val remainingPolyline = currentStep.polylineList.subList(index, currentStep.polylineList.size)
            var remainingDistance = 0.0
            for (i in 0 until remainingPolyline.size - 1) {
                remainingDistance += remainingPolyline[i].getHaversineDistance(remainingPolyline[i + 1])
            }
            val remainingDuration =
                ((remainingDistance / currentStep.distance.toFloat()) * currentStep.duration).toLong()
            val remainingDurationInTraffic = (remainingDuration * trafficMultiplier).toLong()
            duration += remainingDuration
            durationInTraffic += remainingDurationInTraffic
        }

        //sum of remaining steps in the current leg
        if (snappedLocationDetails.stepIndex + 1 < currentLeg.steps.size)
            for (i in snappedLocationDetails.stepIndex + 1 until currentLeg.steps.size) {
                duration += currentLeg.steps[i].duration
                durationInTraffic += (currentLeg.steps[i].duration * trafficMultiplier).toLong()
            }

        //sum of remaining legs
        if (snappedLocationDetails.legIndex + 1 < polylineData.legs.size)
            for (i in snappedLocationDetails.legIndex + 1 until polylineData.legs.size) {
                duration += polylineData.legs[i].duration
                durationInTraffic += polylineData.legs[i].durationInTraffic
            }
        TrimmedDurationResult(duration = duration, durationInTraffic = durationInTraffic)
    }


}