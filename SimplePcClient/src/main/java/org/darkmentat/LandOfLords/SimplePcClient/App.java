package org.darkmentat.LandOfLords.SimplePcClient;

import org.darkmentat.LandOfLords.Common.NetMessagesToClient;
import org.darkmentat.LandOfLords.SimplePcClient.network.TCPClient;
import org.darkmentat.LandOfLords.SimplePcClient.network.TCPClientListener;

import java.util.Scanner;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;

public class App {
    public static void main (String args[]) {

        TCPClient client = new TCPClient("localhost", 8080);

        client.setListener(new TCPClientListener() {
            @Override public void onError(Exception exception) {
                exception.printStackTrace();
            }
            @Override public void onReceive(NetMessagesToClient.PingClient ping) {
                System.out.print("ping");
            }
            @Override public void onReceive(NetMessagesToClient.PlayerUnitState state) {
                System.out.print(state.getGameObjectState());
                System.out.print("\tX: ");
                System.out.print(state.getStateValueList().stream().filter(kv -> kv.getKey().equals("X")).findAny().get().getValue().substring(0,6));
                System.out.print("\tY: ");
                System.out.print(state.getStateValueList().stream().filter(kv -> kv.getKey().equals("Y")).findAny().get().getValue().substring(0, 6));
                System.out.println();
            }
            @Override public void onClose() {
                System.out.println("closing socket");
            }
        });

        client.connect();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String f = sc.nextLine();
            if (f.equals("exit")) {
                break;
            }

            String[] split = f.split(" ");

            if(split.length == 2 && split[0].equals("login")){
                client.send(Login.newBuilder().setLogin(split[1]).build());
            }

            if(split.length == 3 && split[0].equals("register")){
                client.send(Register.newBuilder().setLogin(split[1]).setEmail(split[2]).build());
            }

            if(split.length == 1 && split[0].equals("ping")){
                client.send(PingServer.newBuilder().build());
            }

            if(split.length == 1 && split[0].equals("spawn")){
                client.send(SpawnPlayerUnit.newBuilder().build());
            }
        }

        client.close();
    }
}
