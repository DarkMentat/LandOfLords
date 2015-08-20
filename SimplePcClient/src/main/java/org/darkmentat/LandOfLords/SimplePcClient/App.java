package org.darkmentat.LandOfLords.SimplePcClient;

import org.darkmentat.LandOfLords.SimplePcClient.network.TCPClient;

import java.util.Scanner;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;

public class App {
    public static void main (String args[]) {

        TCPClient client = new TCPClient("localhost", 8080);

        client.setOnError(System.out::println);
        client.setOnReceiveData(System.out::println);

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
                client.send(null);
            }
        }

        client.close();
    }
}
