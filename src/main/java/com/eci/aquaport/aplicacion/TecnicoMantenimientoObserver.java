package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TecnicoMantenimientoObserver implements ObservadorMision {

    private static final Logger LOGGER = Logger.getLogger(TecnicoMantenimientoObserver.class.getName());

    @Override
    public void notificarFallo(Mision mision, String motivo) {
        LOGGER.log(Level.SEVERE, () -> String.format(
                "[TÉCNICO MANTENIMIENTO] Atención requerida para misión ID=%s: %s",
                mision != null ? mision.getId() : "N/A", motivo));
    }

    @Override
    public void notificarAsignacionExitosa(Mision mision, DroneAcuatico drone) {
        // El técnico monitorea sin interrumpir asignaciones exitosas
    }
}
