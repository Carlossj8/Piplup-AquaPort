package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.Prioridad;
import com.eci.aquaport.dominio.TipoDrone;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultorFlotaAvanzado {

    /**
     * Consulta 1: Agrupar drones disponibles por tipo de drone.
     */
    public Map<TipoDrone, List<DroneAcuatico>> agruparDisponiblesPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .collect(Collectors.groupingBy(DroneAcuatico::tipo));
    }

    /**
     * Consulta 2: Obtener el drone óptimo para una zona dada (mayor batería y tipo compatible).
     */
    public Optional<DroneAcuatico> obtenerDroneOptimoParaZona(List<DroneAcuatico> flota, String zona, TipoDrone tipoCompatible) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.zona().equalsIgnoreCase(zona))
                .filter(d -> d.tipo() == tipoCompatible)
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }

    /**
     * Consulta 3: Calcular el promedio de nivel de batería por tipo de drone.
     */
    public Map<TipoDrone, Double> calcularPromedioBateriaPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::tipo,
                        Collectors.averagingDouble(DroneAcuatico::bateria)
                ));
    }

    /**
     * Consulta 4: Separar misiones CRÍTICAS de las demás usando partitioningBy.
     */
    public Map<Boolean, List<Mision>> separarMisionesCriticas(List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.partitioningBy(m -> m.getPrioridad() == Prioridad.CRITICA));
    }

    /**
     * Consulta 5: Listar todas las zonas únicas que cubre la flota activa.
     */
    public List<String> listarZonasUnicasFlotaActiva(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::disponible)
                .map(DroneAcuatico::zona)
                .distinct()
                .sorted()
                .toList();
    }
}
