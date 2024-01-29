package org.educa.games;

import org.educa.game.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Clase que simula el juego de los dados
 * @author Iker Ruiz y Javier Villarta Martinez
 */
public class GameDices implements Games{
    private static final int PLAYERS_NEEDED = 2;
    private static final String NAME = "Dices";
    private int guestPort;
    private int hostPort;



    @Override
    public void play(int role,int idMatch) throws InterruptedException {
        if (role == 1) {
            Thread.sleep(2000);
            this.guestPort = Player.getGuestPort()+idMatch+100;
            this.hostPort = Player.getHostPort()+idMatch;
            System.out.println("Estoy tirando para mandarselo al hostport "+hostPort);
            InetSocketAddress addrGuest = new InetSocketAddress("localhost", guestPort);

            try (DatagramSocket datagramSocket = new DatagramSocket(addrGuest)) {
                boolean draw = true;
                while (draw) {
                    int roll = (int) (Math.random() * 6) + 1;
                    System.out.println("Resultado de la tirada: " + roll);
                    sendResultDice(datagramSocket, roll);
                    String result = receiveGameResult(datagramSocket);
                    System.out.println(winner(result));
                    if (!("E").equalsIgnoreCase(result)) {
                        draw = false;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            this.hostPort = Player.getHostPort()+idMatch;
            this.guestPort = Player.getGuestPort()+idMatch+100;
            System.out.println("esperando a invitado "+hostPort);
            InetSocketAddress addrHost = new InetSocketAddress("localhost", hostPort);
            try (DatagramSocket datagramSocket = new DatagramSocket(addrHost)) {
                boolean draw = true;
                while (draw) {

                    int guestResult = receiveResultDice(datagramSocket);
                    System.out.println(""+guestResult);
                    int roll = (int) (Math.random() * 6) + 1;
                    System.out.println("Resultado de la tirada: " + roll);
                    String result = gameResult(guestResult, roll);
                    System.out.println(winner(result));
                    sendGameResult(datagramSocket, result);
                    if (!("E").equalsIgnoreCase(result)) {
                        draw = false;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void sendResultDice(DatagramSocket datagramSocket, int roll) throws IOException {
        InetAddress adrToSend = InetAddress.getByName("localhost");
        String resultRoll = String.valueOf(roll);
        DatagramPacket datagramPacket = new DatagramPacket(resultRoll.getBytes(),resultRoll.getBytes().length,adrToSend,hostPort);
        datagramSocket.send(datagramPacket);
        System.out.println("Tirada enviada al Anfitrion");
    }
    private int receiveResultDice(DatagramSocket datagramSocket) throws IOException{
        byte[] msg = new byte[100];
        DatagramPacket datagrama = new DatagramPacket(msg, msg.length);
        datagramSocket.receive(datagrama);
        int guestResult = Integer.parseInt(new String (datagrama.getData(),0,datagrama.getLength()));
        return guestResult;
    }
    private void sendGameResult(DatagramSocket datagramSocket, String gameResult) throws IOException{
        InetAddress adrToSend = InetAddress.getByName("localhost");
        DatagramPacket datagramPacket = new DatagramPacket(gameResult.getBytes(),gameResult.getBytes().length,adrToSend,guestPort);
        datagramSocket.send(datagramPacket);
        System.out.println("Resultado enviado al invitado");

    }
    private String receiveGameResult(DatagramSocket datagramSocket)throws IOException{
        byte[] msg = new byte[100];
        DatagramPacket datagrama = new DatagramPacket(msg, msg.length);
        datagramSocket.receive(datagrama);
        String gameResult = new String (datagrama.getData(),0,datagrama.getLength());
        return gameResult;
    }
    private String gameResult(int guestResult, int hostResult){
        if(guestResult<hostResult){
            return "V";
        }else if(guestResult>hostResult){
            return "D";
        }else{
            return "E";
        }
    }
    private String winner(String result){
        if(("E").equalsIgnoreCase(result)){
            return "Empate";
        }else if(("V").equalsIgnoreCase(result)){
            return "Ha ganado Anfitrion";

        }else {
            return "Ha ganado Invitado";
        }
    }
    public int getPlayerNeeded(){
        return PLAYERS_NEEDED;
    }
    public String getName(){
        return NAME;
    }

}
