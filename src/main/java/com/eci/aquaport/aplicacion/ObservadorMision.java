package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

/**
 * Patrón Observer: Interfaz observadora para recibir notificaciones
 * de eventos del ciclo de vida de la misión y fallos de la flota.
 */
public interface ObservadorMision {

    void notificarFallo(Mision mision, String motivo);

    default void notificarFalloAsignacion(Mision mision) {
        notificarFallo(mision, "Fallo en la asignación de la misión: " + (mision != null ? mision.getId() : "N/A"));
    }

    void notificarAsignacionExitosa(Mision mision, DroneAcuatico drone);
}
