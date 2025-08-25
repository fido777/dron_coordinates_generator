package com.josedrivera.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Coordinate(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("city")
    val city: String,

    @field:JsonProperty("latitude")
    val latitude: Double,

    @field:JsonProperty("longitude")
    val longitude: Double,

    @field:JsonProperty("timestamp")
    val timestamp: LocalDateTime,

    @field:JsonProperty("threatLevel")
    val threatLevel: String
)
