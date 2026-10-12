package com.eci.aquaport.dominio;

/**
 * Representa el resultado del análisis de un eslabón o de la cadena completa de validación.
 */
public record ResultadoValidacion(boolean valido, String mensaje) {

    public static ResultadoValidacion exitoso() {
        return new ResultadoValidacion(true, "Validación exitosa");
    }

    public static ResultadoValidacion fallido(String mensaje) {
        return new ResultadoValidacion(false, mensaje);
    }
}
