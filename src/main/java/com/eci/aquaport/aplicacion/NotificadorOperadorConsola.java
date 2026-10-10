package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.Mision;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificadorOperadorConsola implements NotificadorOperador {

    private static final Logger LOGGER = Logger.getLogger(NotificadorOperadorConsola.class.getName());

    @Override
    public void notificarMisionRegistrada(Mision mision) {
        if (mision != null) {
            LOGGER.log(Level.INFO, () -> String.format(
                    "[NOTIFICACIÓN OPERADOR] Misión registrada exitosamente: ID=%s | Drone=%s | Origen=%s -> Destino=%s",
                    mision.getId(), mision.getDrone().id(), mision.getPuntoPartida(), mision.getPuntoLlegada()));
        }
    }

    @Override
    public void notificarError(String mensaje) {
        LOGGER.log(Level.SEVERE, () -> "[ALERTA OPERADOR] Error en operación: " + mensaje);
    }
}
