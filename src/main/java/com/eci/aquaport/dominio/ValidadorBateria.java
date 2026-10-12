package com.eci.aquaport.dominio;

/**
 * Eslabón que valida el nivel mínimo de energía del drone asignado (umbral >= 35%).
 */
public class ValidadorBateria extends EslabonValidacion {

    public static final int BATERIA_MINIMA = 35;

    @Override
    protected ResultadoValidacion validar(Mision mision) {
        if (mision == null || mision.getDrone() == null) {
            return ResultadoValidacion.fallido("Misión o drone nulo");
        }

        if (mision.getDrone().bateria() < BATERIA_MINIMA) {
            return ResultadoValidacion.fallido("Batería insuficiente: " + mision.getDrone().bateria() + "% (mínimo requerido 35%)");
        }

        return ResultadoValidacion.exitoso();
    }
}
