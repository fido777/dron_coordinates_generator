# PRD: WebSocket-Based Random Coordinate Generator System

## Introduction/Overview

Implement a complete Spring Boot backend system that generates random drone detection coordinates within Colombian cities and broadcasts them in real-time via WebSocket to simulate an early warning system for the Colombian Air Force. This system will serve as the foundation for a mobile alert application that displays drone threats on a map interface.

The feature transforms the current empty Spring Boot application into a fully functional coordinate broadcasting system with configurable city parameters and scheduled coordinate generation.

## Goals

1. **Real-time Communication**: Establish WebSocket STOMP messaging for instant coordinate broadcasting to connected clients
2. **Configurable Simulation**: Create a flexible system supporting multiple Colombian cities with customizable coordinate ranges
3. **Scheduled Generation**: Implement automated coordinate generation at configurable intervals (default: 3 seconds)
4. **REST API Support**: Provide complementary REST endpoints for detection queries and health checks
5. **Production-Ready Architecture**: Build modular, testable components following Spring Boot best practices

## User Stories

1. **As a Kotlin Multiplaform developer using Ktor Client**, I want to connect to a WebSocket endpoint so that I can receive real-time drone coordinates for map visualization.

2. **As a system administrator**, I want to configure multiple cities with specific coordinate ranges so that the simulation covers relevant Colombian territories.

3. **As an Air Force operator**, I want the system to generate realistic drone detection alerts automatically so that training scenarios feel authentic.

5. **As a DevOps engineer**, I want health check endpoints so that I can monitor system status in production environments.

## Functional Requirements

1. **WebSocket Configuration**
   - The system must expose a WebSocket endpoint at `/ws`
   - The system must support STOMP protocol for message broadcasting
   - The system must broadcast coordinates to `/topic/coordinates` topic
   - The system must allow cross-origin connections for development

2. **Coordinate Generation**
   - The system must generate random latitude/longitude pairs within predefined city boundaries
   - The system must support at least 8 Colombian cities: Medellín, Bogotá, Cali, Barranquilla, Cartagena, Bucaramanga, Manizales, Pereira
   - The system must randomly select a different city for each coordinate generation
   - The system must include timestamp and unique ID for each coordinate

3. **Configuration Management**
   - The system must read city configurations from `application.yml`
   - The system must support configurable generation intervals (default: 3000ms)
   - The system must bind configuration using `@ConfigurationProperties`

4. **Scheduled Broadcasting**
   - The system must automatically generate and broadcast coordinates at regular intervals
   - The system must use Spring's `@Scheduled` annotation with configurable rate
   - The system must continue broadcasting while the application is running

5. **Data Models**
   - The system must define a `Coordinate` data class with latitude, longitude, timestamp, and ID
   - The system must define city configuration classes for YAML binding
   - The system must serialize coordinates to JSON format for WebSocket transmission

6. **REST API Endpoints**
   - The system must provide `GET /api/detections` for simulated detection list
   - The system must provide `GET /api/detections/{id}` for specific detection details
   - The system must provide `GET /api/health` for system status checking

7. **Project Structure**
   - The system must organize code into logical packages: config, controller, model, service
   - The system must follow Spring Boot conventions for component organization

## Non-Goals (Out of Scope)

- Real sensor/radar integration (simulation only)
- User authentication and authorization
- Database persistence for coordinates
- Mobile application development
- Production deployment configuration
- Load testing and performance optimization
- Advanced threat level algorithms
- Multi-language support

## Design Considerations

- **WebSocket Protocol**: Use Spring STOMP both with and without SockJS for broad client compatibility
- **Configuration Format**: YAML-based configuration for readability and maintainability
- **JSON Message Format**: Clean, simple coordinate objects for easy client consumption
- **Modular Architecture**: Separate concerns into distinct Spring components
- **Colombian Focus**: Predefined city boundaries matching Colombian geography
- The system should not support dynamic city configuration updates without restart
- Generated numbers in a city are within specified range

## Technical Considerations

- **Spring Boot 3.5.5** with Kotlin language support
- **JDK 17** minimum requirement
- **Spring WebSocket** with STOMP messaging broker
- **Jackson Kotlin** module for JSON serialization
- **Gradle Kotlin DSL** for build configuration
- **Configuration Properties** binding for type-safe configuration
- **Component Architecture**: Service classes for coordinate generation, controllers for REST API
- The system must include any basic logging for generated coordintes events

## Success Metrics

1. **Functionality**: WebSocket clients can successfully connect and receive coordinate broadcasts
2. **Performance**: System generates coordinates every 3 seconds without blocking
3. **Configuration**: All 8 Colombian cities generate coordinates within expected ranges
4. **API Availability**: REST endpoints respond with proper HTTP status codes
5. **Code Quality**: All components are unit testable with clear separation of concerns
