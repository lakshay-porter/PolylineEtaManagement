package usecases

import entities.PolylineData
import entities.PolylineEtaResponse

class BuildPolylineData constructor(private val buildKDTree: BuildKDTree) {
    suspend operator fun invoke(polylineEtaResponse: PolylineEtaResponse): PolylineData {
        val kdTree = buildKDTree(polylineEtaResponse.legs)
        return PolylineData(
            legs = polylineEtaResponse.legs,
            distance = polylineEtaResponse.legs.sumOf { it.distance.toInt() },
            duration = polylineEtaResponse.legs.sumOf { it.duration },
            durationInTraffic = polylineEtaResponse.legs.sumOf { it.durationInTraffic },
            tree = kdTree
        )
    }
}