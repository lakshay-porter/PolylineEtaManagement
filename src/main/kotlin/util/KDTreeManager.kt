package util

import entities.RouteLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class KDTreeManager constructor() {
    private val k = 2

    // Function to build the KD-Tree
    suspend fun buildTree(locations: List<RouteLocation>): KDNode? =
        withContext(Dispatchers.Default) { buildTree(locations, 0) }


    private fun buildTree(locations: List<RouteLocation>, depth: Int): KDNode? {
        if (locations.isEmpty()) return null

        val axis = depth % k
        val sortedLocations = locations.sortedBy { if (axis == 0) it.latlng.lat else it.latlng.lng }
        val median = sortedLocations.size / 2

        val node = KDNode(sortedLocations[median], axis)
        node.left = buildTree(sortedLocations.subList(0, median), depth + 1)
        node.right = buildTree(sortedLocations.subList(median + 1, sortedLocations.size), depth + 1)

        return node
    }

    // Function to find the nearest neighbor
    fun nearestLocation(root: KDNode, target: RouteLocation): RouteLocation? {
        return nearest(root, target, Double.MAX_VALUE)?.location
    }

    private fun nearest(node: KDNode?, target: RouteLocation, bestDistance: Double): KDNode? {
        if (node == null) return null

        var bestNode = node
        var bestDist = bestDistance

        val d = distance(node.location, target)
        if (d < bestDist) {
            bestDist = d
            bestNode = node
        }

        val axis = node.axis
        val diff =
            if (axis == 0) target.latlng.lat - node.location.latlng.lat else target.latlng.lng - node.location.latlng.lng
        val (nearerNode, furtherNode) = if (diff < 0) Pair(node.left, node.right) else Pair(node.right, node.left)

        bestNode = nearest(nearerNode, target, bestDist) ?: bestNode
        bestDist = if (bestNode == node) {
            d
        } else {
            distance(bestNode.location, target)
        }

        if (abs(diff) < bestDist) {
            bestNode = nearest(furtherNode, target, bestDist) ?: bestNode
        }

        return bestNode
    }

    private fun distance(loc1: RouteLocation, loc2: RouteLocation): Double {
        return sqrt((loc1.latlng.lat - loc2.latlng.lat).pow(2) + (loc1.latlng.lng - loc2.latlng.lng).pow(2))
    }
}
