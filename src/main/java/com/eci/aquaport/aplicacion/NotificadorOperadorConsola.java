package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.Mision;

public class NotificadorOperadorConsola implements NotificadorOperador {

    @Override
    public void notificarMisionRegistrada(Mision mision) {
        System.out.println("[NOTIFICACIÓN OPERADOR] Misión registrada exitosamente: ID="
                + mision.getId() + " | Drone=" + mision.getDrone().id()
                + " | Origen=" + mision.getPuntoPartida()
                + " -> Destino=" + mision.getPuntoLlegada());
    }

    @Override
    public void notificarError(String mensaje) {
        System.err.println("[ALERTA OPERADOR] Error en operación: " + mensaje);
    }
}
