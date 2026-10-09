package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este")
        );
    }

    @Test
    @DisplayName("Consulta 1: Drones disponibles con bateria >= 35% ordenados descendentemente")
    void testObtenerDisponiblesConBateriaSuficiente() {
        List<DroneAcuatico> resultado = consultor.obtenerDisponiblesConBateriaSuficiente(flota);

        assertEquals(3, resultado.size());
        assertEquals("AR-01", resultado.get(0).id());
        assertEquals(92, resultado.get(0).bateria());
        assertEquals("AR-04", resultado.get(1).id());
        assertEquals(73, resultado.get(1).bateria());
        assertEquals("AR-02", resultado.get(2).id());
        assertEquals(45, resultado.get(2).bateria());
    }

    @Test
    @DisplayName("Consulta 2: IDs de drones disponibles")
    void testObtenerIdsDronesDisponibles() {
        List<String> ids = consultor.obtenerIdsDronesDisponibles(flota);

        assertEquals(List.of("AR-01", "AR-02", "AR-04"), ids);
    }

    @Test
    @DisplayName("Consulta 3: Existe al menos un drone disponible con bateria >= 35%")
    void testExisteDroneDisponibleConBateriaSuficiente() {
        assertTrue(consultor.existeDroneDisponibleConBateriaSuficiente(flota));

        List<DroneAcuatico> sinDisponibles = List.of(
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur")
        );
        assertFalse(consultor.existeDroneDisponibleConBateriaSuficiente(sinDisponibles));
    }

    @Test
    @DisplayName("Consulta 4: Contar cantidad de drones disponibles")
    void testContarDronesDisponibles() {
        assertEquals(3, consultor.contarDronesDisponibles(flota));
    }

    @Test
    @DisplayName("Consulta 5: Drone con mayor bateria de toda la flota")
    void testObtenerDroneConMayorBateria() {
        Optional<DroneAcuatico> mayor = consultor.obtenerDroneConMayorBateria(flota);

        assertTrue(mayor.isPresent());
        assertEquals("AR-01", mayor.get().id());
        assertEquals(92, mayor.get().bateria());
    }
}
