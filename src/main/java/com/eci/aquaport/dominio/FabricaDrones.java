package com.eci.aquaport.dominio;

/**
 * Patrón Factory Method: Centraliza la creación e instanciación de los
 * diferentes tipos especializados de drones acuáticos (Superficial, Semisumergido, Buceador).
 */
public class FabricaDrones {

    private FabricaDrones() {
        // Constructor privado para clase utilitaria/fabrica
    }

    public static DroneAcuatico crearDrone(
            TipoDrone tipo,
            String id,
            String modelo,
            int bateria,
            boolean disponible,
            String zona) {

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de drone no puede ser nulo.");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del drone no puede ser nulo ni vacío.");
        }

        return new DroneAcuatico(id, modelo, bateria, disponible, zona, tipo);
    }

    public static DroneAcuatico crearSuperficial(String id, int bateria, String zona) {
        return crearDrone(TipoDrone.SUPERFICIAL, id, "Aqua-Ranger Superficial", bateria, true, zona);
    }

    public static DroneAcuatico crearSemisumergido(String id, int bateria, String zona) {
        return crearDrone(TipoDrone.SEMISUMERGIDO, id, "Aqua-Sub Semisumergido", bateria, true, zona);
    }

    public static DroneAcuatico crearBuceador(String id, int bateria, String zona) {
        return crearDrone(TipoDrone.BUCEADOR, id, "Deep-Diver Buceador", bateria, true, zona);
    }
}
