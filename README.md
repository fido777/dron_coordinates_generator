# 🛰️ Backend - Sistema Móvil de Alerta Temprana para Detección de Drones

**Generador de Coordenadas Aleatorias con Kotlin Spring Boot + WebSocket**

Este proyecto forma parte de un sistema de alerta temprana desarrollado para la Fuerza Aérea Colombiana, cuyo objetivo es simular detecciones de drones no autorizados mediante coordenadas generadas periódicamente y transmitidas en tiempo real a una aplicación móvil.

## 🚀 Descripción

El backend implementado en **Spring Boot (Kotlin)** simula un sistema de detección de drones generando coordenadas geográficas aleatorias dentro de varias ciudades de Colombia. Cada coordenada representa una supuesta detección de dron y es transmitida al cliente a través de un canal **WebSocket en tiempo real**, permitiendo su visualización inmediata.

Este backend también incluye:

- API REST básica para consultas simuladas de detecciones
- Generador configurable de coordenadas aleatorias
- Transmisión WebSocket de alertas en tiempo real
- Configuración modular y preparada para despliegue en producción

---

## 🛠️ Tecnologías utilizadas

- 🧠 **Kotlin**
- 🌱 **Spring Boot 3**
- 🔌 **WebSocket (Spring Messaging + STOMP)**
- 🧪 **JUnit + MockK** para pruebas unitarias
- 📦 **Gradle Kotlin DSL**

---

## 📡 Endpoints principales

### WebSocket (STOMP)

- **URL de conexión:** `ws://localhost:8080/ws`
- **Topic de suscripción:** `/topic/drones`

### REST API

| Método | Endpoint               | Descripción                                 |
| ------ | ---------------------- | ------------------------------------------- |
| GET    | `/api/detections`      | Obtener lista simulada de detecciones       |
| GET    | `/api/detections/{id}` | Obtener detalle de una detección específica |
| GET    | `/api/health`          | Verificar estado del backend                |

---

## ▶️ Cómo ejecutar

### Requisitos previos

- JDK 17+
- Gradle (o usar wrapper ./gradlew)

### Pasos

```bash
# 1. Clona el repositorio
git clone git@github-fido777:fido777/dron_coordinates_generator.git
cd dron_coordinates_generator

# 2. Ejecuta la aplicación
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

---

## 📱 Casos de uso

Este backend está diseñado para integrarse con una aplicación frontend (móvil o web) que:

- Se conecta vía WebSocket para recibir alertas de detección
- Muestra en tiempo real los drones sobre un mapa
- Notifica al usuario sobre amenazas según nivel de riesgo

---

## 🔐 Seguridad

Este proyecto no expone datos reales por razones de confidencialidad. Toda la información generada es simulada, y el sistema puede ser adaptado a un entorno productivo real mediante integración con sensores o radares reales.

---

## 📃 Licencia

Este proyecto ha sido desarrollado con fines académicos e institucionales. Su distribución y uso está sujeta a autorización de la Fuerza Aérea Colombiana.

---

## 👨‍💻 Autor

**Nombre:** José Daniel Rivera Vergara  
**Rol:** Practicante de Ingeniería de Telecomunicaciones  
**Institución:** Universidad de Antioquia  
**Contacto:** josed.rivera@udea.edu.co  
