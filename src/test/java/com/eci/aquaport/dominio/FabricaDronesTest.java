package com.eci.aquaport.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FabricaDronesTest {

    @Test
    @DisplayName("Creación exitosa de los tres tipos especializados")
    void crearDronesEspecializados_exito() {
        DroneAcuatico superficial = FabricaDrones.crearSuperficial("AR-01", 90, "Embalse Norte");
        DroneAcuatico semisumergido = FabricaDrones.crearSemisumergido("AR-02", 85, "Canal Central");
        DroneAcuatico buceador = FabricaDrones.crearBuceador("AR-03", 95, "Laguna Sur");

        assertEquals(TipoDrone.SUPERFICIAL, superficial.tipo());
        assertEquals("Aqua-Ranger Superficial", superficial.modelo());
        assertTrue(superficial.disponible());

        assertEquals(TipoDrone.SEMISUMERGIDO, semisumergido.tipo());
        assertEquals("Aqua-Sub Semisumergido", semisumergido.modelo());

        assertEquals(TipoDrone.BUCEADOR, buceador.tipo());
        assertEquals("Deep-Diver Buceador", buceador.modelo());
    }

    @Test
    @DisplayName("Tipo nulo lanza IllegalArgumentException")
    void crearDrone_tipoNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                FabricaDrones.crearDrone(null, "AR-99", "Modelo", 80, true, "Zona"));
        assertTrue(ex.getMessage().contains("tipo de drone"));
    }

    @Test
    @DisplayName("Id nulo o vacío lanza IllegalArgumentException")
    void crearDrone_idInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, null, "Modelo", 80, true, "Zona"));
        assertThrows(IllegalArgumentException.class, () ->
                FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "   ", "Modelo", 80, true, "Zona"));
    }
}
