package com.josedrivera.model

import java.time.LocalDateTime

data class Coordinate(
    val id: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime,
    val threatLevel: String
)

enum class ThreatLevel { LOW, MEDIUM, HIGH }