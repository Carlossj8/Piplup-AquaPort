package com.eci.aquaport.dominio;

import java.util.Objects;

/**
 * Componente concreto del patrón Decorator que encapsula un DroneAcuatico base.
 */
public class DroneBaseOperativo implements DroneOperativo {

    private final DroneAcuatico drone;

    public DroneBaseOperativo(DroneAcuatico drone) {
        this.drone = Objects.requireNonNull(drone, "El drone no puede ser nulo.");
    }

    @Override
    public String id() {
        return drone.id();
    }

    @Override
    public int bateria() {
        return drone.bateria();
    }

    @Override
    public String zona() {
        return drone.zona();
    }

    @Override
    public TipoDrone tipo() {
        return drone.tipo();
    }

    @Override
    public EstadoDrone estado() {
        return drone.estado();
    }

    @Override
    public String transmitirTelemetria() {
        return String.format("ID=%s;BAT=%d%%;ZONA=%s;TIPO=%s",
                drone.id(), drone.bateria(), drone.zona(), drone.tipo());
    }

    public DroneAcuatico getDrone() {
        return drone;
    }
}
