package com.eci.aquaport.dominio;

/**
 * Puerto de dominio: Abstracción para consultar el estado ambiental y navegabilidad
 * de una zona hídrica sin acoplamiento a servicios o formatos externos.
 */
public interface ProveedorCondicionesHidricas {

    CondicionHidrica consultarCondiciones(String zona);
}
