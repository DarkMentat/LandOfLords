package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.darkmentat.LandOfLords.Server.gameMechanics.UserGameMechanicsActor.HeartbeatTick;

public class GameMechanicsActor extends AbstractActor {
    public static class StartUserGameMechanicsMsg {
        public final String Login;

        public StartUserGameMechanicsMsg(String login) {
            Login = login;
        }
    }

    public static final String ADDRESS = "akka://LandOfLordsServer/user/game_mechanics";
    public static final FiniteDuration HEARTBEAT = Duration.apply(1, TimeUnit.SECONDS);

    public GameMechanicsActor() {
        receive(ReceiveBuilder
                .match(StartUserGameMechanicsMsg.class, this::onStartUserGameMechanics)
                .build());

        ActorSystem system = context().system();
        system.scheduler().schedule(HEARTBEAT, HEARTBEAT, (Runnable) this::onHeartbeatTick, system.dispatcher());
    }

    private void onStartUserGameMechanics(StartUserGameMechanicsMsg msg) {
        if (!context().child("userGM_" + msg.Login).isDefined()) {
            getContext().actorOf(Props.create(UserGameMechanicsActor.class), "userGM_" + msg.Login);
        }
    }
    private void onHeartbeatTick(){
        getContext().getChildren().forEach(actor -> actor.tell(new HeartbeatTick(), self()));
    }
}
