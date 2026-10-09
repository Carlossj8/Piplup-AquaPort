package com.eci.aquaport.dominio;

import java.util.Set;

public class ValidadorMision {

    public static final int BATERIA_MINIMA_OPERACION = 35;

    public static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte",
            "Canal Central",
            "Laguna Sur",
            "Punto Ribereño Este",
            "Laboratorio Hídrico"
    );

    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        if (drone == null) {
            return false;
        }
        return drone.bateria() >= BATERIA_MINIMA_OPERACION;
    }

    public void validarPuntoPartida(String puntoPartida) {
        if (puntoPartida == null) {
            throw new IllegalArgumentException("El punto de partida no puede ser nulo.");
        }
        String puntoLimpio = puntoPartida.trim();
        if (puntoLimpio.isEmpty()) {
            throw new IllegalArgumentException("El punto de partida no puede estar vacío.");
        }
        if (!ZONAS_VALIDAS.contains(puntoLimpio)) {
            throw new IllegalArgumentException("El punto de partida no pertenece a una zona válida del campus: " + puntoPartida);
        }
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null) {
            throw new IllegalArgumentException("El punto de llegada no puede ser nulo.");
        }
        String puntoLimpio = puntoLlegada.trim();
        if (puntoLimpio.isEmpty()) {
            throw new IllegalArgumentException("El punto de llegada no puede estar vacío.");
        }
        if (!ZONAS_VALIDAS.contains(puntoLimpio)) {
            throw new IllegalArgumentException("El punto de llegada no pertenece a una zona válida del campus: " + puntoLlegada);
        }
    }

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
}
