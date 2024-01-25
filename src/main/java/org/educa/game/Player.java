package org.educa.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    private String gameType;
    private static final String HOST = "localhost";
    private static final int PORT = 5555;
    private Socket socket;

    public Player(String name, String gameType) {
        super.setName(name);
        this.gameType = gameType;
    }

    @Override
    public void run() {
        System.out.println("Start player");

        try {
            socket = new Socket(HOST, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(this.getName());
            System.out.println("Conexion realizada con exito");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
