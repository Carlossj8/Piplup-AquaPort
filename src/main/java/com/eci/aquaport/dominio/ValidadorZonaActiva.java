package com.eci.aquaport.dominio;

import java.util.Set;

/**
 * Eslabón que valida que los puntos de partida y llegada pertenezcan a zonas hídricas activas.
 */
public class ValidadorZonaActiva extends EslabonValidacion {

    private static final Set<String> ZONAS_ACTIVAS = Set.of(
            "Embalse de Investigación", "Red de Canales", "Laguna de Reserva", "Laboratorio Hídrico Central",
            "Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico"
    );

    @Override
    protected ResultadoValidacion validar(Mision mision) {
        if (mision == null || mision.getPuntoPartida() == null || mision.getPuntoLlegada() == null) {
            return ResultadoValidacion.fallido("Puntos de ruta no definidos");
        }

        if (!esZonaActiva(mision.getPuntoPartida())) {
            return ResultadoValidacion.fallido("Zona de partida inactiva o desconocida: " + mision.getPuntoPartida());
        }

        if (!esZonaActiva(mision.getPuntoLlegada())) {
            return ResultadoValidacion.fallido("Zona de llegada inactiva o desconocida: " + mision.getPuntoLlegada());
        }

        return ResultadoValidacion.exitoso();
    }

    private boolean esZonaActiva(String zona) {
        return ZONAS_ACTIVAS.stream().anyMatch(z -> z.equalsIgnoreCase(zona.trim()));
    }
}
