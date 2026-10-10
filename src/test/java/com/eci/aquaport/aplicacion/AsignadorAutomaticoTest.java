package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private Mision mision;
    private DroneAcuatico droneApto;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategiaMock);

        droneApto = FabricaDrones.crearSuperficial("AR-01", 90, "Embalse Norte");

        mision = new Mision.Builder()
                .id("M-CRIT-1")
                .drone(droneApto)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .prioridad(Prioridad.CRITICA)
                .build();
    }

    @Test
    @DisplayName("Cuando la asignación falla, todos los observadores son notificados exactamente una vez")
    void asignacionFalla_notificaTodosLosObservadoresExactamenteUnaVez() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMantenimientoMock);

        when(estrategiaMock.seleccionar(any(), eq(mision)))
                .thenReturn(Optional.empty());

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(mision, List.of());

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(centroControlMock, times(1)).notificarFallo(eq(mision), contains("No se encontraron drones aptos"));
        verify(tecnicoMantenimientoMock, times(1)).notificarFallo(eq(mision), contains("No se encontraron drones aptos"));
    }

    @Test
    @DisplayName("Cuando la asignación es exitosa, se notifica el éxito a los observadores")
    void asignacionExitosa_notificaExito() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);

        when(estrategiaMock.seleccionar(any(), eq(mision)))
                .thenReturn(Optional.of(droneApto));

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(mision, List.of(droneApto));

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals("AR-01", resultado.get().id());
        verify(centroControlMock, times(1)).notificarAsignacionExitosa(mision, droneApto);
        verify(centroControlMock, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("Un observador desregistrado no recibe notificaciones de fallo")
    void observadorRemovido_noRecibeNotificacion() {
        // ARRANGE
        asignador.registrarObservador(centroControlMock);
        asignador.registrarObservador(tecnicoMantenimientoMock);
        asignador.removerObservador(tecnicoMantenimientoMock);

        when(estrategiaMock.seleccionar(any(), eq(mision)))
                .thenReturn(Optional.empty());

        // ACT
        asignador.asignar(mision, List.of());

        // ASSERT
        verify(centroControlMock, times(1)).notificarFallo(eq(mision), anyString());
        verify(tecnicoMantenimientoMock, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("Factory Method crea correctamente los 3 tipos de drones especializados")
    void fabricaDrones_creaTiposEspecializadosCorrectamente() {
        DroneAcuatico superficial = FabricaDrones.crearSuperficial("AR-10", 80, "Canal Central");
        DroneAcuatico semisumergido = FabricaDrones.crearSemisumergido("SS-10", 85, "Laguna Sur");
        DroneAcuatico buceador = FabricaDrones.crearBuceador("BC-10", 95, "Embalse Norte");

        assertEquals(TipoDrone.SUPERFICIAL, superficial.tipo());
        assertEquals(500, superficial.tipo().getCapacidadMaximaGramos());

        assertEquals(TipoDrone.SEMISUMERGIDO, semisumergido.tipo());
        assertEquals(1500, semisumergido.tipo().getCapacidadMaximaGramos());

        assertEquals(TipoDrone.BUCEADOR, buceador.tipo());
        assertEquals(300, buceador.tipo().getCapacidadMaximaGramos());
    }
}
