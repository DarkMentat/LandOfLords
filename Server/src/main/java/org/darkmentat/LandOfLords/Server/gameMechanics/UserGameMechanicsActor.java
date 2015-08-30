package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.GeneratedMessage;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.*;

import java.util.Map;
import java.util.Optional;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.PlayerUnitState;
import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.SpawnPlayerUnit;
import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.CommandPlayerUnit;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.LoginClientActor;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.UnLoginClientActor;


public class UserGameMechanicsActor extends AbstractActor {
    public static class HeartbeatTick {}

    public static String getPath(String login){
        return GameMechanicsActor.ADDRESS + "/userGM_" + login;
    }

    private final String mLogin;
    private final LuaMachine mLuaMachine;
    private final GameMapPerformer mMapPerformer;

    private Optional<ActorRef> mNetClient = Optional.empty();

    private Optional<PlayerUnit> mPlayerUnit = Optional.empty();

    public UserGameMechanicsActor(String login) {
        mLogin = login;

        receive(ReceiveBuilder
                .match(HeartbeatTick.class, this::onHeartbeatTick)
                .match(LoginClientActor.class, this::onLoginNetClient)
                .match(UnLoginClientActor.class, this::onUnLoginNetClient)
                .match(SpawnPlayerUnit.class, this::onSpawnPlayerUnit)
                .match(CommandPlayerUnit.class, this::onCommandPlayerUnit)
                .build());

        mLuaMachine = new LuaMachine();
        mMapPerformer = new GameMapPerformer();
    }
    private void onHeartbeatTick(HeartbeatTick tick) {
        mMapPerformer.performActions();

        mPlayerUnit.ifPresent(unit -> mNetClient.ifPresent(a -> a.tell(makeStateMsg(unit), self())));
    }

    private void onSpawnPlayerUnit(SpawnPlayerUnit msg) {
        PlayerUnit playerUnit = new PlayerUnit(mLogin, mLuaMachine.loadPlayerUnit());

        mMapPerformer.registerPositionable(playerUnit);

        mPlayerUnit = Optional.of(playerUnit);
    }
    private void onCommandPlayerUnit(CommandPlayerUnit msg) {
        mPlayerUnit.ifPresent(unit -> {
            String code = msg.getLuaCode();
            unit.eval(code);
        });
    }

    private void onLoginNetClient(LoginClientActor login) {
        mNetClient = Optional.of(login.Actor);
    }
    private void onUnLoginNetClient(UnLoginClientActor unlogin) {
        mNetClient = Optional.empty();
    }

    private GeneratedMessage makeStateMsg(PlayerUnit player){
        PlayerUnitState.Builder stateMsg = PlayerUnitState.newBuilder();

        stateMsg.setGameObjectState(player.getBasicState().name());

        stateMsg.setX(player.getX());
        stateMsg.setY(player.getY());

        for (Observational.CellInfo cell : player.getSurroundingsInfo()) {
            PlayerUnitState.CellInfo.Builder cellBuilder = PlayerUnitState.CellInfo.newBuilder()
                    .setX(cell.X)
                    .setY(cell.Y)
                    .setDescription(cell.Description);

            for (String pos : cell.getPositionables()) {
                cellBuilder.addUnits(pos);
            }

            stateMsg.addCellsAround(cellBuilder);
        }

        for (Map.Entry<String, String> entry : player.getStateValues().entrySet()) {
            stateMsg.addStateValue(PlayerUnitState.KeyValueTupple.newBuilder()
                    .setKey(entry.getKey())
                    .setValue(entry.getValue())
                    .build());
        }

        return stateMsg.build();
    }
}
