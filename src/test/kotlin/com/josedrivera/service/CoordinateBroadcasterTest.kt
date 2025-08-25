package com.josedrivera.service

import com.josedrivera.model.Coordinate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.messaging.simp.SimpMessagingTemplate
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import java.time.LocalDateTime

class CoordinateBroadcasterTest {

    private lateinit var coordinateBroadcaster: CoordinateBroadcaster
    private lateinit var coordinateGenerator: CoordinateGenerator
    private lateinit var simpMessagingTemplate: SimpMessagingTemplate

    @BeforeEach
    fun setup() {
        coordinateGenerator = mockk(relaxed = true)
        simpMessagingTemplate = mockk(relaxed = true)
        coordinateBroadcaster = CoordinateBroadcaster(coordinateGenerator, simpMessagingTemplate)
        
        every { simpMessagingTemplate.convertAndSend(any<String>(), any<Coordinate>()) } just Runs
    }

    @Test
    fun `should broadcast coordinate when generator returns coordinate`() {
        val testCoordinate = Coordinate(
            id = "dron-test123",
            city = "Medellín",
            latitude = 6.25,
            longitude = -75.55,
            timestamp = LocalDateTime.now(),
            threatLevel = "HIGH"
        )
        every { coordinateGenerator.generate() } returns testCoordinate

        coordinateBroadcaster.broadcast()

        verify { coordinateGenerator.generate() }
        verify { simpMessagingTemplate.convertAndSend("/topic/coordinates", testCoordinate) }
    }

    @Test
    fun `should not broadcast when generator returns null`() {
        every { coordinateGenerator.generate() } returns null

        coordinateBroadcaster.broadcast()

        verify { coordinateGenerator.generate() }
        verify(exactly = 0) { simpMessagingTemplate.convertAndSend(any<String>(), any<Coordinate>()) }
    }

    @Test
    fun `should call generator exactly once per broadcast call`() {
        val testCoordinate = Coordinate(
            id = "dron-test456",
            city = "Bogotá",
            latitude = 4.6,
            longitude = -74.1,
            timestamp = LocalDateTime.now(),
            threatLevel = "LOW"
        )
        every { coordinateGenerator.generate() } returns testCoordinate

        coordinateBroadcaster.broadcast()
        coordinateBroadcaster.broadcast()

        verify(exactly = 2) { coordinateGenerator.generate() }
        verify(exactly = 2) { simpMessagingTemplate.convertAndSend("/topic/coordinates", testCoordinate) }
    }

    @Test
    fun `should broadcast to correct topic`() {
        val testCoordinate = Coordinate(
            id = "dron-test789",
            city = "Cali",
            latitude = 3.4,
            longitude = -76.5,
            timestamp = LocalDateTime.now(),
            threatLevel = "MEDIUM"
        )
        every { coordinateGenerator.generate() } returns testCoordinate

        coordinateBroadcaster.broadcast()

        verify { simpMessagingTemplate.convertAndSend("/topic/coordinates", testCoordinate) }
    }
}