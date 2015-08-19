package org.darkmentat.LandOfLords.Server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;

import org.darkmentat.LandOfLords.Server.network.FrontNetworkActor;
import org.darkmentat.LandOfLords.Server.network.NetworkClientActor;
import scala.concurrent.duration.Duration;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("LandOfLordsServer");

        ActorRef networking = system.actorOf(Props.create(FrontNetworkActor.class), "networking");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String f = sc.nextLine();
            if (f.equals("exit")) break;
            if (f.matches("2login .+: .+$")){
                Pattern pattern = Pattern.compile("2login (.+): (.+)");
                Matcher matcher = pattern.matcher(f);

                if (matcher.find()) {
                    String login = matcher.group(1);
                    String msg = matcher.group(2);

                    Inbox inbox = Inbox.create(system);
                    inbox.send(networking, new FrontNetworkActor.GetClientActor(login));
                    ActorRef client = (ActorRef) inbox.receive(Duration.create(1, TimeUnit.SECONDS));

                    client.tell(new NetworkClientActor.SendToClient(msg), ActorRef.noSender());
                }
            }
        }

        system.terminate();
    }
}
