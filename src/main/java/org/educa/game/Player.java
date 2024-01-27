package org.educa.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if (!startConnection()) {
            System.out.println("No se pudo conectar con el servidor.");
        } else {
            //Logica del cliente
        }


    }

    public boolean startConnection() {
        try {
            socket = new Socket(HOST, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(this.getName());
            System.out.println("Conexion realizada con exito");
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }

    }

    public void closeConnection() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
