package com.eci.aquaport.dominio;

public record DroneAcuatico(
    String id,
    String modelo,
    int bateria,       // 0-100%
    boolean disponible,
    String zona        // "Embalse Norte", "Canal Central", etc.
) {}
