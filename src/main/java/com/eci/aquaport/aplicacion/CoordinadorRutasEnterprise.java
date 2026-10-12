package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Coordinador Enterprise para orquestar rutas multi-etapa y analítica avanzada
 * sobre la flota de 60 drones en las 4 zonas hídricas de la ECI.
 */
public class CoordinadorRutasEnterprise {

    /**
     * Consulta 1: Calcula la eficiencia operativa por zona (completadas / totales).
     */
    public Map<String, Double> calcularEficienciaPorZona(List<Mision> misiones) {
        if (misiones == null || misiones.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<Mision>> porZona = misiones.stream()
                .collect(Collectors.groupingBy(Mision::getPuntoPartida));

        Map<String, Double> resultado = new HashMap<>();
        porZona.forEach((zona, lista) -> {
            long completadas = lista.stream()
                    .filter(m -> m.getEstado() == EstadoMision.ENTREGADA)
                    .count();
            resultado.put(zona, (double) completadas / lista.size());
        });
        return resultado;
    }

    /**
     * Consulta 2: Encuentra el drone con el mejor historial de entregas exitosas.
     */
    public Optional<DroneAcuatico> encontrarDroneConMasEntregasExitosas(List<Mision> misiones) {
        if (misiones == null || misiones.isEmpty()) {
            return Optional.empty();
        }

        return misiones.stream()
                .filter(m -> m.getEstado() == EstadoMision.ENTREGADA)
                .collect(Collectors.groupingBy(Mision::getDrone, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Consulta 3: Planifica la ruta óptima multi-etapa seleccionando el drone apto en cada tramo.
     */
    public List<TramoRuta> planificarRutaOptima(List<Waypoint> waypoints, List<DroneAcuatico> flota) {
        if (waypoints == null || flota == null) {
            return Collections.emptyList();
        }

        List<DroneAcuatico> disponibles = new ArrayList<>(flota.stream()
                .filter(DroneAcuatico::disponible)
                .filter(d -> d.bateria() >= 35)
                .filter(d -> d.estado() != EstadoDrone.FALLO)
                .toList());

        List<TramoRuta> ruta = new ArrayList<>();
        for (Waypoint wp : waypoints) {
            Optional<DroneAcuatico> seleccionado = seleccionarParaTramo(disponibles, wp.getZona());
            seleccionado.ifPresent(d -> {
                ruta.add(new TramoRuta(wp, d));
                disponibles.remove(d);
            });
        }
        return ruta;
    }

    private Optional<DroneAcuatico> seleccionarParaTramo(List<DroneAcuatico> candidatos, String zona) {
        return candidatos.stream()
                .filter(d -> d.zona().equalsIgnoreCase(zona))
                .max(Comparator.comparingInt(DroneAcuatico::bateria))
                .or(() -> candidatos.stream().max(Comparator.comparingInt(DroneAcuatico::bateria)));
    }

    /**
     * Consulta 4: Collector personalizado que agrupa drones por zona y calcula el promedio
     * de batería en una sola pasada.
     */
    public static Collector<DroneAcuatico, ?, Map<String, Double>> agrupadorBateriaPromedioPorZona() {
        return Collector.of(
                HashMap<String, int[]>::new,
                (mapa, drone) -> {
                    int[] acumulador = mapa.computeIfAbsent(drone.zona(), k -> new int[2]);
                    acumulador[0] += drone.bateria();
                    acumulador[1]++;
                },
                (mapa1, mapa2) -> {
                    mapa2.forEach((zona, acumulador2) -> {
                        int[] acumulador1 = mapa1.computeIfAbsent(zona, k -> new int[2]);
                        acumulador1[0] += acumulador2[0];
                        acumulador1[1] += acumulador2[1];
                    });
                    return mapa1;
                },
                mapa -> {
                    Map<String, Double> promedios = new HashMap<>();
                    mapa.forEach((zona, acc) -> promedios.put(zona, acc[1] == 0 ? 0.0 : (double) acc[0] / acc[1]));
                    return promedios;
                }
        );
    }

    /**
     * Consulta 5: Calcula la carga de trabajo por zona usando flatMap de waypoints en misiones activas.
     */
    public Map<String, Long> calcularCargaTrabajoPorZona(List<Mision> misiones) {
        if (misiones == null) {
            return Collections.emptyMap();
        }

        return misiones.stream()
                .filter(m -> m.getEstado() == EstadoMision.EN_TRANSITO)
                .flatMap(m -> m.getWaypoints().stream())
                .collect(Collectors.groupingBy(Waypoint::getZona, Collectors.counting()));
    }
}
