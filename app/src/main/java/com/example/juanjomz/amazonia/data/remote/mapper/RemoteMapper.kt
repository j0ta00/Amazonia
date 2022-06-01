package com.example.juanjomz.amazonia.data.remote.mapper

import com.example.juanjomz.amazonia.data.remote.model.PlantDTO
import com.example.juanjomz.amazonia.data.remote.model.SpecieDTO
import com.example.juanjomz.amazonia.domain.PlantBO

fun PlantBO.toDTO(): SpecieDTO = SpecieDTO(
    scientificName,
    commonName,
    family
)