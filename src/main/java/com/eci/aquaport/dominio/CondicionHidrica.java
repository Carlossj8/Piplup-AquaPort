package com.eci.aquaport.dominio;

/**
 * Entidad de dominio puro que describe las condiciones ambientales y físicas
 * de una zona hídrica del campus de la ECI.
 */
public record CondicionHidrica(
        double nivelAguaMetros,
        double turbidezNtu,
        boolean navegable
) {
    public CondicionHidrica {
        if (nivelAguaMetros < 0) {
            throw new IllegalArgumentException("El nivel de agua no puede ser negativo.");
        }
        if (turbidezNtu < 0) {
            throw new IllegalArgumentException("La turbidez no puede ser negativa.");
        }
    }
}
