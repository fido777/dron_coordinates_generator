package com.josedrivera.controller

import com.josedrivera.model.Coordinate
import com.josedrivera.service.CoordinateGenerator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class DetectionController(
    private val coordinateGenerator: CoordinateGenerator
) {

    // In-memory storage for demo purposes - in real app this would be a database
    private val detectionHistory = mutableMapOf<String, Coordinate>()

    @GetMapping("/detections")
    fun getDetections(): List<Coordinate> {
        // Generate some mock historical detections using the real generator
        if (detectionHistory.isEmpty()) {
            repeat(10) {
                val coordinate = coordinateGenerator.generate()
                coordinate?.let { 
                    detectionHistory[it.id] = it 
                }
            }
        }
        return detectionHistory.values.toList()
    }

    @GetMapping("/detections/{id}")
    fun getDetectionById(@PathVariable id: String): ResponseEntity<Coordinate> {
        // Check if detection exists in history
        val detection = detectionHistory[id]
        return if (detection != null) {
            ResponseEntity.ok(detection)
        } else {
            // For demo purposes, generate a detection if ID follows the pattern
            if (id.startsWith("dron-") && id != "dron-nonexistent") {
                val mockDetection = coordinateGenerator.generate()
                mockDetection?.let { 
                    val customDetection = it.copy(id = id)
                    detectionHistory[id] = customDetection
                    ResponseEntity.ok(customDetection)
                } ?: ResponseEntity.notFound().build()
            } else {
                ResponseEntity.notFound().build()
            }
        }
    }

    @GetMapping("/health")
    fun getHealth(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now(),
            "service" to "Drone Coordinates Generator"
        )
    }
}