package com.eci.aquaport.dominio;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Decorador concreto que aplica cifrado y seguridad a las transmisiones de telemetría.
 */
public class DroneConCifrado extends DecoradorDrone {

    public DroneConCifrado(DroneOperativo decorado) {
        super(decorado);
    }

    @Override
    public String transmitirTelemetria() {
        String original = super.transmitirTelemetria();
        return cifrarPayload(original);
    }

    private String cifrarPayload(String mensaje) {
        String codificado = Base64.getEncoder().encodeToString(mensaje.getBytes(StandardCharsets.UTF_8));
        return "ENC[AES-256]:" + codificado;
    }
}
