package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class UserGameMechanicsActor extends AbstractActor {
    public static class HeartbeatTick {}

    public UserGameMechanicsActor() {
        receive(ReceiveBuilder
                .match(HeartbeatTick.class, this::onHeartbeatTick)
                .build());

    }
    private void onHeartbeatTick(HeartbeatTick tick) {
        System.out.println(self().path() + " received tick");
    }
}
