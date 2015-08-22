package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.GeneratedMessage;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.*;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.SpawnPlayerUnit;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.LoginClientActor;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.UnLoginClientActor;


public class UserGameMechanicsActor extends AbstractActor {
    public static class HeartbeatTick {}

    public static String getPath(String login){
        return GameMechanicsActor.ADDRESS + "/userGM_" + login;
    }

    private final String mLogin;
    private final Globals mLuaGlobals;

    private Optional<ActorRef> mNetClient = Optional.empty();

    private Optional<GameObject> mPlayerUnit = Optional.empty();

    private Set<GameObject> mMovingGameObjects = new HashSet<>();

    public UserGameMechanicsActor(String login) {
        mLogin = login;

        receive(ReceiveBuilder
                .match(HeartbeatTick.class, this::onHeartbeatTick)
                .match(LoginClientActor.class, this::onLoginNetClient)
                .match(UnLoginClientActor.class, this::onUnLoginNetClient)
                .match(SpawnPlayerUnit.class, this::onSpawnPlayerUnit)
                .build());

        mLuaGlobals = JsePlatform.standardGlobals();
    }
    private void onHeartbeatTick(HeartbeatTick tick) {
        mMovingGameObjects.forEach(go -> go.FullState.invokemethod("onTick"));
        mMovingGameObjects.removeIf(go -> go.BasicState != GameObject.GameObjectState.MOVING);

        mPlayerUnit.ifPresent(unit -> mNetClient.ifPresent(a -> a.tell(makeStateMsg(unit), self())));
    }

    private void onSpawnPlayerUnit(SpawnPlayerUnit msg) {
        GameObject playerUnit = new GameObject(mLogin, "Player");
        playerUnit.BasicState = GameObject.GameObjectState.MOVING;

        playerUnit.Parameters = mLuaGlobals.load("return {strength = 5, intelligence = 7, agility = 6}").call();
        playerUnit.Skills = mLuaGlobals.load("return {moving_afoot = 10}").call();
        playerUnit.Items = mLuaGlobals.load("return {'knife'}").call();
        playerUnit.FullState = mLuaGlobals.load("state = { x = 0, y = 0, speed = 1, destination_x = 10, destination_y = 0 } function state:onTick() self.x = self.x + 1 end return state").call();

        playerUnit.Behaviour = mLuaGlobals.load("return function(stimulus)  end").call();
        playerUnit.Stimuli = mLuaGlobals.load("return {}").call();

        mMovingGameObjects.add(playerUnit);

        mPlayerUnit = Optional.of(playerUnit);
    }

    private void onLoginNetClient(LoginClientActor login) {
        mNetClient = Optional.of(login.Actor);
    }
    private void onUnLoginNetClient(UnLoginClientActor unlogin) {
        mNetClient = Optional.empty();
    }

    private GeneratedMessage makeStateMsg(GameObject player){
        return NetMessagesToClient.PlayerUnitState.newBuilder()
                .setX(player.FullState.get("x").toint())
                .setY(player.FullState.get("y").toint())
                .build();
    }
}
