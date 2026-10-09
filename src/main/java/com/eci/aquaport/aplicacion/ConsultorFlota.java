package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ConsultorFlota {

    /**
     * Consulta 1: Obtener la lista de drones disponibles con bateria >= 35%,
     * ordenados de mayor a menor bateria.
     */
    public List<DroneAcuatico> obtenerDisponiblesConBateriaSuficiente(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(d -> d.disponible() && d.bateria() >= 35)
                .sorted(Comparator.comparingInt(DroneAcuatico::bateria).reversed())
                .toList();
    }

    /**
     * Consulta 2: Obtener solo los IDs de los drones disponibles (como List<String>).
     */
    public List<String> obtenerIdsDronesDisponibles(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .map(DroneAcuatico::id)
                .toList();
    }

    /**
     * Consulta 3: ¿Existe algun drone disponible con bateria >= 35%? (retorna boolean).
     */
    public boolean existeDroneDisponibleConBateriaSuficiente(List<DroneAcuatico> flota) {
        return flota.stream()
                .anyMatch(d -> d.disponible() && d.bateria() >= 35);
    }

    /**
     * Consulta 4: Contar cuantos drones estan disponibles.
     */
    public long contarDronesDisponibles(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .count();
    }

    /**
     * Consulta 5: Obtener el drone con mayor bateria de toda la flota (retorna Optional<DroneAcuatico>).
     */
    public Optional<DroneAcuatico> obtenerDroneConMayorBateria(List<DroneAcuatico> flota) {
        return flota.stream()
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}
