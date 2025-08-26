package com.josedrivera.controller

import com.josedrivera.model.Coordinate
import com.josedrivera.service.CoordinateGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every

@WebMvcTest(DetectionController::class)
class DetectionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var coordinateGenerator: CoordinateGenerator

    @BeforeEach
    fun setup() {
        val mockCoordinate = Coordinate(
            id = "dron-test123",
            city = "Medellín",
            latitude = 6.2674,
            longitude = -75.5682,
            timestamp = LocalDateTime.now(),
            threatLevel = "HIGH"
        )

        every { coordinateGenerator.generate() } returns mockCoordinate
    }

    @Test
    fun `should return list of detections`() {
        mockMvc.perform(get("/api/detections"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].city").exists())
            .andExpect(jsonPath("$[0].latitude").exists())
            .andExpect(jsonPath("$[0].longitude").exists())
            .andExpect(jsonPath("$[0].timestamp").exists())
            .andExpect(jsonPath("$[0].threatLevel").exists())
    }

    @Test
    fun `should return specific detection by id`() {
        val testId = "dron-test123"

        mockMvc.perform(get("/api/detections/{id}", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.city").exists())
            .andExpect(jsonPath("$.latitude").exists())
            .andExpect(jsonPath("$.longitude").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.threatLevel").exists())
    }

    @Test
    fun `should return not found for non-existent detection id`() {
        val nonExistentId = "dron-nonexistent"

        mockMvc.perform(get("/api/detections/{id}", nonExistentId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return health status`() {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.service").value("Drone Coordinates Generator"))
    }

    @Test
    fun `should return valid coordinate format in detections list`() {
        mockMvc.perform(get("/api/detections"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].threatLevel").value(org.hamcrest.Matchers.oneOf("LOW", "MEDIUM", "HIGH")))
            .andExpect(jsonPath("$[0].latitude").isNumber)
            .andExpect(jsonPath("$[0].longitude").isNumber)
    }

    @Test
    fun `should return Colombian cities in detection data`() {
        mockMvc.perform(get("/api/detections"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].city").value("Medellín"))
    }
}
