package com.eci.aquaport.aplicacion;

import com.eci.aquaport.dominio.Mision;

public interface NotificadorOperador {

    void notificarMisionRegistrada(Mision mision);

    void notificarError(String mensaje);
}
