package com.garth.locationsice

data class Response(
    val type: String,
    val features: List<Feature>
)

data class Feature (
    val type: String,
    val properties: Properties
)

data class Properties(
    val name: String,
    val address_line2: String
)

