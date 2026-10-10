package com.eci.aquaport.dominio;

public record DroneAcuatico(
    String id,
    String modelo,
    int bateria,       // 0-100%
    boolean disponible,
    String zona,       // "Embalse Norte", "Canal Central", etc.
    TipoDrone tipo
) {
    public DroneAcuatico(String id, String modelo, int bateria, boolean disponible, String zona) {
        this(id, modelo, bateria, disponible, zona, TipoDrone.SUPERFICIAL);
    }
}
