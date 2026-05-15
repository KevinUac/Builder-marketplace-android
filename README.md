<h1 align="center">Builder App 🛠️</h1>

<p align="center">
  <strong>Marketplace Premium de Servicios del Hogar para Android</strong><br>
  Construido con Kotlin, Jetpack Compose y Firebase bajo principios de Clean Architecture.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF.svg?style=flat&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=flat&logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Firebase-Backend-FFCA28.svg?style=flat&logo=firebase" alt="Firebase">
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853.svg?style=flat" alt="MVVM">
  <img src="https://img.shields.io/badge/Dagger%20Hilt-DI-black.svg?style=flat&logo=dagger" alt="Hilt">
</p>

---

## 📱 Visión General

**Builder** es una aplicación nativa moderna para Android diseñada para conectar a clientes que requieren servicios domésticos (plomería, electricidad, limpieza, etc.) con profesionales independientes capacitados. 

El proyecto destaca por su sistema de **doble rol** integrado en una sola aplicación, donde la experiencia de usuario (UX) y la interfaz (UI) se adaptan dinámicamente dependiendo de si el usuario ha iniciado sesión como **Cliente** o como **Proveedor**.

---

## ✨ Características Principales

### 🔄 Sistema de Roles Dinámico
La aplicación maneja flujos lógicos y de UI completamente distintos basados en el tipo de usuario autenticado:
- **Flujo de Cliente:** Exploración de categorías, visualización de perfiles en lista y mapa interactivo, envío de solicitudes de servicio y calificación de proveedores.
- **Flujo de Proveedor:** Dashboard de solicitudes entrantes, gestión de portafolio visual, captura automática de ubicación (GPS) y herramientas de respuesta rápida (Aceptar/Rechazar servicios).

### 💬 Comunicación en Tiempo Real
- Sistema de mensajería (Chat) integrado entre el cliente y el proveedor.
- Sincronización instantánea utilizando `StateFlow` y listeners de Firestore.

### 🗺️ Búsqueda Geoespacial
- Integración nativa de **Google Maps SDK** para Compose.
- Visualización de proveedores cercanos con marcadores personalizados.
- Manejo seguro de permisos de ubicación y trazabilidad de coordenadas GPS.

### 📊 Gestión de Estados de Servicio
- Historial detallado del ciclo de vida de un servicio: `Pendiente` ➔ `En Progreso` ➔ `Completado` o `Cancelado`.
- Estados visuales en tiempo real para ambas partes (ej. Feedback animado de *"Esperando respuesta..."* para el cliente).

---

## 🚶‍♂️ User Journeys (Cómo funciona la App)

Builder está dividida internamente en dos aplicaciones que conviven bajo el mismo código, dependiendo del tipo de cuenta con la que te registres.

### 👤 Experiencia del Cliente (Usuario)
1. **Descubrimiento:** Al iniciar sesión, el cliente es recibido por un *Dashboard* con categorías de servicios (Plomería, Limpieza, etc.) y una lista horizontal de proveedores recomendados y cercanos.
2. **Búsqueda en Mapa:** Si el cliente necesita alguien urgente, puede abrir el **Mapa Interactivo**. El sistema detecta su ubicación y le muestra pines con los proveedores disponibles a su alrededor en tiempo real.
3. **Contratación:** Al seleccionar a un proveedor, el cliente ve su perfil detallado, tarifa por hora, portafolio de fotos y reseñas. Puede enviar una **Solicitud de Servicio** con una descripción del problema. Un overlay animado le confirmará que la solicitud fue enviada.
4. **Seguimiento:** En la pestaña de *Historial*, el cliente verá su servicio como *"Pendiente"*. Cuando el proveedor acepte, el estado cambiará automáticamente a *"En Progreso"*.
5. **Comunicación:** El cliente puede abrir el **Chat** integrado para enviar mensajes directos al proveedor, negociar precios o enviar detalles extra antes de que llegue a su domicilio.

### 👷‍♂️ Experiencia del Proveedor
1. **Onboarding:** Al registrarse, el proveedor pasa por una pantalla especial para "Completar su Perfil". Aquí define su tarifa, sube fotos de sus trabajos anteriores, e interactivamente **otorga permisos de GPS** para que la app capture sus coordenadas exactas y lo posicione en el mapa de los clientes.
2. **Recepción de Solicitudes:** Su pantalla principal es un panel de control. No necesita buscar clientes; él simplemente recibe las solicitudes entrantes.
3. **Gestión Operativa:** En su *Historial*, verá las solicitudes nuevas. Tendrá botones prominentes de **Aceptar** o **Rechazar**. 
4. **Ejecución:** Si acepta, se le habilita la opción de chatear con el cliente. Una vez que termina el trabajo físico en la vida real, el proveedor presiona **"Marcar Completado"** en la app, cerrando el ciclo del servicio.

---

## 🎨 UI/UX Design System

La aplicación se construyó siguiendo un enfoque de diseño **Minimalista Premium (Dark Mode First)**, alejándose de las interfaces genéricas para ofrecer una experiencia de nivel *flagship*.

- **Tema "Premium Dark":** Paleta de colores curada con fondos azules oscuros (`#0B0E14`), superficies elevadas sutiles (`#151A22`) y acentos de color vibrantes (`#3B82F6` para primarios, `#10B981` para éxito).
- **Tipografía:** Jerarquía visual estricta basada en pesos tipográficos, mejorando la legibilidad sin sacrificar la estética moderna.
- **Micro-interacciones:** Animaciones fluidas en navegaciones (Fade, ScaleIn) y feedback visual animado en acciones clave (ej. overlay de "Solicitud Enviada" con checkmark animado).
- **Componentes Custom:** Implementación de un sistema de diseño propio (`BuilderComponents.kt`) que sobreescribe Material 3 para garantizar consistencia absoluta en botones, text fields, cards y modales.

---

## 🏗️ Arquitectura y Tecnologías

El proyecto sigue estrictamente el patrón **MVVM** (Model-View-ViewModel) estructurado bajo los principios de **Clean Architecture** para garantizar escalabilidad, testabilidad y separación de responsabilidades.

### Stack Tecnológico:
- **Lenguaje:** Kotlin.
- **UI:** Jetpack Compose (100% declarativo, sin XML layouts).
- **Inyección de Dependencias (DI):** Dagger Hilt.
- **Asincronía & Reactividad:** Coroutines + Kotlin Flow (`StateFlow`, `SharedFlow`).
- **Navegación:** Type-Safe Compose Navigation (Serialización).
- **Imágenes:** Coil (para carga de imágenes desde URLs/Firebase Storage).

### Backend (Firebase BaaS):
- **Firebase Auth:** Autenticación segura de usuarios mediante correo y contraseña.
- **Cloud Firestore:** Base de datos NoSQL para almacenamiento reactivo de perfiles, historiales de servicios y chats.
- **Cloud Storage:** Alojamiento de assets visuales (fotos de perfil y portafolios de trabajo).

### Estructura de Capas (Clean Architecture):
```text
app/src/main/java/com/builder/app/
 ├── core/          # Componentes transversales: UI Theme, Utils, DI Modules, Navegación.
 ├── data/          # Implementación de repositorios, mapeo de datos, Data Sources (Firebase).
 ├── domain/        # Modelos de negocio, Interfaces de Repositorios, Casos de Uso (UseCases).
 └── presentation/  # UI Layer: Composables, ViewModels, Gestión de estados (UiState).
```

---

## ⚙️ Configuración del Entorno Local

Para ejecutar este proyecto en tu máquina local:

1. Clona el repositorio:
   ```bash
   git clone https://github.com/KevinUac/Builder-marketplace-android.git
   ```
2. Abre el directorio raíz en **Android Studio** (Koala o superior recomendado).
3. **Configuración de Firebase:**
   - Deberás proveer tu propio archivo `google-services.json`.
   - Coloca el archivo en la ruta: `app/google-services.json`.
   - Asegúrate de habilitar **Authentication** (Email/Password), **Firestore** y **Storage** en tu consola de Firebase.
4. **Permisos y Google Maps:**
   - La API Key de Google Maps debe configurarse en tu entorno local o en `local.properties` para que el mapa renderice correctamente.
5. Selecciona un emulador (API 24+) o dispositivo físico y ejecuta la aplicación (Shift + F10).

---

<p align="center">
  <i>Diseñado y Desarrollado por <b>KevinUac</b>.</i>
</p>
