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

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este")
        );

        ConsultorFlota consultor = new ConsultorFlota();

        System.out.println("=== AquaPort - Consultor de Flota ===");

        System.out.println("\n1. Drones disponibles con bateria >= 35% (ordenados desc):");
        consultor.obtenerDisponiblesConBateriaSuficiente(flota)
                .forEach(d -> System.out.println("   - " + d));

        System.out.println("\n2. IDs de drones disponibles:");
        System.out.println("   " + consultor.obtenerIdsDronesDisponibles(flota));

        System.out.println("\n3. ¿Existe algun drone disponible con bateria >= 35%?");
        System.out.println("   " + consultor.existeDroneDisponibleConBateriaSuficiente(flota));

        System.out.println("\n4. Cantidad de drones disponibles:");
        System.out.println("   " + consultor.contarDronesDisponibles(flota));

        System.out.println("\n5. Drone con mayor bateria:");
        consultor.obtenerDroneConMayorBateria(flota)
                .ifPresentOrElse(
                        d -> System.out.println("   " + d),
                        () -> System.out.println("   Ninguno")
                );
    }
}
