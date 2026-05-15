package com.builder.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val icon: String // Could be a resource name or URL
)

val predefinedCategories = listOf(
    Category("plomeria", "Plomería", "plumbing"),
    Category("electricidad", "Electricidad", "electric"),
    Category("limpieza", "Limpieza", "cleaning_services"),
    Category("pintura", "Pintura", "format_paint"),
    Category("carpinteria", "Carpintería", "handyman"),
    Category("jardineria", "Jardinería", "yard"),
    Category("albanileria", "Albañilería", "construction"),
    Category("climatizacion", "Aire Acondicionado", "ac_unit")
)
