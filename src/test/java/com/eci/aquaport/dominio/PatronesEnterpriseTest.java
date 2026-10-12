package com.eci.aquaport.dominio;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronesEnterpriseTest {

    @Mock
    private ProveedorCondicionesHidricas proveedorHidricoMock;

    @Mock
    private ClienteAPIExternaHidrica clienteExternoMock;

    private DroneAcuatico droneBuceador;
    private DroneAcuatico droneSuperficialBajo;
    private Mision misionValida;

    @BeforeEach
    void setUp() {
        droneBuceador = FabricaDrones.crearBuceador("BUC-01", 90, "Embalse de Investigación");
        droneSuperficialBajo = FabricaDrones.crearSuperficial("SUP-01", 20, "Red de Canales");

        misionValida = new Mision.Builder()
                .id("M-ENT-1")
                .drone(droneBuceador)
                .puntoPartida("Embalse de Investigación")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .prioridad(Prioridad.NORMAL)
                .build();
    }

    // =========================================================================
    // 1. Pruebas Chain of Responsibility
    // =========================================================================

    @Test
    @DisplayName("Chain of Responsibility: La cadena se detiene en el primer eslabón que falla")
    void cadenaResponsabilidad_detieneEnPrimerFallo() {
        ValidadorBateria validadorBateria = new ValidadorBateria();
        EslabonValidacion validadorMock = mock(EslabonValidacion.class);
        validadorBateria.enlazarSiguiente(validadorMock);

        Mision misionBateriaBaja = new Mision.Builder()
                .id("M-FAIL-1")
                .drone(droneSuperficialBajo)
                .puntoPartida("Red de Canales")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        ResultadoValidacion resultado = validadorBateria.procesar(misionBateriaBaja);

        assertFalse(resultado.valido());
        assertTrue(resultado.mensaje().contains("Batería insuficiente"));
        verify(validadorMock, never()).procesar(any());
        verify(validadorMock, never()).validar(any());
    }

    @Test
    @DisplayName("Chain of Responsibility: Validación completa exitosa cuando todos los eslabones aprueban")
    void cadenaResponsabilidad_todosLosEslabonesExitosos() {
        when(proveedorHidricoMock.consultarCondiciones("Embalse de Investigación"))
                .thenReturn(new CondicionHidrica(2.5, 15.0, true));

        ValidadorBateria bateria = new ValidadorBateria();
        ValidadorCapacidadCarga carga = new ValidadorCapacidadCarga();
        ValidadorZonaActiva zona = new ValidadorZonaActiva();
        ValidadorCondicionesHidricas hidricas = new ValidadorCondicionesHidricas(proveedorHidricoMock);

        bateria.enlazarSiguiente(carga)
                .enlazarSiguiente(zona)
                .enlazarSiguiente(hidricas);

        ResultadoValidacion resultado = bateria.procesar(misionValida);

        assertTrue(resultado.valido());
        assertEquals("Validación exitosa", resultado.mensaje());
    }

    @Test
    @DisplayName("Chain of Responsibility: Falla por sobrecarga de peso según tipo de drone")
    void cadenaResponsabilidad_fallaPorSobrecarga() {
        // Buceador solo soporta 300g, sensor pesa 450g
        Mision misionSobrecargada = new Mision.Builder()
                .id("M-OVERLOAD")
                .drone(droneBuceador)
                .puntoPartida("Embalse de Investigación")
                .puntoLlegada("Laboratorio Hídrico Central")
                .tipoCarga(TipoCarga.SENSOR)
                .build();

        ValidadorCapacidadCarga validadorCarga = new ValidadorCapacidadCarga();
        ResultadoValidacion resultado = validadorCarga.procesar(misionSobrecargada);

        assertFalse(resultado.valido());
        assertTrue(resultado.mensaje().contains("Sobrecarga"));
    }

    // =========================================================================
    // 2. Pruebas Decorator
    // =========================================================================

    @Test
    @DisplayName("Decorator: DroneConMonitoreo registra telemetría sin alterar datos del drone base")
    void decoradorMonitoreo_registraTelemetriaSinAlterarDrone() {
        DroneBaseOperativo base = new DroneBaseOperativo(droneBuceador);
        DroneConMonitoreo conMonitoreo = new DroneConMonitoreo(base);

        assertEquals("BUC-01", conMonitoreo.id());
        assertEquals(90, conMonitoreo.bateria());
        assertEquals(TipoDrone.BUCEADOR, conMonitoreo.tipo());
        assertEquals(0, conMonitoreo.getCantidadTransmisiones());

        String telemetria = conMonitoreo.transmitirTelemetria();

        assertTrue(telemetria.contains("ID=BUC-01"));
        assertEquals(1, conMonitoreo.getCantidadTransmisiones());
        assertEquals(List.of(telemetria), conMonitoreo.getRegistrosTelemetria());
    }

    @Test
    @DisplayName("Decorator: Apilamiento Cifrado(Monitoreo(base)) preserva identidad y cifra payload")
    void decoradorApilado_cifraYRegistraPreservandoIdentidad() {
        DroneBaseOperativo base = new DroneBaseOperativo(droneBuceador);
        DroneConMonitoreo monitoreado = new DroneConMonitoreo(base);
        DroneConCifrado cifradoYMonitoreado = new DroneConCifrado(monitoreado);

        assertEquals("BUC-01", cifradoYMonitoreado.id());
        assertEquals("Embalse de Investigación", cifradoYMonitoreado.zona());

        String resultadoCifrado = cifradoYMonitoreado.transmitirTelemetria();

        assertTrue(resultadoCifrado.startsWith("ENC[AES-256]:"));
        assertEquals(1, monitoreado.getCantidadTransmisiones());
    }

    // =========================================================================
    // 3. Pruebas Adapter
    // =========================================================================

    @Test
    @DisplayName("Adapter: Traduce correctamente campos de API externa con valores óptimos")
    void adaptadorAPIHidrica_traduceRespuestaExterna() {
        when(clienteExternoMock.consultarSensor("Embalse de Investigación"))
                .thenReturn(new ClienteAPIExternaHidrica.RespuestaSensorExterno(
                        "Embalse de Investigación", 3.2, 12.0, "ACTIVE"));

        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteExternoMock);
        CondicionHidrica condicion = adaptador.consultarCondiciones("Embalse de Investigación");

        assertEquals(3.2, condicion.nivelAguaMetros(), 0.001);
        assertEquals(12.0, condicion.turbidezNtu(), 0.001);
        assertTrue(condicion.navegable());
    }

    @Test
    @DisplayName("Adapter: Maneja valores límite de turbidez y nivel de agua")
    void adaptadorAPIHidrica_evaluaValoresLimite() {
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteExternoMock);

        // Caso límite exacto válido: nivel=0.5m (mínimo), turbidez=50.0 NTU (máximo)
        when(clienteExternoMock.consultarSensor("ZonaLimite"))
                .thenReturn(new ClienteAPIExternaHidrica.RespuestaSensorExterno("ZonaLimite", 0.5, 50.0, "ACTIVE"));

        CondicionHidrica condicionLimite = adaptador.consultarCondiciones("ZonaLimite");
        assertTrue(condicionLimite.navegable());

        // Caso excedido: turbidez=50.1 NTU
        when(clienteExternoMock.consultarSensor("ZonaExcedida"))
                .thenReturn(new ClienteAPIExternaHidrica.RespuestaSensorExterno("ZonaExcedida", 0.5, 50.1, "ACTIVE"));

        CondicionHidrica condicionInvalida = adaptador.consultarCondiciones("ZonaExcedida");
        assertFalse(condicionInvalida.navegable());
    }
}
