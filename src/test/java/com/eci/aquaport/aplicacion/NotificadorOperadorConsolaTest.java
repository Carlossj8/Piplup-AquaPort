package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.TipoCarga;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificadorOperadorConsolaTest {

    @Test
    @DisplayName("Notificar misión registrada emite log sin lanzar excepciones")
    void notificarMisionRegistrada_exito() {
        NotificadorOperadorConsola notificador = new NotificadorOperadorConsola();
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-301")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        assertDoesNotThrow(() -> notificador.notificarMisionRegistrada(mision));
        assertDoesNotThrow(() -> notificador.notificarMisionRegistrada(null));
    }

    @Test
    @DisplayName("Notificar error emite log de alerta sin lanzar excepciones")
    void notificarError_exito() {
        NotificadorOperadorConsola notificador = new NotificadorOperadorConsola();
        assertDoesNotThrow(() -> notificador.notificarError("Batería insuficiente"));
    }
}
