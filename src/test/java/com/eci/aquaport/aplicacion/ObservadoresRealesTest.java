package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ObservadoresRealesTest {

    @Test
    @DisplayName("CentroControlObserver registra fallos y asignaciones exitosas sin lanzar excepciones")
    void centroControlObserver_notificaCorrectamente() {
        CentroControlObserver observer = new CentroControlObserver();
        DroneAcuatico drone = FabricaDrones.crearSuperficial("AR-01", 90, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-10").drone(drone)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).prioridad(Prioridad.CRITICA).build();

        assertDoesNotThrow(() -> observer.notificarAsignacionExitosa(mision, drone));
        assertDoesNotThrow(() -> observer.notificarFallo(mision, "Batería insuficiente"));
        assertDoesNotThrow(() -> observer.notificarFallo(null, "Alerta general"));
        assertDoesNotThrow(() -> observer.notificarFalloAsignacion(mision));
    }

    @Test
    @DisplayName("TecnicoMantenimientoObserver registra alertas técnicas sin lanzar excepciones")
    void tecnicoMantenimientoObserver_notificaCorrectamente() {
        TecnicoMantenimientoObserver observer = new TecnicoMantenimientoObserver();
        DroneAcuatico drone = FabricaDrones.crearSuperficial("AR-02", 40, "Canal Central");
        Mision mision = new Mision.Builder()
                .id("M-20").drone(drone)
                .puntoPartida("Canal Central").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).prioridad(Prioridad.NORMAL).build();

        assertDoesNotThrow(() -> observer.notificarAsignacionExitosa(mision, drone));
        assertDoesNotThrow(() -> observer.notificarFallo(mision, "Fallo de rotor"));
        assertDoesNotThrow(() -> observer.notificarFallo(null, "Avería global"));
    }
}
