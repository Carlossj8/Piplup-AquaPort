package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Orquestador principal que integra Strategy para la selección autónoma
 * y Observer para el despacho de alertas a centros de control y mantenimiento.
 */
public class AsignadorAutomatico {

    private EstrategiaSeleccion estrategia;
    private final List<ObservadorMision> observadores = new ArrayList<>();

    public AsignadorAutomatico(EstrategiaSeleccion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula.");
    }

    public void setEstrategia(EstrategiaSeleccion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula.");
    }

    public void registrarObservador(ObservadorMision observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorMision observador) {
        observadores.remove(observador);
    }

    /**
     * Sobrecarga de asignación para llamadas directas sin flota explícita.
     */
    public Optional<DroneAcuatico> asignar(Mision mision) {
        return asignar(mision, List.of());
    }

    /**
     * Asigna un drone automáticamente delegando polimórficamente a la estrategia,
     * gestionando flotas vacías y contingencias de drones en FALLO técnico.
     */
    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flota) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }

        if (flota == null || flota.isEmpty()) {
            notificarTodosFallo(mision, "Lista de drones vacía para la misión " + mision.getId());
            return Optional.empty();
        }

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, mision);

        if (seleccionado.isPresent()) {
            DroneAcuatico drone = seleccionado.get();

            if (drone.estado() == com.eci.aquaport.dominio.EstadoDrone.FALLO) {
                notificarTodosFallo(mision, "Drone " + drone.id() + " en estado de FALLO técnico detectado durante asignación");

                List<DroneAcuatico> alternativos = flota.stream()
                        .filter(d -> !d.id().equals(drone.id()) && d.estado() != com.eci.aquaport.dominio.EstadoDrone.FALLO)
                        .toList();

                if (alternativos.isEmpty()) {
                    return Optional.empty();
                }

                Optional<DroneAcuatico> droneAlternativo = estrategia.seleccionar(alternativos, mision);
                if (droneAlternativo.isPresent() && droneAlternativo.get().estado() != com.eci.aquaport.dominio.EstadoDrone.FALLO) {
                    notificarTodosExito(mision, droneAlternativo.get());
                    return droneAlternativo;
                } else {
                    notificarTodosFallo(mision, "No se encontró drone alternativo para la misión " + mision.getId());
                    return Optional.empty();
                }
            }

            notificarTodosExito(mision, drone);
            return seleccionado;
        } else {
            String motivo = (mision.getPrioridad() == com.eci.aquaport.dominio.Prioridad.CRITICA)
                    ? "Misión CRÍTICA sin drones disponibles: " + mision.getId()
                    : "No se encontraron drones aptos o disponibles para la misión " + mision.getId();
            notificarTodosFallo(mision, motivo);
            return Optional.empty();
        }
    }

    /**
     * Notifica a todos los observadores registrados cuando ocurre un evento de fallo.
     */
    public void notificarEventoFallo(Mision mision, String motivo) {
        notificarTodosFallo(mision, motivo);
    }

    private void notificarTodosExito(Mision mision, DroneAcuatico drone) {
        for (ObservadorMision observador : observadores) {
            observador.notificarAsignacionExitosa(mision, drone);
        }
    }

    private void notificarTodosFallo(Mision mision, String motivo) {
        for (ObservadorMision observador : observadores) {
            observador.notificarFallo(mision, motivo);
        }
    }
}
