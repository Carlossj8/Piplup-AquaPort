package com.eci.aquaport.dominio;

import java.util.Objects;

/**
 * Representa un punto de control intermedio (waypoint) en rutas hídricas multi-etapa.
 */
public record Waypoint(String id, String zona) {

    public Waypoint {
        Objects.requireNonNull(id, "El identificador del waypoint no puede ser nulo.");
        Objects.requireNonNull(zona, "La zona del waypoint no puede ser nula.");
    }

    public Waypoint(String zona) {
        this(zona, zona);
    }

    public String getZona() {
        return zona;
    }

    public String getId() {
        return id;
    }
}
