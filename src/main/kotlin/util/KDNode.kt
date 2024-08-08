package util

import entities.RouteLocation

class KDNode(val location: RouteLocation, val axis: Int) {
    var left: KDNode? = null
    var right: KDNode? = null
}
