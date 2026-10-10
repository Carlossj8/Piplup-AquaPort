package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia concreta que selecciona el drone disponible con mayor porcentaje de batería.
 */
public class MayorBateriaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> flota, Mision mision) {
        if (flota == null || flota.isEmpty()) {
            return Optional.empty();
        }

        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= 35)
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}
