package com.eci.aquaport.dominio;

import java.util.Objects;

/**
 * Eslabón final que consulta al proveedor hídrico y valida que el cuerpo de agua sea navegable.
 */
public class ValidadorCondicionesHidricas extends EslabonValidacion {

    private final ProveedorCondicionesHidricas proveedor;

    public ValidadorCondicionesHidricas(ProveedorCondicionesHidricas proveedor) {
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor de condiciones hídricas no puede ser nulo.");
    }

    @Override
    protected ResultadoValidacion validar(Mision mision) {
        if (mision == null || mision.getPuntoPartida() == null) {
            return ResultadoValidacion.fallido("Misión o punto de partida nulo");
        }

        try {
            CondicionHidrica condicion = proveedor.consultarCondiciones(mision.getPuntoPartida());
            if (condicion == null || !condicion.navegable()) {
                return ResultadoValidacion.fallido("Condiciones hidrológicas adversas en: " + mision.getPuntoPartida());
            }
            return ResultadoValidacion.exitoso();
        } catch (Exception e) {
            return ResultadoValidacion.fallido("Fallo consultando condiciones hídricas: " + e.getMessage());
        }
    }
}
