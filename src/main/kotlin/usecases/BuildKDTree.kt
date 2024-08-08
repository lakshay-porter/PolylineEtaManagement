package usecases

import entities.RouteLeg
import entities.RouteLocation
import util.KDNode
import util.KDTreeManager

class BuildKDTree constructor(
    private val kdTreeManager: KDTreeManager
){
    suspend operator fun invoke(legs: List<RouteLeg>): KDNode {
        if (legs.isEmpty()) error("Legs cannot be empty")
        val routeLocations = mutableListOf<RouteLocation>()
        for (legIndex in legs.indices) {
            val leg = legs[legIndex]
            for (stepIndex in leg.steps.indices) {
                val step = leg.steps[stepIndex]
                val routeLocationsForStep = step.polylineList.map {
                    RouteLocation(
                        latlng = it,
                        stepIndex = stepIndex,
                        legIndex = legIndex
                    )
                }
                routeLocations.addAll(routeLocationsForStep)
            }
        }
        return kdTreeManager.buildTree(routeLocations) ?: error("Root node cannot be null")
    }
}