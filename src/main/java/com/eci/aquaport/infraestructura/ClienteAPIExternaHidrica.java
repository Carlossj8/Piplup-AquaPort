package com.eci.aquaport.infraestructura;

/**
 * Contrato de la API externa de sensores hídricos con esquema foráneo en inglés.
 */
public interface ClienteAPIExternaHidrica {

    record RespuestaSensorExterno(
            String zoneId,
            double waterLevel,
            double turbidity,
            String status
    ) {}

    RespuestaSensorExterno consultarSensor(String zoneId);
}
