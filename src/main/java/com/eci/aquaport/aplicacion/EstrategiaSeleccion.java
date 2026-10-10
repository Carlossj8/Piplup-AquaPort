package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.DroneAcuatico;
import com.eci.aquaport.dominio.Mision;

import java.util.List;
import java.util.Optional;

/**
 * Patrón Strategy: Interfaz que desacopla el algoritmo de selección
 * del drone óptimo de la lógica del orquestador.
 */
public interface EstrategiaSeleccion {

    Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> flota, Mision mision);
}
