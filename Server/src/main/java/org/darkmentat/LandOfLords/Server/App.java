package org.darkmentat.LandOfLords.Server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;

import org.darkmentat.LandOfLords.Common.Test;
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

//        system.terminate();
    }
}
