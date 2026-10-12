package com.eci.aquaport.infraestructura;

import com.eci.aquaport.dominio.CondicionHidrica;
import com.eci.aquaport.dominio.ProveedorCondicionesHidricas;

import java.util.Objects;

/**
 * Patrón Adapter: Traduce las respuestas foráneas en inglés del sensor externo
 * al modelo de dominio puro CondicionHidrica de AquaPort.
 */
public class AdaptadorAPIHidrica implements ProveedorCondicionesHidricas {

    public static final double NIVEL_AGUA_MINIMO = 0.5;
    public static final double NIVEL_AGUA_MAXIMO = 5.0;
    public static final double TURBIDEZ_MAXIMA = 50.0;

    private final ClienteAPIExternaHidrica clienteExterno;

    public AdaptadorAPIHidrica(ClienteAPIExternaHidrica clienteExterno) {
        this.clienteExterno = Objects.requireNonNull(clienteExterno, "El cliente externo no puede ser nulo.");
    }

    @Override
    public CondicionHidrica consultarCondiciones(String zona) {
        if (zona == null || zona.trim().isEmpty()) {
            throw new IllegalArgumentException("La zona no puede ser nula ni vacía.");
        }

        ClienteAPIExternaHidrica.RespuestaSensorExterno respuesta = clienteExterno.consultarSensor(zona);
        if (respuesta == null) {
            return new CondicionHidrica(0.0, 0.0, false);
        }

        boolean navegable = "ACTIVE".equalsIgnoreCase(respuesta.status())
                && respuesta.waterLevel() >= NIVEL_AGUA_MINIMO
                && respuesta.waterLevel() <= NIVEL_AGUA_MAXIMO
                && respuesta.turbidity() <= TURBIDEZ_MAXIMA;

        return new CondicionHidrica(respuesta.waterLevel(), respuesta.turbidity(), navegable);
    }
}
