package com.josedrivera.integration

import com.josedrivera.model.Coordinate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
class WebSocketIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var stompClient: StompSession

    @BeforeEach
    fun setup() {
        val webSocketClient = StandardWebSocketClient()
        val stompSession = WebSocketStompClient(webSocketClient)
        stompSession.messageConverter = MappingJackson2MessageConverter()
        
        val sessionHandler = object : StompSessionHandlerAdapter() {
            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                println("Connected to WebSocket in session $session and headers $connectedHeaders")
//                session.subscribe(
//                    "/topic/coordinates",
//                    object: StompFrameHandler {
//                        override fun getPayloadType(headers: StompHeaders): Type = Coordinate::class.java
//                        override fun handleFrame(
//                            headers: StompHeaders,
//                            payload: Any?
//                        ) {
//                            println("Received coordinate in session $session and headers $headers and payload $payload")
//                        }
//                    }
//                )
            }
        }
        
        stompClient = stompSession
            .connectAsync("ws://localhost:$port/coordinates", sessionHandler)
            .get(5, TimeUnit.SECONDS)
    }

    @Test
    fun `should receive coordinate broadcasts on topic coordinates`() {
        // TODO: this test does not pass no matter what I try, I keep not getting the receivedCoordinate atomic reference updated
        val latch = CountDownLatch(1)
        val receivedCoordinate = AtomicReference<Coordinate>()
        
        val subscription = stompClient.subscribe(
            "/topic/coordinates",
            object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type = Coordinate::class.java

                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    receivedCoordinate.set(payload as Coordinate)
                    latch.countDown()
                }
            }
        )

        println("Subscription: $subscription")

        // Wait for a coordinate to be broadcast (scheduled every 3 seconds)
        val received = latch.await(5, TimeUnit.SECONDS)

        assertTrue(received, "Should receive a coordinate broadcast within 5 seconds")

        val coordinate = receivedCoordinate.get()
        assertNotNull(coordinate)
        assertTrue(coordinate.id.startsWith("dron-"))
        assertNotNull(coordinate.city)
        assertTrue(coordinate.latitude != 0.0)
        assertTrue(coordinate.longitude != 0.0)
        assertNotNull(coordinate.timestamp)
        assertTrue(coordinate.threatLevel in listOf("LOW", "MEDIUM", "HIGH"))
        
        subscription.unsubscribe()
    }

    @Test
    fun `should broadcast coordinates from Colombian cities`() {
        val latch = CountDownLatch(1)
        val receivedCoordinate = AtomicReference<Coordinate>()
        val colombianCities = listOf(
            "Medellín", "Bogotá", "Cali", "Barranquilla", 
            "Cartagena", "Bucaramanga", "Manizales", "Pereira"
        )
        
        val subscription = stompClient.subscribe("/topic/coordinates", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = Coordinate::class.java
            
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                receivedCoordinate.set(payload as Coordinate)
                latch.countDown()
            }
        })

        val received = latch.await(5, TimeUnit.SECONDS)
        
        assertTrue(received, "Should receive a coordinate broadcast within 5 seconds")
        
        val coordinate = receivedCoordinate.get()
        assertTrue(colombianCities.contains(coordinate.city), 
            "Broadcast should contain coordinate from one of the Colombian cities")
        
        subscription.unsubscribe()
    }
}