package com.eci.aquaport.dominio;

import java.util.Objects;

/**
 * Decorador abstracto base que delega el comportamiento al DroneOperativo decorado.
 */
public abstract class DecoradorDrone implements DroneOperativo {

    protected final DroneOperativo decorado;

    protected DecoradorDrone(DroneOperativo decorado) {
        this.decorado = Objects.requireNonNull(decorado, "El drone decorado no puede ser nulo.");
    }

    @Override
    public String id() {
        return decorado.id();
    }

    @Override
    public int bateria() {
        return decorado.bateria();
    }

    @Override
    public String zona() {
        return decorado.zona();
    }

    @Override
    public TipoDrone tipo() {
        return decorado.tipo();
    }

    @Override
    public EstadoDrone estado() {
        return decorado.estado();
    }

    @Override
    public String transmitirTelemetria() {
        return decorado.transmitirTelemetria();
    }
}
