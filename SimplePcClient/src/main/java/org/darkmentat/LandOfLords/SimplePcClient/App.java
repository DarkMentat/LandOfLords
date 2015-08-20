package org.darkmentat.LandOfLords.SimplePcClient;

import org.darkmentat.LandOfLords.SimplePcClient.network.TCPClient;

import java.util.Scanner;

public class App {
    public static void main (String args[]) {

        TCPClient client = new TCPClient("localhost", 8080);

        client.connect();

        client.setOnError(System.out::println);
        client.setOnReceiveData(System.out::println);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String f = sc.nextLine();
            if (f.equals("exit")) {
                break;
            }else{
                client.send(f+"\n");
            }
        }

        client.close();
    }
}
