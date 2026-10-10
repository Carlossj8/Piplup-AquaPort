package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CentroControlObserver implements ObservadorMision {

    private static final Logger LOGGER = Logger.getLogger(CentroControlObserver.class.getName());

    @Override
    public void notificarFallo(Mision mision, String motivo) {
        LOGGER.log(Level.WARNING, () -> String.format(
                "[CENTRO DE CONTROL] Alerta en misión ID=%s: %s",
                mision != null ? mision.getId() : "N/A", motivo));
    }

    @Override
    public void notificarAsignacionExitosa(Mision mision, DroneAcuatico drone) {
        LOGGER.log(Level.INFO, () -> String.format(
                "[CENTRO DE CONTROL] Asignación confirmada: Misión ID=%s -> Drone %s (%s)",
                mision.getId(), drone.id(), drone.tipo()));
    }
}
