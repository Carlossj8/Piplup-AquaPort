package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock
    private EstrategiaSeleccion estrategiaMock;

    @Mock
    private ObservadorMision centroControlMock;

    @Mock
    private ObservadorMision tecnicoMantenimientoMock;

    private AsignadorAutomatico asignador;
    private Mision misionNormal;
    private Mision misionCritica;
    private DroneAcuatico droneApto;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategiaMock);

        droneApto = FabricaDrones.crearSuperficial("AR-01", 90, "Embalse Norte");

        misionNormal = new Mision.Builder()
                .id("M-NORM-1")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .prioridad(Prioridad.NORMAL)
                .build();

        misionCritica = new Mision.Builder()
                .id("M-CRIT-1")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.SENSOR)
                .prioridad(Prioridad.CRITICA)
                .build();
    }

    @Test
    @DisplayName("1. Happy Path: Asignación exitosa notifica confirmación a los observadores")
    void asignacionExitosa_notificaExito() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);
        when(estrategiaMock.seleccionar(any(), eq(misionNormal)))
                .thenReturn(Optional.of(droneApto));

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, List.of(droneApto));

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals("AR-01", resultado.get().id());
        verify(centroControlMock, times(1)).notificarAsignacionExitosa(misionNormal, droneApto);
        verify(centroControlMock, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("2. Misión CRÍTICA sin drones disponibles notifica alerta crítica a todos los observadores")
    void misionCritica_sinDrones_notificaObservadores() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMantenimientoMock);

        when(estrategiaMock.seleccionar(any(), eq(misionCritica)))
                .thenReturn(Optional.empty());

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(misionCritica, List.of(droneApto));

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionCritica), contains("CRÍTICA"));
        verify(tecnicoMantenimientoMock, times(1)).notificarFallo(eq(misionCritica), contains("CRÍTICA"));
    }

    @Test
    @DisplayName("3. Drone encontrado en FALLO durante asignación notifica alerta y busca drone alternativo exitosamente")
    void droneEnFalloDuranteAsignacion_notificaFalloYBuscaAlternativo() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMantenimientoMock);

        DroneAcuatico droneEnFallo = new DroneAcuatico(
                "AR-FALLO-1", "Aqua-Ranger 100", 80, false, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.FALLO);

        DroneAcuatico droneAlternativo = new DroneAcuatico(
                "AR-ALT-2", "Aqua-Ranger 100", 75, true, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.DISPONIBLE);

        List<DroneAcuatico> flota = List.of(droneEnFallo, droneAlternativo);

        // Primer intento retorna el drone en fallo; segundo intento con flota filtrada retorna el alternativo
        when(estrategiaMock.seleccionar(eq(flota), eq(misionNormal)))
                .thenReturn(Optional.of(droneEnFallo));
        when(estrategiaMock.seleccionar(argThat(lista -> lista.size() == 1 && lista.contains(droneAlternativo)), eq(misionNormal)))
                .thenReturn(Optional.of(droneAlternativo));

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, flota);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals("AR-ALT-2", resultado.get().id());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("FALLO técnico"));
        verify(tecnicoMantenimientoMock, times(1)).notificarFallo(eq(misionNormal), contains("FALLO técnico"));
        verify(centroControlMock, times(1)).notificarAsignacionExitosa(misionNormal, droneAlternativo);
    }

    @Test
    @DisplayName("4. Lista de drones vacía notifica fallo a los observadores sin invocar la estrategia")
    void listaDronesVacia_notificaFallo() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, Collections.emptyList());

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("vacía"));
        verify(estrategiaMock, never()).seleccionar(any(), any());
    }

    @Test
    @DisplayName("5. Caso edge: Drone con batería exactamente en el umbral mínimo (35%) es asignado exitosamente")
    void bateriaExactamenteEnUmbralMinimo_asignacionExitosa() {
        // ARRANGE con estrategia real MayorBateriaStrategy para probar el caso edge de dominio
        AsignadorAutomatico asignadorReal = new AsignadorAutomatico(new MayorBateriaStrategy());
        asignadorReal.registrarObservador(centroControlMock);

        DroneAcuatico droneUmbral35 = new DroneAcuatico(
                "AR-35", "Aqua-Ranger 100", 35, true, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.DISPONIBLE);

        // ACT
        Optional<DroneAcuatico> resultado = asignadorReal.asignar(misionNormal, List.of(droneUmbral35));

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(35, resultado.get().bateria());
        verify(centroControlMock, times(1)).notificarAsignacionExitosa(misionNormal, droneUmbral35);
    }

    @Test
    @DisplayName("Caso edge negativo: Drone con 34% de batería está por debajo del umbral y es rechazado")
    void bateriaBajoUmbralMinimo_rechazado() {
        AsignadorAutomatico asignadorReal = new AsignadorAutomatico(new MayorBateriaStrategy());
        asignadorReal.registrarObservador(centroControlMock);

        DroneAcuatico droneBajo34 = new DroneAcuatico(
                "AR-34", "Aqua-Ranger 100", 34, true, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.DISPONIBLE);

        Optional<DroneAcuatico> resultado = asignadorReal.asignar(misionNormal, List.of(droneBajo34));

        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("No se encontraron drones aptos"));
    }

    @Test
    @DisplayName("Drone en FALLO sin alternativa viable notifica fallo final y retorna empty")
    void droneEnFalloSinAlternativo_retornaEmpty() {
        asignador.registrarObservador(centroControlMock);

        DroneAcuatico droneEnFallo = new DroneAcuatico(
                "AR-FALLO-1", "Aqua-Ranger 100", 80, false, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.FALLO);

        when(estrategiaMock.seleccionar(any(), eq(misionNormal)))
                .thenReturn(Optional.of(droneEnFallo));

        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, List.of(droneEnFallo));

        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("FALLO técnico"));
    }

    @Test
    @DisplayName("Un observador desregistrado no recibe notificaciones")
    void observadorRemovido_noRecibeNotificacion() {
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMantenimientoMock);
        asignador.removerObservador(tecnicoMantenimientoMock);

        when(estrategiaMock.seleccionar(any(), eq(misionNormal)))
                .thenReturn(Optional.empty());

        asignador.asignar(misionNormal, List.of(droneApto));

        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), anyString());
        verify(tecnicoMantenimientoMock, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("Sobrecarga asignar(Mision) con flota nula o vacía notifica y retorna empty")
    void asignarSobrecarga_retornaEmpty() {
        asignador.registrarObservador(centroControlMock);

        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal);

        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("vacía"));
    }

    @Test
    @DisplayName("Misión nula lanza IllegalArgumentException")
    void misionNula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> asignador.asignar(null, List.of(droneApto)));
    }

    @Test
    @DisplayName("Estrategia nula en constructor o setter lanza NullPointerException")
    void estrategiaNula_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> new AsignadorAutomatico(null));
        assertThrows(NullPointerException.class, () -> asignador.setEstrategia(null));
    }

    @Test
    @DisplayName("setEstrategia con estrategia válida actualiza la estrategia exitosamente")
    void setEstrategia_valida_actualizaEstrategia() {
        MayorBateriaStrategy nuevaEstrategia = new MayorBateriaStrategy();
        asignador.setEstrategia(nuevaEstrategia);
        assertDoesNotThrow(() -> asignador.setEstrategia(nuevaEstrategia));
    }

    @Test
    @DisplayName("Registrar observador nulo o duplicado no genera duplicados ni excepciones")
    void registrarObservador_nuloODuplicado_ignora() {
        asignador.registrarObservador(null);
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(centroControlMock);

        when(estrategiaMock.seleccionar(any(), eq(misionNormal)))
                .thenReturn(Optional.of(droneApto));

        asignador.asignar(misionNormal, List.of(droneApto));

        verify(centroControlMock, times(1)).notificarAsignacionExitosa(misionNormal, droneApto);
    }

    @Test
    @DisplayName("Drone inicial en FALLO pero estrategia no encuentra alternativo viable notifica fallo final")
    void droneEnFallo_sinAlternativoViablePorEstrategia_retornaEmpty() {
        asignador.registrarObservador(centroControlMock);

        DroneAcuatico droneEnFallo = new DroneAcuatico(
                "AR-FALLO-1", "Aqua-Ranger 100", 80, false, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.FALLO);

        DroneAcuatico droneIncompatible = new DroneAcuatico(
                "AR-INCOMPATIBLE", "Aqua-Ranger 100", 20, false, "Embalse Norte",
                TipoDrone.SUPERFICIAL, EstadoDrone.MANTENIMIENTO);

        List<DroneAcuatico> flota = List.of(droneEnFallo, droneIncompatible);

        when(estrategiaMock.seleccionar(any(), eq(misionNormal)))
                .thenReturn(Optional.of(droneEnFallo))
                .thenReturn(Optional.empty());

        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, flota);

        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("FALLO técnico"));
        verify(centroControlMock, times(1)).notificarFallo(eq(misionNormal), contains("No se encontró drone alternativo"));
    }

    @Test
    @DisplayName("Notificación de evento de fallo manual propaga a observadores")
    void notificarEventoFallo_propagaAObservadores() {
        asignador.registrarObservador(centroControlMock);

        asignador.notificarEventoFallo(misionNormal, "Emergencia climática");

        verify(centroControlMock, times(1)).notificarFallo(misionNormal, "Emergencia climática");
    }
}
