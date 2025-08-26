package com.josedrivera.config

import org.junit.jupiter.api.Test
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import org.junit.jupiter.api.BeforeEach

class WebSocketConfigTest {

    private lateinit var webSocketConfig: WebSocketConfig
    private lateinit var messageBrokerRegistry: MessageBrokerRegistry
    private lateinit var stompEndpointRegistry: StompEndpointRegistry

    @BeforeEach
    fun setup() {
        webSocketConfig = WebSocketConfig()
        messageBrokerRegistry = mockk(relaxed = true)
        stompEndpointRegistry = mockk(relaxed = true)
        
        every { stompEndpointRegistry.addEndpoint(any()) } returns mockk(relaxed = true)
    }

    @Test
    fun `should configure message broker with topic prefix and app destination`() {
        webSocketConfig.configureMessageBroker(messageBrokerRegistry)
        
        verify { messageBrokerRegistry.enableSimpleBroker("/topic") }
        verify { messageBrokerRegistry.setApplicationDestinationPrefixes("/app") }
    }

    @Test
    fun `should register STOMP endpoints with SockJS and CORS support`() {
        val mockEndpoint = mockk<org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration>(relaxed = true)
        every { stompEndpointRegistry.addEndpoint("/coordinates") } returns mockEndpoint
        every { mockEndpoint.setAllowedOriginPatterns("*") } returns mockEndpoint
        
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry)
        
        verify { stompEndpointRegistry.addEndpoint("/coordinates") }
        verify { mockEndpoint.setAllowedOriginPatterns("*") }
    }
}
