package com.josedrivera.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.josedrivera.model.Coordinate
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Standalone WebSocket test client to connect to the drone coordinates WebSocket server
 * and display real-time coordinate broadcasts.
 * 
 * Usage: Run this after starting the main application
 */
class WebSocketTestClient {
    
    private val objectMapper = ObjectMapper().also {
        it.registerKotlinModule()
        it.findAndRegisterModules()
    }
    
    fun connectAndListen(serverUrl: String = "ws://localhost:8080/coordinates") {
        println("🚁 Drone Coordinates WebSocket Test Client")
        println("=" .repeat(50))
        println("Connecting to: $serverUrl")
        println("Topic: /topic/coordinates")
        println("=" .repeat(50))
        
        val webSocketClient = StandardWebSocketClient()
        val stompClient = WebSocketStompClient(webSocketClient)
        val messageConverter = MappingJackson2MessageConverter()
        messageConverter.objectMapper = objectMapper
        stompClient.messageConverter = messageConverter
        
        val connectionLatch = CountDownLatch(1)
        
        val sessionHandler = object : StompSessionHandlerAdapter() {
            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                println("✅ Connected to WebSocket server successfully!")
                println("Session ID: ${session.sessionId}")
                println("Waiting for coordinate broadcasts...")
                println("-".repeat(50))
                connectionLatch.countDown()
            }
            
            override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
                println("❌ WebSocket error: ${exception.message}")
                exception.printStackTrace()
            }
        }
        
        try {
            val session = stompClient.connectAsync(serverUrl, sessionHandler)
                .get(10, TimeUnit.SECONDS)
            
            // Wait for connection to be established
            connectionLatch.await(5, TimeUnit.SECONDS)
            
            // Subscribe to coordinate broadcasts
            val subscription = session.subscribe("/topic/coordinates", object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type = Coordinate::class.java
                
                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    val coordinate = payload as Coordinate
                    displayCoordinate(coordinate)
                }
            })
            
            println("📡 Subscribed to /topic/coordinates")
            println("🔄 Listening for broadcasts (press Ctrl+C to stop)...")
            println()
            
            // Keep the client running indefinitely
            val shutdownLatch = CountDownLatch(1)
            
            // Add shutdown hook for graceful cleanup
            Runtime.getRuntime().addShutdownHook(Thread {
                println("\n🛑 Shutting down WebSocket client...")
                subscription.unsubscribe()
                session.disconnect()
                shutdownLatch.countDown()
            })
            
            // Wait indefinitely (until Ctrl+C)
            shutdownLatch.await()
            
        } catch (e: Exception) {
            println("❌ Failed to connect: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun displayCoordinate(coordinate: Coordinate) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val threatEmoji = when(coordinate.threatLevel) {
            "LOW" -> "🟢"
            "MEDIUM" -> "🟡" 
            "HIGH" -> "🔴"
            else -> "⚪"
        }
        
        println("[$timestamp] $threatEmoji ${coordinate.threatLevel} THREAT")
        println("  🆔 ID: ${coordinate.id}")
        println("  🏙️  City: ${coordinate.city}")
        println("  📍 Coordinates: ${coordinate.latitude}, ${coordinate.longitude}")
        println("  ⏰ Generated: ${coordinate.timestamp}")
        println("  📊 JSON: ${objectMapper.writeValueAsString(coordinate)}")
        println("-".repeat(70))
    }
}

fun main(args: Array<String>) {
    val serverUrl = if (args.isNotEmpty()) args[0] else "ws://localhost:8080/coordinates"
    
    val client = WebSocketTestClient()
    client.connectAndListen(serverUrl)
}