package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MayorBateriaStrategyTest {

    private final MayorBateriaStrategy estrategia = new MayorBateriaStrategy();

    @Test
    @DisplayName("Flota nula o vacía retorna Optional.empty()")
    void flotaNulaOVacia_retornaEmpty() {
        assertTrue(estrategia.seleccionar(null, null).isEmpty());
        assertTrue(estrategia.seleccionar(Collections.emptyList(), null).isEmpty());
    }

    @Test
    @DisplayName("Selecciona drone con mayor batería disponible que cumple umbral")
    void seleccionaMayorBateriaDisponible() {
        DroneAcuatico d1 = FabricaDrones.crearSuperficial("AR-01", 50, "Embalse Norte");
        DroneAcuatico d2 = FabricaDrones.crearSuperficial("AR-02", 92, "Canal Central");
        DroneAcuatico d3 = new DroneAcuatico("AR-03", "Aqua-Ranger", 99, false, "Laguna Sur");

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(List.of(d1, d2, d3), null);

        assertTrue(seleccionado.isPresent());
        assertEquals("AR-02", seleccionado.get().id());
        assertEquals(92, seleccionado.get().bateria());
    }
}
