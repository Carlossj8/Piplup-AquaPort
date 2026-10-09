package com.eci.aquaport.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MisionTest {

    private DroneAcuatico droneDisponible;
    private DroneAcuatico droneNoDisponible;

    @BeforeEach
    void setUp() {
        droneDisponible = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        droneNoDisponible = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 20, false, "Laguna Sur");
    }

    @Test
    @DisplayName("Construir misión exitosamente con todos los campos válidos")
    void construirMisionValida_exito() {
        Mision mision = new Mision.Builder()
                .id("M-001")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estado(EstadoMision.PENDIENTE)
                .build();

        assertNotNull(mision);
        assertEquals("M-001", mision.getId());
        assertEquals(droneDisponible, mision.getDrone());
        assertEquals("Embalse Norte", mision.getPuntoPartida());
        assertEquals("Laboratorio Hídrico", mision.getPuntoLlegada());
        assertEquals(TipoCarga.MUESTRA_AGUA, mision.getTipoCarga());
        assertEquals(EstadoMision.PENDIENTE, mision.getEstado());
    }

    @Test
    @DisplayName("Estado por defecto es PENDIENTE cuando no se especifica explícitamente")
    void estadoPorDefectoEsPendiente() {
        Mision mision = new Mision.Builder()
                .id("M-002")
                .drone(droneDisponible)
                .puntoPartida("Canal Central")
                .puntoLlegada("Punto Ribereño Este")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        assertEquals(EstadoMision.PENDIENTE, mision.getEstado());
    }

    @Test
    @DisplayName("Lanza IllegalStateException si el ID es nulo o vacío")
    void idInvalido_lanzaExcepcion() {
        Mision.Builder builderNulo = new Mision.Builder()
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        IllegalStateException exNulo = assertThrows(IllegalStateException.class, builderNulo::build);
        assertTrue(exNulo.getMessage().contains("identificador de la misión"));

        Mision.Builder builderVacio = new Mision.Builder()
                .id("   ")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        IllegalStateException exVacio = assertThrows(IllegalStateException.class, builderVacio::build);
        assertTrue(exVacio.getMessage().contains("identificador de la misión"));
    }

    @Test
    @DisplayName("Lanza IllegalStateException si el drone es nulo")
    void droneNulo_lanzaExcepcion() {
        Mision.Builder builder = new Mision.Builder()
                .id("M-003")
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("drone asignado"));
    }

    @Test
    @DisplayName("Lanza IllegalStateException si el drone asignado no está disponible")
    void droneNoDisponible_lanzaExcepcion() {
        Mision.Builder builder = new Mision.Builder()
                .id("M-004")
                .drone(droneNoDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("no se encuentra disponible"));
    }

    @Test
    @DisplayName("Lanza IllegalStateException si punto de partida es nulo o vacío")
    void puntoPartidaInvalido_lanzaExcepcion() {
        Mision.Builder builderNulo = new Mision.Builder()
                .id("M-005")
                .drone(droneDisponible)
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        assertThrows(IllegalStateException.class, builderNulo::build);

        Mision.Builder builderVacio = new Mision.Builder()
                .id("M-005")
                .drone(droneDisponible)
                .puntoPartida("  ")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        assertThrows(IllegalStateException.class, builderVacio::build);
    }

    @Test
    @DisplayName("Lanza IllegalStateException si punto de llegada es nulo o vacío")
    void puntoLlegadaInvalido_lanzaExcepcion() {
        Mision.Builder builderNulo = new Mision.Builder()
                .id("M-006")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        assertThrows(IllegalStateException.class, builderNulo::build);

        Mision.Builder builderVacio = new Mision.Builder()
                .id("M-006")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);

        assertThrows(IllegalStateException.class, builderVacio::build);
    }

    @Test
    @DisplayName("Lanza IllegalStateException si el tipo de carga es nulo")
    void tipoCargaNulo_lanzaExcepcion() {
        Mision.Builder builder = new Mision.Builder()
                .id("M-007")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Lanza IllegalStateException si el estado explícito es nulo")
    void estadoNulo_lanzaExcepcion() {
        Mision.Builder builder = new Mision.Builder()
                .id("M-008")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.PAQUETE_LIGERO)
                .estado(null);

        assertThrows(IllegalStateException.class, builder::build);
    }
}
