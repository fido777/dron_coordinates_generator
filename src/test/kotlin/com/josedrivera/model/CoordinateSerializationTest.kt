package com.josedrivera.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime

class CoordinateSerializationTest {

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper().registerKotlinModule()
        objectMapper.findAndRegisterModules()
    }

    @Test
    fun `should serialize coordinate to JSON with proper field names`() {
        val coordinate = Coordinate(
            id = "dron-test123",
            city = "Medellín",
            latitude = 6.25,
            longitude = -75.55,
            timestamp = LocalDateTime.of(2025, 8, 25, 15, 30, 0),
            threatLevel = "HIGH"
        )

        val json = objectMapper.writeValueAsString(coordinate)

        assertTrue(json.contains("\"id\":\"dron-test123\""))
        assertTrue(json.contains("\"city\":\"Medellín\""))
        assertTrue(json.contains("\"latitude\":6.25"))
        assertTrue(json.contains("\"longitude\":-75.55"))
        assertTrue(json.contains("\"threatLevel\":\"HIGH\""))
        assertTrue(json.contains("\"timestamp\""))
    }

    @Test
    fun `should deserialize JSON to coordinate with proper field values`() {
        val json = """
            {
                "id": "dron-test456",
                "city": "Bogotá",
                "latitude": 4.6,
                "longitude": -74.1,
                "timestamp": "2025-08-25T15:30:00",
                "threatLevel": "MEDIUM"
            }
        """.trimIndent()

        val coordinate = objectMapper.readValue(json, Coordinate::class.java)

        assertEquals("dron-test456", coordinate.id)
        assertEquals("Bogotá", coordinate.city)
        assertEquals(4.6, coordinate.latitude)
        assertEquals(-74.1, coordinate.longitude)
        assertEquals("MEDIUM", coordinate.threatLevel)
        assertEquals(LocalDateTime.of(2025, 8, 25, 15, 30, 0), coordinate.timestamp)
    }

    @Test
    fun `should handle all threat levels in JSON`() {
        val threatLevels = listOf("LOW", "MEDIUM", "HIGH")
        
        threatLevels.forEach { level ->
            val coordinate = Coordinate(
                id = "dron-$level",
                city = "Cali", 
                latitude = 3.4,
                longitude = -76.5,
                timestamp = LocalDateTime.now(),
                threatLevel = level
            )
            
            val json = objectMapper.writeValueAsString(coordinate)
            val deserialized = objectMapper.readValue(json, Coordinate::class.java)
            
            assertEquals(level, deserialized.threatLevel)
        }
    }

    @Test
    fun `should preserve coordinate precision in JSON serialization`() {
        val coordinate = Coordinate(
            id = "dron-precision",
            city = "Cartagena",
            latitude = 10.391049,
            longitude = -75.479426,
            timestamp = LocalDateTime.now(),
            threatLevel = "LOW"
        )

        val json = objectMapper.writeValueAsString(coordinate)
        val deserialized = objectMapper.readValue(json, Coordinate::class.java)

        assertEquals(coordinate.latitude, deserialized.latitude, 0.000001)
        assertEquals(coordinate.longitude, deserialized.longitude, 0.000001)
    }

    @Test
    fun `should match expected WebSocket message format from IMPLEMENTATION_IDEAS`() {
        val coordinate = Coordinate(
            id = "dron-2391",
            city = "Medellín", 
            latitude = 6.2674,
            longitude = -75.5682,
            timestamp = LocalDateTime.of(2025, 8, 23, 15, 12, 0),
            threatLevel = "HIGH"
        )

        val json = objectMapper.writeValueAsString(coordinate)
        
        // Verify it matches the format specified in IMPLEMENTATION_IDEAS.md
        assertTrue(json.contains("\"id\":\"dron-2391\""))
        assertTrue(json.contains("\"latitude\":6.2674"))
        assertTrue(json.contains("\"longitude\":-75.5682"))
        assertTrue(json.contains("\"threatLevel\":\"HIGH\""))
        assertTrue(json.contains("\"timestamp\""))
        
        // Verify all required fields are present
        val deserialized = objectMapper.readValue(json, Coordinate::class.java)
        assertNotNull(deserialized.id)
        assertNotNull(deserialized.city)
        assertNotNull(deserialized.latitude)
        assertNotNull(deserialized.longitude)
        assertNotNull(deserialized.timestamp)
        assertNotNull(deserialized.threatLevel)
    }
}