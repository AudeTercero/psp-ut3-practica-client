package org.educa.games;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class GameDices implements Games{
    private static final int PLAYERS_NEEDED = 2;
    private static final String NAME = "Dices";


    @Override
    public void play(String rol) {
        if (("guest").equalsIgnoreCase(rol)) {
            InetSocketAddress addrGuest = new InetSocketAddress("localhost", 5556);
            try (DatagramSocket datagramSocket = new DatagramSocket(addrGuest)) {
                boolean draw = true;
                while (draw) {
                    int roll = (int) (Math.random() * 6) + 1;
                    System.out.println("Resultado de la tirada: " + roll);
                    sendResultDice(datagramSocket, roll);
                    String result = receiveGameResult(datagramSocket);
                    if(!("E").equalsIgnoreCase(result)){
                        draw = false;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            InetSocketAddress addrHost = new InetSocketAddress("localhost",5557);
            try (DatagramSocket datagramSocket = new DatagramSocket(addrHost)) {
                boolean draw = true;
                while (draw) {
                    int guestResult = receiveResultDice(datagramSocket);
                    int roll = (int) (Math.random() * 6) + 1;
                    System.out.println("Resultado de la tirada: " + roll);
                    String result = gameResult(guestResult,roll);
                    sendGameResult(datagramSocket,result);
                    if(!("E").equalsIgnoreCase(result)){
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
        DatagramPacket datagramPacket = new DatagramPacket(resultRoll.getBytes(),resultRoll.getBytes().length,adrToSend,5557);
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
        DatagramPacket datagramPacket = new DatagramPacket(gameResult.getBytes(),gameResult.getBytes().length,adrToSend,5556);
        datagramSocket.send(datagramPacket);
        System.out.println("Tirada enviada al Anfitrion");

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
    public int getPlayerNeeded(){
        return PLAYERS_NEEDED;
    }
    public String getName(){
        return NAME;
    }

}
