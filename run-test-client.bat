@echo off
echo Starting WebSocket Test Client...
echo Make sure the main application is running on localhost:8080
echo.
pause
./gradlew test --tests "com.josedrivera.client.WebSocketTestClient" -Dtest.single=WebSocketTestClient