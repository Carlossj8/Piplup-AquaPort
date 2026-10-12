package com.eci.aquaport.dominio;

/**
 * Eslabón que valida que la carga a transportar no supere la capacidad máxima
 * del tipo de drone asignado.
 */
public class ValidadorCapacidadCarga extends EslabonValidacion {

    @Override
    protected ResultadoValidacion validar(Mision mision) {
        if (mision == null || mision.getDrone() == null || mision.getTipoCarga() == null) {
            return ResultadoValidacion.fallido("Datos incompletos de misión, drone o carga");
        }

        int pesoGramos = obtenerPesoEstimado(mision.getTipoCarga());
        int capacidadMaxima = mision.getDrone().tipo().getCapacidadMaximaGramos();

        if (pesoGramos > capacidadMaxima) {
            return ResultadoValidacion.fallido(String.format(
                    "Sobrecarga: tipo %s requiere %dg pero drone %s solo soporta %dg",
                    mision.getTipoCarga(), pesoGramos, mision.getDrone().tipo(), capacidadMaxima));
        }

        return ResultadoValidacion.exitoso();
    }

    private int obtenerPesoEstimado(TipoCarga tipo) {
        return switch (tipo) {
            case MUESTRA_AGUA -> 250;
            case SENSOR -> 450;
            case PAQUETE_LIGERO -> 1000;
        };
    }
}
