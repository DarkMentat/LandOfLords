package org.darkmentat.LandOfLords.Server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import org.darkmentat.LandOfLords.Server.gameMechanics.GameMechanicsActor;
import org.darkmentat.LandOfLords.Server.network.FrontNetworkActor;

public class App {

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("LandOfLordsServer");

        system.actorOf(Props.create(FrontNetworkActor.class), "networking");
        system.actorOf(Props.create(GameMechanicsActor.class), "game_mechanics");

//        system.terminate();
    }
}
