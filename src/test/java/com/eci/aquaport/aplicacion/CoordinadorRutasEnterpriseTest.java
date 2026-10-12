package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CoordinadorRutasEnterpriseTest {

    private CoordinadorRutasEnterprise coordinador;
    private DroneAcuatico dNorte1;
    private DroneAcuatico dNorte2;
    private DroneAcuatico dCanal1;
    private DroneAcuatico dLaguna1;

    @BeforeEach
    void setUp() {
        coordinador = new CoordinadorRutasEnterprise();

        dNorte1 = FabricaDrones.crearBuceador("D-01", 90, "Embalse de Investigación");
        dNorte2 = FabricaDrones.crearBuceador("D-02", 70, "Embalse de Investigación");
        dCanal1 = FabricaDrones.crearSuperficial("D-03", 85, "Red de Canales");
        dLaguna1 = FabricaDrones.crearSemisumergido("D-04", 80, "Laguna de Reserva");
    }

    @Test
    @DisplayName("1. Calcula la eficiencia por zona correctamente como (completadas / totales)")
    void calcularEficienciaPorZona_calculaCorrectamente() {
        Mision m1 = new Mision.Builder().id("M1").drone(dNorte1)
                .puntoPartida("Embalse de Investigación").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.ENTREGADA).build();

        Mision m2 = new Mision.Builder().id("M2").drone(dNorte2)
                .puntoPartida("Embalse de Investigación").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.FALLIDA).build();

        Mision m3 = new Mision.Builder().id("M3").drone(dCanal1)
                .puntoPartida("Red de Canales").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).estado(EstadoMision.ENTREGADA).build();

        Map<String, Double> eficiencia = coordinador.calcularEficienciaPorZona(List.of(m1, m2, m3));

        assertEquals(0.5, eficiencia.get("Embalse de Investigación"), 0.001);
        assertEquals(1.0, eficiencia.get("Red de Canales"), 0.001);
        assertTrue(coordinador.calcularEficienciaPorZona(null).isEmpty());
        assertTrue(coordinador.calcularEficienciaPorZona(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("2. Encuentra el drone con el mayor historial de entregas exitosas")
    void encontrarDroneConMasEntregasExitosas_retornaOptimo() {
        Mision m1 = new Mision.Builder().id("M1").drone(dNorte1)
                .puntoPartida("Embalse de Investigación").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.ENTREGADA).build();

        Mision m2 = new Mision.Builder().id("M2").drone(dNorte1)
                .puntoPartida("Embalse de Investigación").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.ENTREGADA).build();

        Mision m3 = new Mision.Builder().id("M3").drone(dCanal1)
                .puntoPartida("Red de Canales").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).estado(EstadoMision.ENTREGADA).build();

        Optional<DroneAcuatico> mejor = coordinador.encontrarDroneConMasEntregasExitosas(List.of(m1, m2, m3));

        assertTrue(mejor.isPresent());
        assertEquals("D-01", mejor.get().id());
        assertTrue(coordinador.encontrarDroneConMasEntregasExitosas(null).isEmpty());
        assertTrue(coordinador.encontrarDroneConMasEntregasExitosas(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("3. Planifica la ruta óptima multi-etapa asignando drones por tramo")
    void planificarRutaOptima_asignaCorrectamente() {
        Waypoint w1 = new Waypoint("W1", "Embalse de Investigación");
        Waypoint w2 = new Waypoint("W2", "Red de Canales");
        Waypoint w3 = new Waypoint("W3", "Laguna de Reserva");

        List<Waypoint> waypoints = List.of(w1, w2, w3);
        List<DroneAcuatico> flota = List.of(dNorte1, dNorte2, dCanal1, dLaguna1);

        List<TramoRuta> ruta = coordinador.planificarRutaOptima(waypoints, flota);

        assertEquals(3, ruta.size());
        assertEquals("D-01", ruta.get(0).drone().id());
        assertEquals("D-03", ruta.get(1).drone().id());
        assertEquals("D-04", ruta.get(2).drone().id());

        assertTrue(coordinador.planificarRutaOptima(null, flota).isEmpty());
        assertTrue(coordinador.planificarRutaOptima(waypoints, null).isEmpty());
    }

    @Test
    @DisplayName("4. Custom Collector calcula el promedio de batería por zona en una sola pasada (secuencial y paralelo)")
    void customCollector_calculaPromedioBateriaPorZona() {
        List<DroneAcuatico> flota = List.of(
                dNorte1, // 90
                dNorte2, // 70 -> promedio Embalse de Investigación = 80.0
                dCanal1, // 85 -> promedio Red de Canales = 85.0
                dLaguna1 // 80 -> promedio Laguna de Reserva = 80.0
        );

        Map<String, Double> promediosSecuencial = flota.stream()
                .collect(CoordinadorRutasEnterprise.agrupadorBateriaPromedioPorZona());

        assertEquals(80.0, promediosSecuencial.get("Embalse de Investigación"), 0.001);
        assertEquals(85.0, promediosSecuencial.get("Red de Canales"), 0.001);
        assertEquals(80.0, promediosSecuencial.get("Laguna de Reserva"), 0.001);

        // Prueba en stream paralelo para ejercitar el combiner del Collector
        Map<String, Double> promediosParalelo = flota.parallelStream()
                .collect(CoordinadorRutasEnterprise.agrupadorBateriaPromedioPorZona());

        assertEquals(promediosSecuencial, promediosParalelo);
    }

    @Test
    @DisplayName("5. Calcula la carga de trabajo por zona con flatMap encadenado")
    void calcularCargaTrabajoPorZona_flatMapEncadenado() {
        Waypoint w1 = new Waypoint("WP-1", "Embalse de Investigación");
        Waypoint w2 = new Waypoint("WP-2", "Red de Canales");
        Waypoint w3 = new Waypoint("WP-3", "Red de Canales");

        Mision m1 = new Mision.Builder().id("M-ACT-1").drone(dNorte1)
                .puntoPartida("Embalse de Investigación").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.EN_TRANSITO)
                .waypoints(List.of(w1, w2)).build();

        Mision m2 = new Mision.Builder().id("M-ACT-2").drone(dCanal1)
                .puntoPartida("Red de Canales").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.MUESTRA_AGUA).estado(EstadoMision.EN_TRANSITO)
                .waypoints(List.of(w3)).build();

        Mision m3 = new Mision.Builder().id("M-INACTIVA").drone(dLaguna1)
                .puntoPartida("Laguna de Reserva").puntoLlegada("Laboratorio")
                .tipoCarga(TipoCarga.SENSOR).estado(EstadoMision.PENDIENTE)
                .waypoints(List.of(w1)).build();

        Map<String, Long> carga = coordinador.calcularCargaTrabajoPorZona(List.of(m1, m2, m3));

        assertEquals(1L, carga.get("Embalse de Investigación"));
        assertEquals(2L, carga.get("Red de Canales"));
        assertNull(carga.get("Laguna de Reserva"));
        assertTrue(coordinador.calcularCargaTrabajoPorZona(null).isEmpty());
    }
}
