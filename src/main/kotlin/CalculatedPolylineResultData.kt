data class CalculatedPolylineResultData constructor(
    val snappedLocation: SnappedLocation,
    val nextPointInPolylineData: NextLocationOnPolyLine,
    val deviation: Double,
    val legIndex: Int,
    val stepIndex: Int,
) {
    override fun toString(): String {
        return "CalculatedPolylineResultData(" +
                "\n\t\tsnappedLocation = $snappedLocation, " +
                "\n\t\tnextPointInPolylineData = $nextPointInPolylineData, " +
                "\n\t\tdeviation = $deviation" +
                "\n\t\tlegIndex = $legIndex" +
                "\n\t\tstepIndex = $stepIndex" +
                "\n)"
    }

    companion object {

        val defaultData = CalculatedPolylineResultData(
            snappedLocation = SnappedLocation(0.0, 0.0),
            nextPointInPolylineData = NextLocationOnPolyLine(0.0, 0.0),
            deviation = -1.0,
            legIndex = -1,
            stepIndex = -1,
        )
    }
}