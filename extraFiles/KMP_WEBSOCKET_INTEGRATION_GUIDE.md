# Kotlin Multiplatform WebSocket Integration Guide

## 📋 Overview

This guide provides step-by-step instructions for creating a Kotlin Multiplatform (KMP) application that connects to the drone detection server via WebSocket using STOMP protocol. The server broadcasts real-time drone coordinates from Colombian cities every `interval-ms` milliseconds (configured in the server currently as 10000)

## 🏗️ System Architecture

Based on the server architecture and existing test client, the system works as follows:

```
┌─────────────────┐    WebSocket/STOMP     ┌──────────────────┐
│   KMP Client    │◄──────────────────────►│  Spring Boot     │
│  (Android/iOS)  │   /coordinates         │     Server       │
│                 │   /topic/coordinates   │  (Kotlin/JVM)    │
└─────────────────┘                        └──────────────────┘
```

**Server Details:**
- **WebSocket Endpoint:** `ws://localhost:8080/coordinates` 
- **STOMP Topic:** `/topic/coordinates`
- **Message Broker:** Simple in-memory broker (`/topic` prefix)
- **Broadcast Interval:** Every `interval-ms` milliseconds
- **CORS:** Allows all origins (`*`)

## 🛠️ Prerequisites

### For Windows Development (Your Setup)
- **Kotlin Multiplatform Mobile Plugin** in Android Studio
- **JDK 17** (to match server requirements)  
- **Understanding of**: Coroutines, Serialization, STOMP protocol
- **Target platforms**: Android + shared code (iOS code can be written but not compiled)

### Additional Requirements for iOS Target
- **macOS machine** (for iOS compilation, simulator, and App Store deployment)
- **Xcode 15+** (latest stable version)
- **iOS Simulator** or physical device for testing

### Recommended Approach
1. **Develop on Windows**: Shared code + Android implementation
2. **Build iOS on macOS**: Use cloud Mac or separate machine when needed

## 📦 Project Setup

### 1. Create New KMP Project

```bash
# Using Android Studio: New Project → Kotlin Multiplatform App
# Or use the initializer
```

### 2. Project Structure for Compose Multiplatform

Update your project structure to use **Compose Multiplatform** instead of separate Android/iOS implementations:

```kotlin
// shared/build.gradle.kts - Updated for Compose Multiplatform
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                
                // WebSocket & Networking
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("io.ktor:ktor-client-websockets:2.3.7")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
                implementation("io.ktor:ktor-client-logging:2.3.7")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.7")
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation("androidx.lifecycle:lifecycle-compose:2.7.0")
            }
        }
        
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.7")
            }
        }
    }
}

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.21"
    id("com.android.library")
    id("org.jetbrains.compose") version "1.5.11"
}
```

### 3. Windows Development Workflow

Since you're on Windows 10, here's your recommended development approach:

**✅ What you can do on Windows:**
```bash
# Develop all shared code
shared/src/commonMain/kotlin/     # ✅ Business logic, WebSocket client
shared/src/commonMain/kotlin/ui/  # ✅ Compose UI (works for both platforms!)

# Test Android implementation
./gradlew :android:installDebug   # ✅ Build and install Android app
./gradlew :shared:testDebugUnitTest # ✅ Run unit tests
```

**❌ What requires macOS:**
```bash
# iOS-specific tasks (need macOS + Xcode)
./gradlew :shared:iosSimulatorArm64Test  # ❌ iOS tests
./gradlew :shared:linkDebugFrameworkIos  # ❌ iOS framework
# App Store deployment                   # ❌ iOS publication
```

### 4. Add Dependencies

Add to `shared/build.gradle.kts`:

```kotlin
kotlin {
    // ... existing configuration
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("io.ktor:ktor-client-websockets:2.3.7")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
                implementation("io.ktor:ktor-client-logging:2.3.7")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.7")
            }
        }
        
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.7")
            }
        }
    }
}

// Enable serialization
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.21"
}
```

## 📋 Data Models

### Server Data Structure

Based on the server's `Coordinate.kt` model, coordinates have this exact structure:

```kotlin
// Server model (for reference - don't include in KMP)
data class Coordinate(
    val id: String,           // e.g., "dron-2391"
    val city: String,         // Colombian city name
    val latitude: Double,     // Decimal degrees
    val longitude: Double,    // Decimal degrees  
    val timestamp: LocalDateTime,  // Server generation time
    val threatLevel: String   // "LOW", "MEDIUM", "HIGH"
)
```

### KMP Data Classes

Create `shared/src/commonMain/kotlin/models/DroneCoordinate.kt`:

```kotlin
package models

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class DroneCoordinate(
    val id: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime,
    val threatLevel: ThreatLevel
)

enum class ThreatLevel {
    LOW, MEDIUM, HIGH
}

// Colombian cities supported by the server
object ColombianCities {
    val SUPPORTED_CITIES = listOf(
        "Medellín", "Bogotá", "Cali", "Barranquilla", 
        "Cartagena", "Bucaramanga", "Manizales", "Pereira"
    )
}
```

## 🌐 WebSocket Client Implementation

### Base WebSocket Client

Create `shared/src/commonMain/kotlin/websocket/DroneWebSocketClient.kt`:

```kotlin
package websocket

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import models.DroneCoordinate

class DroneWebSocketClient(
    private val serverUrl: String = "ws://10.0.2.2:8080", // Android emulator default
    private val engine: HttpClientEngine? = null
) {
    private var client: HttpClient? = null
    private var webSocketSession: WebSocketSession? = null
    private var job: Job? = null
    
    // Shared flows for reactive programming
    private val _droneCoordinates = MutableSharedFlow<DroneCoordinate>(replay = 1)
    val droneCoordinates: SharedFlow<DroneCoordinate> = _droneCoordinates.asSharedFlow()
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED || 
            _connectionState.value == ConnectionState.CONNECTING) {
            return
        }
        
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                _connectionState.value = ConnectionState.CONNECTING
                
                client = HttpClient(engine ?: getDefaultEngine()) {
                    install(WebSockets) {
                        pingInterval = 30_000 // 30 seconds
                        maxFrameSize = Long.MAX_VALUE
                    }
                    install(ContentNegotiation) {
                        json(this@DroneWebSocketClient.json)
                    }
                    install(Logging) {
                        level = LogLevel.INFO
                    }
                }
                
                // Connect to the exact endpoint the server expects
                client?.webSocket(
                    urlString = "$serverUrl/coordinates"
                ) {
                    webSocketSession = this
                    _connectionState.value = ConnectionState.CONNECTED
                    
                    // Send STOMP CONNECT frame (matching server expectations)
                    sendStompConnect()
                    
                    // Subscribe to the exact topic the server uses
                    sendStompSubscribe("/topic/coordinates")
                    
                    // Listen for messages
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                handleStompMessage(frame.readText())
                            }
                            is Frame.Close -> {
                                _connectionState.value = ConnectionState.DISCONNECTED
                                _errorMessages.emit("Connection closed by server")
                                break
                            }
                            else -> { 
                                println("Received other frame type: ${frame::class.simpleName}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.ERROR
                _errorMessages.emit("Connection error: ${e.message}")
                println("WebSocket connection error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun WebSocketSession.sendStompConnect() {
        val connectFrame = buildStompFrame(
            command = "CONNECT",
            headers = mapOf(
                "accept-version" to "1.0,1.1,2.0",
                "heart-beat" to "10000,10000",
                "host" to "localhost"
            )
        )
        send(Frame.Text(connectFrame))
        println("📤 Sent STOMP CONNECT frame")
    }
    
    private suspend fun WebSocketSession.sendStompSubscribe(destination: String) {
        val subscribeFrame = buildStompFrame(
            command = "SUBSCRIBE",
            headers = mapOf(
                "id" to "sub-0",
                "destination" to destination
            )
        )
        send(Frame.Text(subscribeFrame))
        println("📤 Subscribed to $destination")
    }
    
    private fun buildStompFrame(
        command: String,
        headers: Map<String, String> = emptyMap(),
        body: String = ""
    ): String {
        val frameBuilder = StringBuilder()
        frameBuilder.append(command).append('\n')
        
        headers.forEach { (key, value) ->
            frameBuilder.append("$key:$value").append('\n')
        }
        
        frameBuilder.append('\n').append(body).append('\u0000')
        return frameBuilder.toString()
    }
    
    private suspend fun handleStompMessage(message: String) {
        try {
            val lines = message.split('\n')
            val command = lines.firstOrNull() ?: return
            
            println("📥 Received STOMP frame: $command")
            
            when (command) {
                "CONNECTED" -> {
                    println("✅ STOMP Connected successfully")
                }
                "MESSAGE" -> {
                    // Extract JSON body from STOMP MESSAGE frame
                    // The body starts after the first blank line (\n\n)
                    val bodyStartIndex = message.indexOf("\n\n") + 2
                    if (bodyStartIndex > 1) {
                        val jsonBody = message.substring(bodyStartIndex).trim('\u0000')
                        println("📦 Raw JSON: $jsonBody")
                        
                        try {
                            val coordinate = json.decodeFromString<DroneCoordinate>(jsonBody)
                            _droneCoordinates.emit(coordinate)
                            println("📡 Received coordinate: ${coordinate.city} (${coordinate.latitude}, ${coordinate.longitude}) - ${coordinate.threatLevel}")
                        } catch (e: Exception) {
                            println("❌ JSON parsing error: ${e.message}")
                            _errorMessages.emit("Failed to parse coordinate: ${e.message}")
                        }
                    }
                }
                "ERROR" -> {
                    val errorMsg = "STOMP Error received: $message"
                    println("❌ $errorMsg")
                    _errorMessages.emit(errorMsg)
                }
                "RECEIPT" -> {
                    println("📧 STOMP Receipt received")
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Error parsing STOMP message: ${e.message}"
            println("❌ $errorMsg")
            _errorMessages.emit(errorMsg)
        }
    }
    
    fun disconnect() {
        job?.cancel()
        webSocketSession?.cancel()
        client?.close()
        _connectionState.value = ConnectionState.DISCONNECTED
        println("🛑 WebSocket client disconnected")
    }

    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, ERROR
    }
}

// Platform-specific engine selection
expect fun getDefaultEngine(): HttpClientEngine
```

### Platform-Specific Implementations

**Android** - `shared/src/androidMain/kotlin/websocket/PlatformEngine.kt`:

```kotlin
package websocket

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun getDefaultEngine(): HttpClientEngine = OkHttp.create()
```

**iOS** - `shared/src/iosMain/kotlin/websocket/PlatformEngine.kt`:

```kotlin
package websocket

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun getDefaultEngine(): HttpClientEngine = Darwin.create()
```

## 📱 Compose Multiplatform UI Implementation

### Shared UI Code (Works on Both Android & iOS)

Create `shared/src/commonMain/kotlin/ui/DroneDetectorApp.kt`:

```kotlin
package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import models.DroneCoordinate
import websocket.DroneWebSocketClient

@Composable
fun DroneDetectorApp(client: DroneWebSocketClient) {
    val connectionState by client.connectionState.collectAsStateWithLifecycle()
    val latestCoordinate by client.droneCoordinates.collectAsStateWithLifecycle(initialValue = null)
    val errorMessage by client.errorMessages.collectAsStateWithLifecycle(initialValue = null)
    
    // Keep a list of recent coordinates
    var coordinatesList by remember { mutableStateOf<List<DroneCoordinate>>(emptyList()) }
    
    // Update coordinates list when new coordinate arrives
    LaunchedEffect(latestCoordinate) {
        latestCoordinate?.let { coord ->
            coordinatesList = (listOf(coord) + coordinatesList).take(50) // Keep last 50
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "🚁 Drone Detection System",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Connection controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Button(
                onClick = { client.connect() },
                enabled = connectionState != DroneWebSocketClient.ConnectionState.CONNECTED &&
                         connectionState != DroneWebSocketClient.ConnectionState.CONNECTING,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("🔗 Connect")
            }
            
            Button(
                onClick = { client.disconnect() },
                enabled = connectionState == DroneWebSocketClient.ConnectionState.CONNECTED,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("🔌 Disconnect")
            }
        }
        
        // Connection status with color coding
        val statusColor = when (connectionState) {
            DroneWebSocketClient.ConnectionState.CONNECTED -> Color(0xFF4CAF50)
            DroneWebSocketClient.ConnectionState.CONNECTING -> Color(0xFFFF9800)
            DroneWebSocketClient.ConnectionState.ERROR -> Color(0xFFE53935)
            DroneWebSocketClient.ConnectionState.DISCONNECTED -> Color.Gray
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
        ) {
            Text(
                text = "📡 Status: ${connectionState.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = statusColor,
                modifier = Modifier.padding(12.dp)
            )
        }
        
        // Error messages
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = "❌ $error",
                    color = Color(0xFFE53935),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Statistics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("📊 Total: ${coordinatesList.size}")
                Text("🏙️ Cities: ${coordinatesList.map { it.city }.distinct().size}")
                Text("⚠️ High Threat: ${coordinatesList.count { it.threatLevel == "HIGH" }}")
            }
        }
        
        // Coordinates list
        Text(
            "Recent Drone Detections",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LazyColumn {
            items(coordinatesList) { coordinate ->
                DroneCoordinateCard(coordinate)
            }
        }
    }
}

@Composable
fun DroneCoordinateCard(coordinate: DroneCoordinate) {
    val threatColor = when(coordinate.threatLevel) {
        "LOW" -> Color(0xFF4CAF50)
        "MEDIUM" -> Color(0xFFFF9800)
        "HIGH" -> Color(0xFFE53935)
        "CRITICAL" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = threatColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🚁 ${coordinate.id}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = coordinate.threatLevel,
                    color = threatColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(
                            color = threatColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text("📍 ${coordinate.city}")
            Text("🌐 ${String.format("%.4f", coordinate.latitude)}, ${String.format("%.4f", coordinate.longitude)}")
            Text("⏰ ${coordinate.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
        }
    }
}

@Composable
fun DroneDetectorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}
```

## 🍎 iOS Implementation

### ContentView.swift Integration

```swift
import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = DroneViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                // Title
                Text("🚁 Drone Detection System")
                    .font(.title)
                    .padding()
                
                // Connection Controls
                HStack(spacing: 12) {
                    Button("🔗 Connect") {
                        viewModel.connect()
                    }
                    .disabled(viewModel.isConnected || viewModel.isConnecting)
                    .padding()
                    .background(Color.green.opacity(0.1))
                    .cornerRadius(8)
                    
                    Button("🔌 Disconnect") {
                        viewModel.disconnect()
                    }
                    .disabled(!viewModel.isConnected)
                    .padding()
                    .background(Color.red.opacity(0.1))
                    .cornerRadius(8)
                }
                
                // Status Card
                VStack {
                    HStack {
                        Text("📡 Status:")
                        Text(viewModel.connectionStatus)
                            .foregroundColor(viewModel.statusColor)
                            .fontWeight(.semibold)
                        Spacer()
                    }
                    
                    if let error = viewModel.lastError {
                        HStack {
                            Text("❌ Error:")
                            Text(error)
                                .foregroundColor(.red)
                                .font(.caption)
                            Spacer()
                        }
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(8)
                
                // Statistics
                HStack {
                    VStack {
                        Text("📊")
                        Text("\(viewModel.coordinates.count)")
                            .font(.headline)
                        Text("Total")
                            .font(.caption)
                    }
                    
                    Spacer()
                    
                    VStack {
                        Text("🏙️")
                        Text("\(viewModel.uniqueCities.count)")
                            .font(.headline)
                        Text("Cities")
                            .font(.caption)
                    }
                    
                    Spacer()
                    
                    VStack {
                        Text("⚠️")
                        Text("\(viewModel.highThreatCount)")
                            .font(.headline)
                        Text("High Threat")
                            .font(.caption)
                    }
                }
                .padding()
                .background(Color.blue.opacity(0.1))
                .cornerRadius(8)
                
                Divider()
                
                // Drone coordinates list
                List(viewModel.coordinates, id: \.id) { coordinate in
                    DroneCoordinateRow(coordinate: coordinate)
                }
            }
            .padding()
            .navigationBarHidden(true)
        }
    }
}

struct DroneCoordinateRow: View {
    let coordinate: DroneCoordinate
    
    var threatColor: Color {
        switch coordinate.threatLevel {
        case "LOW": return .green
        case "MEDIUM": return .orange
        case "HIGH": return .red
        case "CRITICAL": return .purple
        default: return .gray
        }
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text("🚁 \(coordinate.id)")
                    .font(.headline)
                Spacer()
                Text(coordinate.threatLevel)
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(threatColor)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 2)
                    .background(threatColor.opacity(0.2))
                    .cornerRadius(4)
            }
            
            Text("📍 \(coordinate.city)")
                .font(.subheadline)
            Text("🌐 \(String(format: "%.4f", coordinate.latitude)), \(String(format: "%.4f", coordinate.longitude))")
                .font(.caption)
                .foregroundColor(.secondary)
            Text("⏰ \(coordinate.timestamp.description)")
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}

@MainActor
class DroneViewModel: ObservableObject {
    // Use localhost for iOS simulator
    private let client = DroneWebSocketClient(serverUrl: "ws://localhost:8080")
    
    @Published var coordinates: [DroneCoordinate] = []
    @Published var isConnected = false
    @Published var isConnecting = false
    @Published var connectionStatus = "Disconnected"
    @Published var lastError: String?
    
    var uniqueCities: Set<String> {
        Set(coordinates.map { $0.city })
    }
    
    var highThreatCount: Int {
        coordinates.filter { $0.threatLevel == "HIGH" || $0.threatLevel == "CRITICAL" }.count
    }
    
    var statusColor: Color {
        switch connectionStatus {
        case "CONNECTED": return .green
        case "CONNECTING": return .orange
        case "ERROR": return .red
        default: return .gray
        }
    }
    
    init() {
        setupObservers()
    }
    
    private func setupObservers() {
        // Observe connection state
        Task {
            for await state in client.connectionState.values {
                await MainActor.run {
                    self.connectionStatus = state.name
                    self.isConnected = (state == DroneWebSocketClient.ConnectionState.connected)
                    self.isConnecting = (state == DroneWebSocketClient.ConnectionState.connecting)
                }
            }
        }
        
        // Observe coordinates
        Task {
            for await coordinate in client.droneCoordinates.values {
                await MainActor.run {
                    self.coordinates.insert(coordinate, at: 0)
                    if self.coordinates.count > 50 {
                        self.coordinates = Array(self.coordinates.prefix(50))
                    }
                }
            }
        }
        
        // Observe errors
        Task {
            for await error in client.errorMessages.values {
                await MainActor.run {
                    self.lastError = error
                }
            }
        }
    }
    
    func connect() {
        lastError = nil
        client.connect()
    }
    
    func disconnect() {
        client.disconnect()
    }
}
```

## 🔧 Configuration & Testing

### Network Configuration

**Android**: Update `android/src/main/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">192.168.1.0/24</domain>
    </domain-config>
</network-security-config>
```

Add to `android/src/main/AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```

**iOS**: Update `Info.plist` for local development:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

### Server Connection URLs

| Platform | Environment | URL |
|----------|-------------|-----|
| Android Emulator | Development | `ws://10.0.2.2:8080` |
| Android Device | Development | `ws://YOUR_COMPUTER_IP:8080` |
| iOS Simulator | Development | `ws://localhost:8080` |
| iOS Device | Development | `ws://YOUR_COMPUTER_IP:8080` |
| Production | All | `wss://yourdomain.com` |

### Testing the Connection

1. **Start the server**: 
   ```bash
   ./gradlew bootRun
   ```

2. **Test with the included test client** (for verification):
   ```bash
   ./gradlew runWebSocketTestClient
   # Or run WebSocketTestClient.kt main function in IDE
   ```

3. **Verify server is working**:
   - Visit `http://localhost:8080/coordinates` in browser
   - Should see WebSocket connection attempt message
   - Test client should show incoming coordinates every 3 seconds

4. **Run your KMP app** and connect

### Expected Output Format

The test client shows exactly what your KMP client should receive:

```
[15:12:03] 🔴 HIGH THREAT
  🆔 ID: dron-2391  
  🏙️ City: Medellín
  📍 Coordinates: 6.2674, -75.5682
  ⏰ Generated: 2025-08-26T15:12:00
  📊 JSON: {"id":"dron-2391","city":"Medellín","latitude":6.2674,"longitude":-75.5682,"timestamp":"2025-08-26T15:12:00","threatLevel":"HIGH"}
```

## 🐛 Common Issues & Solutions

### Connection Issues

**Problem**: `Connection refused` 
- **Solution**: Ensure server is running on `http://localhost:8080`
- Use correct IP addresses per platform table above
- Check firewall settings

**Problem**: STOMP connection established but no messages received
- **Solution**: Verify subscription to exact topic `/topic/coordinates`
- Check server logs for coordinate generation (every `interval-ms` milliseconds)

**Problem**: JSON parsing errors
- **Solution**: Ensure `LocalDateTime` serialization matches server format
- Add kotlinx-datetime dependency
- Server sends ISO format: `2025-08-26T15:12:00`

**Problem**: WebSocket connects but STOMP frames malformed
- **Solution**: Ensure STOMP frames end with null byte (`\u0000`)
- Follow exact STOMP 1.2 specification format

### Performance Optimization

1. **Memory management**: Limit coordinate history (50-100 items)
2. **UI updates**: Use `collectAsStateWithLifecycle` in Compose
3. **Background processing**: Handle WebSocket on IO dispatcher
4. **Reconnection**: Implement exponential backoff for connection attempts

## 📚 Server Reference Architecture

The server architecture follows this pattern:

```kotlin
// Server Components (for reference)
@EnableWebSocketMessageBroker
class WebSocketConfig {
    // Enables simple broker on /topic prefix
    // STOMP endpoint at /coordinates
    // Allows all origins with setAllowedOriginPatterns("*")
}

@Service 
class CoordinateGenerator {
    // @Scheduled every ${drone.simulator.interval-ms} (3000ms)
    // Generates random coordinates in Colombian cities
    // Broadcasts via @Autowired SimpMessagingTemplate.convertAndSend()
}

data class Coordinate {
    // Exact server model structure
    // LocalDateTime timestamp format
    // threatLevel as String (not enum)
}
```

## 🎯 Next Steps

1. **Implement basic WebSocket connection** following this guide
2. **Add Google Maps integration** for Android - MapLibre (GL Native) SDK
3. **Add MapKit integration** for iOS (native MapKit)
4. **Implement push notifications** for high threat levels
5. **Add offline persistence** with SQLDelight
6. **Add comprehensive error handling**
7. **Performance testing** with continuous coordinate streams

## 📖 Additional Resources

- [Ktor Client WebSocket Documentation](https://ktor.io/docs/websocket-client.html)
- [STOMP Protocol Specification 1.2](https://stomp.github.io/stomp-specification-1.2.html)
- [Spring WebSocket STOMP Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Server Test Client Reference](../src/test/kotlin/com/josedrivera/client/WebSocketTestClient.kt)

---

**Note**: This integration guide is designed specifically for the Colombian Air Force drone detection simulation server. All coordinates are randomly generated within Colombian city boundaries for academic and testing purposes.