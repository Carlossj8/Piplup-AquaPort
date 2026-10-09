package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.RepositorioMisiones;
import com.eci.aquaport.dominio.ValidadorMision;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegistradorMisiones {

    private final RepositorioMisiones repositorio;
    private final ValidadorMision validador;
    private final NotificadorOperador notificador;

    /**
     * Constructor Injection: Cumple con el Principio de Inversión de Dependencias (DIP).
     * Depende de la interfaz RepositorioMisiones y no de una implementación concreta.
     */
    public RegistradorMisiones(
            RepositorioMisiones repositorio,
            ValidadorMision validador,
            NotificadorOperador notificador) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio no puede ser nulo.");
        this.validador = Objects.requireNonNull(validador, "El validador no puede ser nulo.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador no puede ser nulo.");
    }

    /**
     * Registra una misión aplicando las validaciones de negocio previas a la persistencia.
     */
    public Mision registrarMision(Mision mision) {
        try {
            validador.validar(mision);

            if (repositorio.existePorId(mision.getId())) {
                throw new IllegalStateException("Ya existe una misión registrada con el ID: " + mision.getId());
            }

            repositorio.guardar(mision);
            notificador.notificarMisionRegistrada(mision);
            return mision;
        } catch (Exception e) {
            notificador.notificarError(e.getMessage());
            throw e;
        }
    }

    public Optional<Mision> consultarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    public List<Mision> consultarTodas() {
        return repositorio.obtenerTodas();
    }

    public List<Mision> consultarPorEstado(EstadoMision estado) {
        return repositorio.obtenerPorEstado(estado);
    }
}
