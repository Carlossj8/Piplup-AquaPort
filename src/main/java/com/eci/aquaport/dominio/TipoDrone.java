package com.eci.aquaport.dominio;

public enum TipoDrone {
    SUPERFICIAL(500),
    SEMISUMERGIDO(1500),
    BUCEADOR(300);

    private final int capacidadMaximaGramos;

    TipoDrone(int capacidadMaximaGramos) {
        this.capacidadMaximaGramos = capacidadMaximaGramos;
    }

    public int getCapacidadMaximaGramos() {
        return capacidadMaximaGramos;
    }
}
