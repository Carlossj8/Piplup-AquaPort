package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;
import com.eci.aquaport.infraestructura.AdaptadorAPIHidrica;
import com.eci.aquaport.infraestructura.ClienteAPIExternaHidrica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Prueba de integración de 3 capas para la versión Enterprise (Empoleon):
 * Integra Aplicación (CoordinadorRutasEnterprise) + Dominio (Chain of Responsibility y Decorator)
 * + Infraestructura (AdaptadorAPIHidrica con Cliente Externo).
 */
@ExtendWith(MockitoExtension.class)
class MisionMultiEtapaIntegracionTest {

    @Mock
    private ClienteAPIExternaHidrica clienteExternoMock;

    private CoordinadorRutasEnterprise coordinador;
    private AdaptadorAPIHidrica adaptadorHidrico;
    private EslabonValidacion cadenaValidacion;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        coordinador = new CoordinadorRutasEnterprise();
        adaptadorHidrico = new AdaptadorAPIHidrica(clienteExternoMock);

        // Ensamblar la cadena completa de validación Enterprise
        ValidadorBateria eslabonBateria = new ValidadorBateria();
        ValidadorCapacidadCarga eslabonCarga = new ValidadorCapacidadCarga();
        ValidadorZonaActiva eslabonZona = new ValidadorZonaActiva();
        ValidadorCondicionesHidricas eslabonHidrico = new ValidadorCondicionesHidricas(adaptadorHidrico);

        eslabonBateria.enlazarSiguiente(eslabonCarga)
                .enlazarSiguiente(eslabonZona)
                .enlazarSiguiente(eslabonHidrico);

        cadenaValidacion = eslabonBateria;

        // Flota de prueba en distintas zonas del campus
        flota = List.of(
                FabricaDrones.crearSuperficial("AR-01", 85, "Embalse Norte"),
                FabricaDrones.crearSemisumergido("AR-02", 90, "Canal Central"),
                FabricaDrones.crearBuceador("AR-03", 95, "Laguna Sur")
        );
    }

    @Test
    @DisplayName("Integración 3 Capas: Planificación de ruta, validación en cadena y telemetría decorada exitosa")
    void flujoCompletoMisionMultiEtapa_exitoso() {
        // 1. Capa de Infraestructura: Simular condiciones hídricas óptimas en sensor externo
        when(clienteExternoMock.consultarSensor("Embalse Norte"))
                .thenReturn(new ClienteAPIExternaHidrica.RespuestaSensorExterno("Embalse Norte", 2.8, 15.0, "ACTIVE"));

        // 2. Capa de Aplicación: Planificar tramos de ruta con waypoints
        List<Waypoint> waypoints = List.of(
                new Waypoint("WP-01", "Embalse Norte"),
                new Waypoint("WP-02", "Canal Central")
        );
        List<TramoRuta> tramos = coordinador.planificarRutaOptima(waypoints, flota);
        assertEquals(2, tramos.size());
        assertEquals("AR-01", tramos.get(0).drone().id());

        // 3. Capa de Dominio: Formular misión multi-etapa con el drone asignado y waypoints
        DroneAcuatico droneAsignado = tramos.get(0).drone();
        Mision mision = new Mision.Builder()
                .id("MS-INT-001")
                .drone(droneAsignado)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Canal Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .prioridad(Prioridad.ALTA)
                .waypoints(waypoints)
                .build();

        // 4. Capa de Dominio + Infraestructura: Ejecutar la Chain of Responsibility completa
        ResultadoValidacion resultadoValidacion = cadenaValidacion.procesar(mision);
        assertTrue(resultadoValidacion.valido());
        assertEquals("Validación exitosa", resultadoValidacion.mensaje());

        // 5. Capa de Dominio: Aplicar Decoradores de Monitoreo y Cifrado AES-256 en runtime
        DroneBaseOperativo base = new DroneBaseOperativo(droneAsignado);
        DroneConMonitoreo monitoreado = new DroneConMonitoreo(base);
        DroneConCifrado cifrado = new DroneConCifrado(monitoreado);

        String telemetria = cifrado.transmitirTelemetria();

        assertNotNull(telemetria);
        assertTrue(telemetria.startsWith("ENC[AES-256]:"));
        assertEquals(1, monitoreado.getCantidadTransmisiones());
        verify(clienteExternoMock, times(1)).consultarSensor("Embalse Norte");
    }

    @Test
    @DisplayName("Integración 3 Capas: La cadena rechaza la misión si el adaptador detecta turbidez extrema")
    void flujoCompletoMisionMultiEtapa_rechazadoPorCondicionesHidricas() {
        // Simular alerta ambiental en sensor externo (turbidez excesiva = 75 NTU > 50 NTU)
        when(clienteExternoMock.consultarSensor("Laguna Sur"))
                .thenReturn(new ClienteAPIExternaHidrica.RespuestaSensorExterno("Laguna Sur", 1.5, 75.0, "ACTIVE"));

        DroneAcuatico drone = FabricaDrones.crearBuceador("AR-03", 90, "Laguna Sur");
        Mision mision = new Mision.Builder()
                .id("MS-INT-002")
                .drone(drone)
                .puntoPartida("Laguna Sur")
                .puntoLlegada("Canal Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .prioridad(Prioridad.CRITICA)
                .build();

        ResultadoValidacion resultado = cadenaValidacion.procesar(mision);

        assertFalse(resultado.valido());
        assertTrue(resultado.mensaje().contains("Condiciones hidrológicas adversas"));
    }
}
