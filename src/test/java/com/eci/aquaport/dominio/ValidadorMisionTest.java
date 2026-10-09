package com.eci.aquaport.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorMisionTest {

    private ValidadorMision validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorMision();
    }

    @Test
    @DisplayName("Drone con batería ≥ 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        // ARRANGE
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(drone);

        // ASSERT
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado")
    void droneBateriaCritica_noAsignable() {
        // ARRANGE
        DroneAcuatico drone = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(drone);

        // ASSERT
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Drone con batería exactamente en 35% (umbral límite) puede ser asignado")
    void droneBateriaExactamenteUmbral_puedeAsignarse() {
        // ARRANGE
        DroneAcuatico drone = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 35, true, "Canal Central");

        // ACT
        boolean resultado = validador.tieneBateriaSuficiente(drone);

        // ASSERT
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone nulo retorna false en verificación de batería")
    void droneNulo_retornaFalse() {
        // ACT & ASSERT
        assertFalse(validador.tieneBateriaSuficiente(null));
    }

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException")
    void puntoLlegadaNulo_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada(null));
        assertTrue(ex.getMessage().contains("no puede ser nulo"));
    }

    @Test
    @DisplayName("Punto de llegada vacío o en blanco lanza IllegalArgumentException")
    void puntoLlegadaVacio_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada("   "));
        assertTrue(ex.getMessage().contains("no puede estar vacío"));
    }

    @Test
    @DisplayName("Punto de llegada fuera de las zonas del campus lanza IllegalArgumentException (caso edge)")
    void puntoLlegadaZonaInvalida_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada("Playa Desconocida"));
        assertTrue(ex.getMessage().contains("no pertenece a una zona válida"));
    }

    @Test
    @DisplayName("Punto de partida nulo lanza IllegalArgumentException")
    void puntoPartidaNulo_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoPartida(null));
        assertTrue(ex.getMessage().contains("no puede ser nulo"));
    }

    @Test
    @DisplayName("Punto de partida vacío o en blanco lanza IllegalArgumentException")
    void puntoPartidaVacio_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoPartida(""));
        assertTrue(ex.getMessage().contains("no puede estar vacío"));
    }

    @Test
    @DisplayName("Punto de partida con zona no válida del campus lanza IllegalArgumentException")
    void puntoPartidaZonaInvalida_lanzaExcepcion() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoPartida("Río Bogotá"));
        assertTrue(ex.getMessage().contains("no pertenece a una zona válida"));
    }

    @Test
    @DisplayName("Misión nula lanza IllegalArgumentException al validar")
    void misionNula_lanzaExcepcion() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> validador.validar(null));
    }

    @Test
    @DisplayName("Misión con drone con batería crítica lanza IllegalStateException")
    void misionConDroneBateriaCritica_lanzaExcepcion() {
        // ARRANGE
        DroneAcuatico droneBajo = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 20, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-201")
                .drone(droneBajo)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        // ACT & ASSERT
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validador.validar(mision));
        assertTrue(ex.getMessage().contains("batería insuficiente"));
    }

    @Test
    @DisplayName("Misión con origen y destino idénticos lanza IllegalStateException")
    void misionOrigenYDestinoIguales_lanzaExcepcion() {
        // ARRANGE
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-202")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Embalse Norte")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        // ACT & ASSERT
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validador.validar(mision));
        assertTrue(ex.getMessage().contains("no pueden ser el mismo"));
    }

    @Test
    @DisplayName("Misión válida con drone disponible y ruta permitida no lanza ninguna excepción")
    void misionValida_pasaSinExcepcion() {
        // ARRANGE
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-203")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        // ACT & ASSERT
        assertDoesNotThrow(() -> validador.validar(mision));
    }

    @Test
    @DisplayName("Misión cuyo drone retorna null lanza IllegalStateException")
    void misionConDroneRetornadoNull_lanzaExcepcion() {
        // ARRANGE
        Mision misionMock = org.mockito.Mockito.mock(Mision.class);
        org.mockito.Mockito.when(misionMock.getDrone()).thenReturn(null);

        // ACT & ASSERT
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validador.validar(misionMock));
        assertTrue(ex.getMessage().contains("no tiene un drone asignado"));
    }

    @Test
    @DisplayName("Misión cuyo drone deja de estar disponible lanza IllegalStateException")
    void misionConDroneNoDisponibleEnValidacion_lanzaExcepcion() {
        // ARRANGE
        Mision misionMock = org.mockito.Mockito.mock(Mision.class);
        DroneAcuatico droneOcupado = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, false, "Embalse Norte");
        org.mockito.Mockito.when(misionMock.getDrone()).thenReturn(droneOcupado);

        // ACT & ASSERT
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validador.validar(misionMock));
        assertTrue(ex.getMessage().contains("no está disponible"));
    }
}
