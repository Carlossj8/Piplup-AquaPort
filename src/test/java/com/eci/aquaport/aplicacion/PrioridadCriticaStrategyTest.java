package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrioridadCriticaStrategyTest {

    private PrioridadCriticaStrategy estrategia;
    private DroneAcuatico droneEmbalseNorteAlto;
    private DroneAcuatico droneEmbalseNorteMedio;
    private DroneAcuatico droneCanalCentralMayor;
    private DroneAcuatico droneBateriaBaja;

    @BeforeEach
    void setUp() {
        estrategia = new PrioridadCriticaStrategy();

        droneEmbalseNorteAlto = FabricaDrones.crearSuperficial("AR-01", 80, "Embalse Norte");
        droneEmbalseNorteMedio = FabricaDrones.crearSuperficial("AR-02", 60, "Embalse Norte");
        droneCanalCentralMayor = FabricaDrones.crearSuperficial("AR-03", 95, "Canal Central");
        droneBateriaBaja = FabricaDrones.crearSuperficial("AR-04", 20, "Embalse Norte");
    }

    @Test
    @DisplayName("Flota nula o vacía retorna Optional.empty()")
    void flotaNulaOVacia_retornaEmpty() {
        Mision mision = new Mision.Builder()
                .id("M-1").drone(droneEmbalseNorteAlto)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).prioridad(Prioridad.CRITICA).build();

        assertTrue(estrategia.seleccionar(null, mision).isEmpty());
        assertTrue(estrategia.seleccionar(Collections.emptyList(), mision).isEmpty());
    }

    @Test
    @DisplayName("Misión crítica selecciona drone con mayor batería en la misma zona de origen")
    void misionCritica_seleccionaDroneEnMismaZona() {
        Mision misionCritica = new Mision.Builder()
                .id("M-CRIT-10").drone(droneEmbalseNorteAlto)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).prioridad(Prioridad.CRITICA).build();

        List<DroneAcuatico> flota = List.of(droneEmbalseNorteMedio, droneEmbalseNorteAlto, droneCanalCentralMayor, droneBateriaBaja);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, misionCritica);

        assertTrue(seleccionado.isPresent());
        assertEquals("AR-01", seleccionado.get().id());
        assertEquals("Embalse Norte", seleccionado.get().zona());
    }

    @Test
    @DisplayName("Misión crítica sin drones disponibles en zona de origen recurre al de mayor batería general")
    void misionCritica_sinDronesEnZona_recurreAMayorBateriaGeneral() {
        Mision misionCriticaLaguna = new Mision.Builder()
                .id("M-CRIT-20").drone(droneEmbalseNorteAlto)
                .puntoPartida("Laguna Sur").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).prioridad(Prioridad.CRITICA).build();

        List<DroneAcuatico> flota = List.of(droneEmbalseNorteMedio, droneCanalCentralMayor);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, misionCriticaLaguna);

        assertTrue(seleccionado.isPresent());
        assertEquals("AR-03", seleccionado.get().id());
        assertEquals(95, seleccionado.get().bateria());
    }

    @Test
    @DisplayName("Misión normal selecciona drone con mayor batería independientemente de la zona")
    void misionNormal_seleccionaMayorBateriaGeneral() {
        Mision misionNormal = new Mision.Builder()
                .id("M-NORM-1").drone(droneEmbalseNorteAlto)
                .puntoPartida("Embalse Norte").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).prioridad(Prioridad.NORMAL).build();

        List<DroneAcuatico> flota = List.of(droneEmbalseNorteAlto, droneCanalCentralMayor);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, misionNormal);

        assertTrue(seleccionado.isPresent());
        assertEquals("AR-03", seleccionado.get().id());
        assertEquals(95, seleccionado.get().bateria());
    }

    @Test
    @DisplayName("Misión nula opera seleccionando el de mayor batería disponible")
    void misionNula_seleccionaMayorBateria() {
        List<DroneAcuatico> flota = List.of(droneEmbalseNorteMedio, droneCanalCentralMayor);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, null);

        assertTrue(seleccionado.isPresent());
        assertEquals("AR-03", seleccionado.get().id());
    }
}
