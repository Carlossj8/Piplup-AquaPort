package com.eci.aquaport.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Decorador concreto que añade registro histórico de telemetría a cualquier drone.
 */
public class DroneConMonitoreo extends DecoradorDrone {

    private final List<String> registrosTelemetria = new ArrayList<>();

    public DroneConMonitoreo(DroneOperativo decorado) {
        super(decorado);
    }

    @Override
    public String transmitirTelemetria() {
        String telemetria = super.transmitirTelemetria();
        registrosTelemetria.add(telemetria);
        return telemetria;
    }

    public List<String> getRegistrosTelemetria() {
        return Collections.unmodifiableList(registrosTelemetria);
    }

    public int getCantidadTransmisiones() {
        return registrosTelemetria.size();
    }
}
