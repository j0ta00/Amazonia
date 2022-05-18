package com.example.juanjomz.amazonia.data.remote.mapper

import com.example.juanjomz.amazonia.data.remote.model.PlantDTO
import com.example.juanjomz.amazonia.domain.PlantBO

fun PlantDTO.toBO() = PlantBO(
    scientificName ?: "",
    commonName ?: "",
            family?: ""
)