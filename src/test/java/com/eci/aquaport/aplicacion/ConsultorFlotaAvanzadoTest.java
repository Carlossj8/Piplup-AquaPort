package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultorFlotaAvanzadoTest {

    private ConsultorFlotaAvanzado consultor;
    private List<DroneAcuatico> flota15;
    private List<Mision> misiones;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlotaAvanzado();

        flota15 = List.of(
                // 5 Drones Superficiales
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte", TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 75, true, "Canal Central", TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 30, false, "Laguna Sur", TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 80, true, "Punto Ribereño Este", TipoDrone.SUPERFICIAL),
                new DroneAcuatico("AR-05", "Aqua-Ranger 100", 60, true, "Laboratorio Hídrico", TipoDrone.SUPERFICIAL),

                // 5 Drones Semisumergidos
                new DroneAcuatico("SS-01", "Aqua-Sub 200", 85, true, "Embalse Norte", TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("SS-02", "Aqua-Sub 200", 40, true, "Canal Central", TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("SS-03", "Aqua-Sub 200", 25, false, "Laguna Sur", TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("SS-04", "Aqua-Sub 200", 95, true, "Laguna Sur", TipoDrone.SEMISUMERGIDO),
                new DroneAcuatico("SS-05", "Aqua-Sub 200", 70, true, "Punto Ribereño Este", TipoDrone.SEMISUMERGIDO),

                // 5 Drones Buceadores
                new DroneAcuatico("BC-01", "Deep-Diver 300", 88, true, "Embalse Norte", TipoDrone.BUCEADOR),
                new DroneAcuatico("BC-02", "Deep-Diver 300", 50, true, "Laguna Sur", TipoDrone.BUCEADOR),
                new DroneAcuatico("BC-03", "Deep-Diver 300", 20, false, "Laboratorio Hídrico", TipoDrone.BUCEADOR),
                new DroneAcuatico("BC-04", "Deep-Diver 300", 92, true, "Embalse Norte", TipoDrone.BUCEADOR),
                new DroneAcuatico("BC-05", "Deep-Diver 300", 65, true, "Canal Central", TipoDrone.BUCEADOR)
        );

        DroneAcuatico d1 = flota15.get(0);
        DroneAcuatico d2 = flota15.get(5);
        DroneAcuatico d3 = flota15.get(10);

        misiones = List.of(
                new Mision.Builder().id("M-1").drone(d1).puntoPartida("Embalse Norte").puntoLlegada("Laboratorio Hídrico").tipoCarga(TipoCarga.MUESTRA_AGUA).prioridad(Prioridad.CRITICA).build(),
                new Mision.Builder().id("M-2").drone(d2).puntoPartida("Embalse Norte").puntoLlegada("Canal Central").tipoCarga(TipoCarga.PAQUETE_LIGERO).prioridad(Prioridad.NORMAL).build(),
                new Mision.Builder().id("M-3").drone(d3).puntoPartida("Embalse Norte").puntoLlegada("Laguna Sur").tipoCarga(TipoCarga.SENSOR).prioridad(Prioridad.CRITICA).build(),
                new Mision.Builder().id("M-4").drone(d1).puntoPartida("Canal Central").puntoLlegada("Laboratorio Hídrico").tipoCarga(TipoCarga.MUESTRA_AGUA).prioridad(Prioridad.BAJA).build()
        );
    }

    @Test
    @DisplayName("Consulta 1: Agrupar drones disponibles por tipo")
    void testAgruparDisponiblesPorTipo() {
        Map<TipoDrone, List<DroneAcuatico>> resultado = consultor.agruparDisponiblesPorTipo(flota15);

        assertEquals(4, resultado.get(TipoDrone.SUPERFICIAL).size());
        assertEquals(4, resultado.get(TipoDrone.SEMISUMERGIDO).size());
        assertEquals(4, resultado.get(TipoDrone.BUCEADOR).size());
    }

    @Test
    @DisplayName("Consulta 2: Obtener drone óptimo para zona (mayor batería y tipo compatible)")
    void testObtenerDroneOptimoParaZona() {
        Optional<DroneAcuatico> optimo = consultor.obtenerDroneOptimoParaZona(flota15, "Embalse Norte", TipoDrone.BUCEADOR);

        assertTrue(optimo.isPresent());
        assertEquals("BC-04", optimo.get().id());
        assertEquals(92, optimo.get().bateria());

        Optional<DroneAcuatico> noExistente = consultor.obtenerDroneOptimoParaZona(flota15, "Zona Inexistente", TipoDrone.BUCEADOR);
        assertTrue(noExistente.isEmpty());
    }

    @Test
    @DisplayName("Consulta 3: Calcular promedio de batería por tipo de drone")
    void testCalcularPromedioBateriaPorTipo() {
        Map<TipoDrone, Double> promedios = consultor.calcularPromedioBateriaPorTipo(flota15);

        // Superficial: (90 + 75 + 30 + 80 + 60) / 5 = 335 / 5 = 67.0
        assertEquals(67.0, promedios.get(TipoDrone.SUPERFICIAL), 0.01);
        // Semisumergido: (85 + 40 + 25 + 95 + 70) / 5 = 315 / 5 = 63.0
        assertEquals(63.0, promedios.get(TipoDrone.SEMISUMERGIDO), 0.01);
        // Buceador: (88 + 50 + 20 + 92 + 65) / 5 = 315 / 5 = 63.0
        assertEquals(63.0, promedios.get(TipoDrone.BUCEADOR), 0.01);
    }

    @Test
    @DisplayName("Consulta 4: Separar misiones CRÍTICAS de las demás con partitioningBy")
    void testSepararMisionesCriticas() {
        Map<Boolean, List<Mision>> particion = consultor.separarMisionesCriticas(misiones);

        List<Mision> criticas = particion.get(true);
        List<Mision> noCriticas = particion.get(false);

        assertEquals(2, criticas.size());
        assertTrue(criticas.stream().allMatch(m -> m.getPrioridad() == Prioridad.CRITICA));

        assertEquals(2, noCriticas.size());
        assertTrue(noCriticas.stream().noneMatch(m -> m.getPrioridad() == Prioridad.CRITICA));
    }

    @Test
    @DisplayName("Consulta 5: Listar todas las zonas únicas cubiertas por la flota activa")
    void testListarZonasUnicasFlotaActiva() {
        List<String> zonas = consultor.listarZonasUnicasFlotaActiva(flota15);

        assertEquals(5, zonas.size());
        assertTrue(zonas.contains("Embalse Norte"));
        assertTrue(zonas.contains("Canal Central"));
        assertTrue(zonas.contains("Laguna Sur"));
        assertTrue(zonas.contains("Punto Ribereño Este"));
        assertTrue(zonas.contains("Laboratorio Hídrico"));
    }
}
