package org.educa;

import org.educa.game.Player;

/**
 * Clase principal que pone en funcionamiento la logica de los jugadores a modo de hilos
 * @author Iker Ruiz y Javier Villarta
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        for (int i = 1; i <= 10; i++) {
            Player player = new Player("Jugador" + i, "dados");
            player.start();
        }
    }
}