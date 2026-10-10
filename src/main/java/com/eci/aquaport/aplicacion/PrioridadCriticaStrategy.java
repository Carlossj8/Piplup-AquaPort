package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia concreta que prioriza compatibilidad técnica de zona y batería máxima
 * para misiones con precedencia crítica.
 */
public class PrioridadCriticaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> flota, Mision mision) {
        if (flota == null || flota.isEmpty()) {
            return Optional.empty();
        }

        // Si la misión es crítica, filtra preferiblemente drones en la misma zona de origen
        if (mision != null && mision.getPrioridad() == Prioridad.CRITICA) {
            Optional<DroneAcuatico> enZona = flota.stream()
                    .filter(DroneAcuatico::disponible)
                    .filter(d -> d.bateria() >= 35)
                    .filter(d -> d.zona().equalsIgnoreCase(mision.getPuntoPartida()))
                    .max(Comparator.comparingInt(DroneAcuatico::bateria));

            if (enZona.isPresent()) {
                return enZona;
            }
        }

        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= 35)
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}
