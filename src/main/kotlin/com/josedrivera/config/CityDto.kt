package com.josedrivera.config

data class CityDto(
    val name: String,
    val latRange: List<Double>,
    val lonRange: List<Double>
) {
    init {
        require(latRange.size == 2) { "latRange must contain exactly 2 values" }
        require(lonRange.size == 2) { "lonRange must contain exactly 2 values" }
        require(latRange[0] <= latRange[1]) { "latRange must be ordered: min ≤ max" }
        require(lonRange[0] <= lonRange[1]) { "lonRange must be ordered: min ≤ max" }
    }

    // Optional: Convenience properties to get the ranges as pairs
    val latitudePair: Pair<Double, Double>
        get() = Pair(latRange[0], latRange[1])
    
    val longitudePair: Pair<Double, Double>
        get() = Pair(lonRange[0], lonRange[1])
}
