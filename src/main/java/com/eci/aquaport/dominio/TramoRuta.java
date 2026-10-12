package com.eci.aquaport.dominio;

import java.util.Objects;

/**
 * Representa la asignación de un drone específico para cubrir un tramo o waypoint
 * en una ruta multi-etapa Enterprise.
 */
public record TramoRuta(Waypoint waypoint, DroneAcuatico drone) {

    public TramoRuta {
        Objects.requireNonNull(waypoint, "El waypoint del tramo no puede ser nulo.");
        Objects.requireNonNull(drone, "El drone asignado al tramo no puede ser nulo.");
    }
}
