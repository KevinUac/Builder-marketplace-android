<h1 align="center">Builder App 🛠️</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF.svg?style=flat&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=flat&logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Firebase-Backend-FFCA28.svg?style=flat&logo=firebase" alt="Firebase">
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853.svg?style=flat" alt="MVVM">
  <img src="https://img.shields.io/badge/Dagger%20Hilt-DI-black.svg?style=flat&logo=dagger" alt="Hilt">
</p>

<p align="center">
  <strong>Marketplace Premium de Servicios del Hogar para Android</strong><br>
  Construido con Kotlin, Jetpack Compose y Firebase bajo principios de Clean Architecture.
</p>

---

## 📱 Visión General

**Builder** es una aplicación nativa moderna para Android diseñada para conectar a clientes que requieren servicios domésticos (plomería, electricidad, limpieza, etc.) con profesionales independientes capacitados. 

El proyecto destaca por su sistema de **doble rol** integrado en una sola aplicación, donde la experiencia de usuario (UX) y la interfaz (UI) se adaptan dinámicamente dependiendo de si el usuario ha iniciado sesión como **Cliente** o como **Proveedor**.

---

## ✨ Características Principales y Últimas Mejoras

### 🎨 Nuevo Sistema de Diseño Minimalista ("White Theme")
La aplicación ha transicionado de un diseño oscuro a un **Minimalismo Premium Blanco**.
- **Branding Consistente:** Uso del logotipo oficial corporativo en Splash, Login y Home.
- **Paleta de Colores:** Interfaz inmaculada blanca (`Color.White`) con grises neutros para textos secundarios y un color de acento principal Naranja (`#FB9833`) que otorga gran modernidad y profesionalismo.
- **Jerarquía y Limpieza:** Eliminación de contenedores y "círculos grises" residuales, maximizando el espacio en blanco y utilizando `BottomSheets` (modales inferiores) en lugar de formularios invasivos.

### 🔄 Flujo de Registro y Onboarding Mejorados
- **Campos en un solo paso:** Los proveedores ahora introducen su Teléfono y Fecha de Nacimiento directamente en el registro inicial.
- **Manejo de Formularios Nativos:** Implementación de `VisualTransformation` nativo de Compose para formatear automáticamente Teléfonos (`XXX XXX XXXX`) y Fechas (`DD/MM/AAAA`) sin interrumpir la escritura ni romper el cursor.
- **Persistencia Segura (Merges):** El onboarding inteligente detecta datos incompletos (como profesión o tarifa) obligando al proveedor a completarlos, pero **preservando (merge)** su foto y datos originales sin sobreescribir la base de datos de Firebase.

### 👷‍♂️ Perfil del Proveedor Avanzado
- **Edad Dinámica:** La aplicación extrae el año de la fecha de nacimiento y calcula automáticamente la edad del proveedor, mostrándola de forma elegante (ej. "65 Años") justo debajo de su nombre.
- **Sistema Estricto de Interacciones (Likes):** Un algoritmo transaccional con `arrayUnion` y `arrayRemove` en Firestore asegura que **un usuario solo pueda dar 1 Like o 1 Dislike** por proveedor. Si un usuario que había dado Dislike cambia a Like, el sistema resta uno y suma el otro atómicamente.
- **Micro-interacciones y UX Premium:** El botón de "Contratar" ahora ejecuta una **Vibración Táctil (Haptic Feedback)** nativa, proporcionando una sensación física de confirmación al usuario.

### ⚙️ Configuración de Usuario Rediseñada
El perfil de usuario fue reconstruido desde cero siguiendo guías de diseño modernas de iOS y Android.
- **Avatar Premium:** Foto de perfil de mayor resolución (120dp) con botón flotante discreto de edición.
- **Modales Modernos:** Se reemplazaron los campos de edición en línea por elegantes `ModalBottomSheet` para modificar nombres, contraseñas o fotos de perfil, manteniendo la pantalla principal completamente limpia.

---

## 🏗️ Arquitectura y Tecnologías

El proyecto sigue estrictamente el patrón **MVVM** (Model-View-ViewModel) estructurado bajo los principios de **Clean Architecture** para garantizar escalabilidad, testabilidad y separación de responsabilidades.

### Stack Tecnológico:
- **Lenguaje:** Kotlin.
- **UI:** Jetpack Compose (100% declarativo).
- **Inyección de Dependencias (DI):** Dagger Hilt.
- **Asincronía & Reactividad:** Coroutines + Kotlin Flow (`StateFlow`, `SharedFlow`).
- **Navegación:** Type-Safe Compose Navigation (Serialización).
- **Imágenes:** Coil (Carga optimizada con caché).

### Backend (Firebase BaaS):
- **Firebase Auth:** Autenticación segura de usuarios.
- **Cloud Firestore:** Base de datos NoSQL con operaciones atómicas (`Batch Writes`, `arrayUnion`).
- **Cloud Storage:** Alojamiento asíncrono de fotos de perfil y portafolios.

### Estructura de Capas (Clean Architecture):
```text
app/src/main/java/com/builder/app/
 ├── core/          # Componentes transversales: Tema, Utils, Modulos Hilt, Navigation.
 ├── data/          # Repositorios, mapeo de datos, orígenes de datos (Firebase).
 ├── domain/        # Modelos de negocio, Interfaces.
 └── presentation/  # UI Layer: Pantallas Compose, ViewModels, Manejo de estado (UiState).
```

---

## ⚙️ Configuración del Entorno Local

Para ejecutar este proyecto en tu máquina local:

1. Clona el repositorio:
   ```bash
   git clone https://github.com/KevinUac/Builder-marketplace-android.git
   ```
2. Abre el directorio raíz en **Android Studio**.
3. **Configuración de Firebase:**
   - Deberás proveer tu propio archivo `google-services.json` y colocarlo en `app/google-services.json`.
4. **Permisos y Google Maps:**
   - Configura tu API Key de Google Maps en `local.properties` para renderizar los mapas correctamente.
5. Ejecuta la aplicación en un emulador (API 24+) o dispositivo físico.

---

<p align="center">
  <i>Diseñado y Desarrollado por <b>KevinUac</b>.</i>
</p>
