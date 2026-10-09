package com.eci.aquaport.infraestructura;

import com.eci.aquaport.dominio.EstadoMision;
import com.eci.aquaport.dominio.Mision;
import com.eci.aquaport.dominio.RepositorioMisiones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioMisionesMemoria implements RepositorioMisiones {

    private final Map<String, Mision> misiones = new ConcurrentHashMap<>();

    @Override
    public void guardar(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión a guardar no puede ser nula.");
        }
        misiones.put(mision.getId(), mision);
    }

    @Override
    public Optional<Mision> buscarPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(misiones.get(id));
    }

    @Override
    public List<Mision> obtenerTodas() {
        return Collections.unmodifiableList(new ArrayList<>(misiones.values()));
    }

    @Override
    public List<Mision> obtenerPorEstado(EstadoMision estado) {
        if (estado == null) {
            return List.of();
        }
        return misiones.values().stream()
                .filter(m -> m.getEstado() == estado)
                .toList();
    }

    @Override
    public boolean existePorId(String id) {
        if (id == null) {
            return false;
        }
        return misiones.containsKey(id);
    }
}
