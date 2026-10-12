package com.eci.aquaport.dominio;

/**
 * Patrón Decorator: Interfaz de componente que define el comportamiento operativo
 * y de transmisión de un drone en el ecosistema Enterprise.
 */
public interface DroneOperativo {

    String id();

    int bateria();

    String zona();

    TipoDrone tipo();

    EstadoDrone estado();

    String transmitirTelemetria();
}
