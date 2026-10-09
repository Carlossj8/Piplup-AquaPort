package com.eci.aquaport.dominio;

import java.util.List;
import java.util.Optional;

public interface RepositorioMisiones {

    void guardar(Mision mision);

    Optional<Mision> buscarPorId(String id);

    List<Mision> obtenerTodas();

    List<Mision> obtenerPorEstado(EstadoMision estado);

    boolean existePorId(String id);
}
