package com.eci.aquaport.dominio;

import java.util.Objects;
import java.util.Set;

/**
 * Encapsula de forma exclusiva las reglas de negocio y validaciones
 * operativas requeridas para la asignación y ejecución de misiones (SRP).
 */
public class ValidadorMision {

    public static final int BATERIA_MINIMA_OPERACION = 35;

    public static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte",
            "Canal Central",
            "Laguna Sur",
            "Punto Ribereño Este",
            "Laboratorio Hídrico"
    );

    /**
     * Verifica si un drone acuático cuenta con la batería mínima para operar.
     */
    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        if (drone == null) {
            return false;
        }
        return drone.bateria() >= BATERIA_MINIMA_OPERACION;
    }

    /**
     * Valida la existencia y pertenencia del punto de partida al campus hídrico.
     */
    public void validarPuntoPartida(String puntoPartida) {
        validarZona("partida", puntoPartida);
    }

    /**
     * Valida la existencia y pertenencia del punto de llegada al campus hídrico.
     */
    public void validarPuntoLlegada(String puntoLlegada) {
        validarZona("llegada", puntoLlegada);
    }

    /**
     * Ejecuta la validación integral de una misión antes de su registro formal.
     */
    public void validar(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión a validar no puede ser nula.");
        }

        DroneAcuatico drone = mision.getDrone();
        if (drone == null) {
            throw new IllegalStateException("La misión no tiene un drone asignado.");
        }

        if (!drone.disponible()) {
            throw new IllegalStateException("El drone " + drone.id() + " no está disponible.");
        }

        if (!tieneBateriaSuficiente(drone)) {
            throw new IllegalStateException("El drone " + drone.id() + " tiene batería insuficiente ("
                    + drone.bateria() + "%). Mínimo requerido: " + BATERIA_MINIMA_OPERACION + "%.");
        }

        validarPuntoPartida(mision.getPuntoPartida());
        validarPuntoLlegada(mision.getPuntoLlegada());

        if (mision.getPuntoPartida().trim().equalsIgnoreCase(mision.getPuntoLlegada().trim())) {
            throw new IllegalStateException("El punto de partida y llegada no pueden ser el mismo.");
        }
    }

    private void validarZona(String tipoPunto, String zona) {
        if (zona == null) {
            throw new IllegalArgumentException("El punto de " + tipoPunto + " no puede ser nulo.");
        }
        String zonaNormalizada = zona.trim();
        if (zonaNormalizada.isEmpty()) {
            throw new IllegalArgumentException("El punto de " + tipoPunto + " no puede estar vacío.");
        }
        if (!ZONAS_VALIDAS.contains(zonaNormalizada)) {
            throw new IllegalArgumentException("El punto de " + tipoPunto + " no pertenece a una zona válida del campus: " + zona);
        }
    }
}
