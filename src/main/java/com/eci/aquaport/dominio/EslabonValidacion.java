package com.eci.aquaport.dominio;

/**
 * Patrón Chain of Responsibility: Eslabón base abstracto para la cadena de validación de misiones.
 */
public abstract class EslabonValidacion {

    private EslabonValidacion siguiente;

    public EslabonValidacion enlazarSiguiente(EslabonValidacion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public ResultadoValidacion procesar(Mision mision) {
        ResultadoValidacion resultado = validar(mision);
        if (!resultado.valido()) {
            return resultado;
        }
        if (siguiente != null) {
            return siguiente.procesar(mision);
        }
        return ResultadoValidacion.exitoso();
    }

    protected abstract ResultadoValidacion validar(Mision mision);

    public EslabonValidacion getSiguiente() {
        return siguiente;
    }
}
