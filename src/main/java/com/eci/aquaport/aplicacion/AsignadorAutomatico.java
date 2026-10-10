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
     * Asigna un drone automáticamente delegando polimórficamente a la estrategia,
     * sin inspección de tipos (evita violaciones con instanceof).
     */
    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flota) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionar(flota, mision);

        if (seleccionado.isPresent()) {
            DroneAcuatico drone = seleccionado.get();
            notificarTodosExito(mision, drone);
            return seleccionado;
        } else {
            String motivo = "No se encontraron drones aptos o disponibles para la misión " + mision.getId();
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
