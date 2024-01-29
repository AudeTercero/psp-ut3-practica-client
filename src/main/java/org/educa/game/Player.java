package org.educa.game;

import org.educa.games.GameDices;
import org.educa.games.Games;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 5555;
    private Socket socket;

    public Player(String name, String gameType) {
        super.setName(name);
    }

    @Override
    public void run() {

        System.out.println("Start player" + this.getName());
        List<String> players;

        try (Socket playerSocket = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress(HOST, PORT);
            playerSocket.connect(addr);

            sendDataServer(playerSocket);
            players = receiveResponse(playerSocket);

            if (players != null) {
                int idMatch;
                int rol;
                for (String s : players) {
                    if(this.getName().equalsIgnoreCase(s.split(",")[1])){
                        idMatch = Integer.parseInt(s.split(",")[0]);
                        rol = Integer.parseInt(s.split(",")[4]);
                        System.out.println("Partida: "+idMatch + " Eres: "+convertCodeRolToString(rol));
                    }else {
                        String auxNick = s.split(",")[1];
                        String auxHost = s.split(",")[2];
                        int auxPort = Integer.parseInt(s.split(",")[3]);
                        int auxRol = Integer.parseInt(s.split(",")[4]);
                        System.out.println(auxNick+": Host->"+auxHost+", Puerto->"+auxPort);
                    }


                }

            }


        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("No se ha podido conectar con el servidor");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    private String convertCodeRolToString(int rol){
        if(rol==0){
            return "Anfitrion";
        }else{
            return "Invitado";
        }
    }


    private void sendDataServer(Socket playerSocket) throws IOException {
        PrintWriter writer = new PrintWriter(playerSocket.getOutputStream(), true);
        Games gameDices = new GameDices();
        String dataPlayer = this.getName() + "," + gameDices.getName() + "," + gameDices.getPlayerNeeded();
        writer.println(dataPlayer);
        writer.flush();
        System.out.println("Enviando datos datos " + this.getName());


    }

    private ArrayList<String> receiveResponse(Socket playerSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());
        Object objectReceive = in.readObject();
        if (objectReceive instanceof ArrayList) {
            ArrayList<String> list = (ArrayList<String>) objectReceive;
            in.close();
            return list;
        }
        in.close();
        return null;
    }


    public void closeConnection() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
