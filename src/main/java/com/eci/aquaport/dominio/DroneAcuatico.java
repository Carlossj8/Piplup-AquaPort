package com.eci.aquaport.dominio;

public record DroneAcuatico(
    String id,
    String modelo,
    int bateria,       // 0-100%
    boolean disponible,
    String zona,       // "Embalse Norte", "Canal Central", etc.
    TipoDrone tipo,
    EstadoDrone estado
) {
    public DroneAcuatico(String id, String modelo, int bateria, boolean disponible, String zona, TipoDrone tipo) {
        this(id, modelo, bateria, disponible, zona, tipo, disponible ? EstadoDrone.DISPONIBLE : EstadoDrone.MANTENIMIENTO);
    }

    public DroneAcuatico(String id, String modelo, int bateria, boolean disponible, String zona) {
        this(id, modelo, bateria, disponible, zona, TipoDrone.SUPERFICIAL, disponible ? EstadoDrone.DISPONIBLE : EstadoDrone.MANTENIMIENTO);
    }
}
