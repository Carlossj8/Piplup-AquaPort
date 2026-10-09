package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import com.eci.aquaport.infraestructura.RepositorioMisionesMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegistradorMisionesTest {

    private RepositorioMisiones repositorio;
    private ValidadorMision validador;
    private ObservadorNotificacionesTest notificador;
    private RegistradorMisiones registrador;

    private DroneAcuatico droneApto;
    private DroneAcuatico droneBateriaBaja;

    // Stub/Spy sencillo para verificar notificaciones sin frameworks complejos
    private static class ObservadorNotificacionesTest implements NotificadorOperador {
        final List<Mision> registradas = new ArrayList<>();
        final List<String> errores = new ArrayList<>();

        @Override
        public void notificarMisionRegistrada(Mision mision) {
            registradas.add(mision);
        }

        @Override
        public void notificarError(String mensaje) {
            errores.add(mensaje);
        }
    }

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioMisionesMemoria();
        validador = new ValidadorMision();
        notificador = new ObservadorNotificacionesTest();
        registrador = new RegistradorMisiones(repositorio, validador, notificador);

        droneApto = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 80, true, "Embalse Norte");
        droneBateriaBaja = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 20, true, "Laguna Sur");
    }

    @Test
    @DisplayName("Registrar misión válida la guarda y notifica al operador (SRP y DIP)")
    void registrarMisionValida_exito() {
        Mision mision = new Mision.Builder()
                .id("M-101")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        Mision resultado = registrador.registrarMision(mision);

        assertEquals(mision, resultado);
        assertTrue(repositorio.buscarPorId("M-101").isPresent());
        assertEquals(1, notificador.registradas.size());
        assertEquals("M-101", notificador.registradas.get(0).getId());
    }

    @Test
    @DisplayName("Misión con drone con batería baja es rechazada y notifica error")
    void registrarMision_droneBateriaBaja_lanzaExcepcion() {
        Mision mision = new Mision.Builder()
                .id("M-102")
                .drone(droneBateriaBaja)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Canal Central")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registrador.registrarMision(mision));

        assertTrue(ex.getMessage().contains("batería insuficiente"));
        assertFalse(repositorio.buscarPorId("M-102").isPresent());
        assertEquals(1, notificador.errores.size());
    }

    @Test
    @DisplayName("Rechaza registrar misión si el ID ya existe en el repositorio")
    void registrarMision_idDuplicado_lanzaExcepcion() {
        Mision mision1 = new Mision.Builder()
                .id("M-103")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        registrador.registrarMision(mision1);

        Mision misionDuplicada = new Mision.Builder()
                .id("M-103")
                .drone(droneApto)
                .puntoPartida("Canal Central")
                .puntoLlegada("Laguna Sur")
                .tipoCarga(TipoCarga.PAQUETE_LIGERO)
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registrador.registrarMision(misionDuplicada));

        assertTrue(ex.getMessage().contains("Ya existe una misión"));
    }

    @Test
    @DisplayName("Consultar por ID retorna la misión si existe o Optional vacío si no")
    void consultarPorId() {
        Mision mision = new Mision.Builder()
                .id("M-104")
                .drone(droneApto)
                .puntoPartida("Canal Central")
                .puntoLlegada("Punto Ribereño Este")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        registrador.registrarMision(mision);

        Optional<Mision> encontrada = registrador.consultarPorId("M-104");
        assertTrue(encontrada.isPresent());
        assertEquals("M-104", encontrada.get().getId());

        Optional<Mision> noExistente = registrador.consultarPorId("M-999");
        assertTrue(noExistente.isEmpty());
    }

    @Test
    @DisplayName("Consultar por estado filtra adecuadamente las misiones")
    void consultarPorEstado() {
        Mision mision1 = new Mision.Builder()
                .id("M-105")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .estado(EstadoMision.PENDIENTE)
                .build();

        registrador.registrarMision(mision1);

        List<Mision> pendientes = registrador.consultarPorEstado(EstadoMision.PENDIENTE);
        assertEquals(1, pendientes.size());

        List<Mision> entregadas = registrador.consultarPorEstado(EstadoMision.ENTREGADA);
        assertTrue(entregadas.isEmpty());
    }
}
