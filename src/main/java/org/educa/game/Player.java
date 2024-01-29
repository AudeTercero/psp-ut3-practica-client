package org.educa.game;

import org.educa.games.GameDices;
import org.educa.games.Games;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Player que simula la accion de los jugadores
 * @author Iker Ruiz y Javier Villarta
 */
public class Player extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 5555;
    private Games gameDices = new GameDices();
    public static  int guestPort = 5558;
    public static  int hostPort = 5554;

    /**
     * Constructor de la clase Player
     * @param name nombre
     * @param gameType tipo de juego
     */
    public Player(String name, String gameType) {
        super.setName(name);
    }

    /**
     * Metodo que inicia el funcionamiento de un jugador
     */
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
                int idMatch = -1;
                int rol = -1;
                for (String s : players) {
                    if(this.getName().equalsIgnoreCase(s.split(",")[1])){
                        idMatch = Integer.parseInt(s.split(",")[0]);
                        rol = Integer.parseInt(s.split(",")[4]);
                        System.out.println("Partida: "+idMatch + " Eres: "+convertCodeRolToString(rol));

                    }else {
                        String auxNick = s.split(",")[1];
                        String auxHost = s.split(",")[2];
                        int auxPort = Integer.parseInt(s.split(",")[3]);
                        System.out.println(auxNick+": Host->"+auxHost+", Puerto->"+auxPort);
                    }

                }
                gameDices.play(rol,idMatch);
                sendFinalMatch(playerSocket,rol,idMatch);


            }


        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("No se ha podido conectar con el servidor");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Metodo que devuelve el puerto del invitado
     * @return
     */
    public static synchronized int getGuestPort(){
        return guestPort;
    }

    /**
     * Metodo que devuelve el puerto del host
     * @return
     */
    public static synchronized int getHostPort(){
        return hostPort;
    }

    /**
     * Metodod que convierte el rol numerico a String
     * @param rol rol numerico
     * @return String rol
     */
    private String convertCodeRolToString(int rol){
        if(rol==0){
            return "Anfitrion";
        }else{
            return "Invitado";
        }
    }

    /**
     * Metodo para inficar al servido el fin de la partida
     * @param playerSocket
     * @param role
     * @param idMatch
     * @throws IOException
     */
    private void sendFinalMatch(Socket playerSocket,int role,int idMatch)throws IOException{
        if(role==0){
            PrintWriter writer = new PrintWriter(playerSocket.getOutputStream(), true);
            String dataPlayer = idMatch +"," ;
            writer.println(dataPlayer);
            writer.flush();
            System.out.println("Anfitrion ha finalizado partida");
        }else{
            System.out.println("Invitado ha finalizado partida");
        }

    }

    /**
     * Metodo que envia los datos del jugador al servidor
     * @param playerSocket
     * @throws IOException
     */
    private void sendDataServer(Socket playerSocket) throws IOException {
        PrintWriter writer = new PrintWriter(playerSocket.getOutputStream(), true);
        Games gameDices = new GameDices();
        String dataPlayer = this.getName() + "," + gameDices.getName() + "," + gameDices.getPlayerNeeded();
        writer.println(dataPlayer);
        writer.flush();
        System.out.println("Enviando datos datos " + this.getName());


    }

    /**
     * Metodo que devuelve la informacion de los jugadores emparejados en el servidor
     * @param playerSocket
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
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



}
