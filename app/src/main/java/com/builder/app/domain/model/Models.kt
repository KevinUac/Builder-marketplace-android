package com.builder.app.domain.model

enum class RolUsuario { CLIENTE, PROVEEDOR }

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: RolUsuario = RolUsuario.CLIENTE,
    val fcmToken: String? = null,
    val fotoUrl: String = ""
)

enum class EstadoServicio { PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO }

data class Servicio(
    val id: String = "",
    val idCliente: String = "",
    val idProveedor: String = "",
    val nombreProveedor: String = "",
    val nombreCliente: String = "",
    val categoria: String = "",
    val estado: EstadoServicio = EstadoServicio.PENDIENTE,
    val fecha: Long = System.currentTimeMillis(),
    val precioEstimado: Double = 0.0,
    val descripcion: String = ""
)

data class Proveedor(
    val uid: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val calificacion: Float = 0f,
    val totalResenas: Int = 0,
    val tarifaHora: Double = 0.0,
    val habilidades: List<String> = emptyList(),
    val portafolioUrls: List<String> = emptyList(),
    val verificado: Boolean = false,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val geohash: String? = null,
    val fotoUrl: String = "",
    val likedBy: List<String> = emptyList(),
    val dislikedBy: List<String> = emptyList(),
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val anosExperiencia: Int = 0
)

data class Mensaje(
    val id: String = "",
    val idRemitente: String = "",
    val texto: String = "",
    val tipo: String = "text",      // "text", "image", "location"
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class Chat(
    val id: String = "",
    val participantes: List<String> = emptyList(),
    val nombresParticipantes: Map<String, String> = emptyMap(),
    val ultimoMensaje: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Resena(
    val id: String = "",
    val nombreCliente: String = "",
    val calificacion: Float = 0f,
    val comentario: String = "",
    val timestamp: Long = 0L
)
