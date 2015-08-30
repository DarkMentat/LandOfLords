package org.darkmentat.LandOfLords.Server.network;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient;
import org.darkmentat.LandOfLords.Common.utils.FakeOutputStream;
import org.darkmentat.LandOfLords.Server.gameMechanics.GameMechanicsActor;
import org.darkmentat.LandOfLords.Server.gameMechanics.UserGameMechanicsActor;

import java.io.IOException;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.*;
import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;
import static org.darkmentat.LandOfLords.Server.network.FrontNetworkActor.*;

public class NetworkClientActor extends AbstractActor {
    public static class LoginClientActor {
        public final String Login;
        public final ActorRef Actor;

        public LoginClientActor(String login, ActorRef actor) {
            Login = login;
            Actor = actor;
        }
    }
    public static class UnLoginClientActor {
        public final String Login;

        public UnLoginClientActor(String login) {
            Login = login;
        }
    }

    private final ActorRef mTcpSocket;
    public String mLogin;

    public NetworkClientActor(ActorRef tcpSocket) {
        mTcpSocket = tcpSocket;

        receive(ReceiveBuilder
                .match(Tcp.Received.class, this::onReceivedData)
                .match(Tcp.ConnectionClosed.class, this::onConnectionClosed)
                .match(GeneratedMessage.class, this::onSendToClient)
                .build());
    }

    @Override public void postStop() throws Exception {
        super.postStop();

        mTcpSocket.tell(TcpMessage.confirmedClose(), self());

        unlogin();
    }
    private void onReceivedData(Tcp.Received received) {
        byte[] data = received.data().toArray();

        try {
            MessageToServer message = MessageToServer.parseFrom(data);
            handleReceivedMessage(message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            context().stop(self());
        }
    }
    private void onConnectionClosed(Tcp.ConnectionClosed closed) {
        context().stop(self());
    }
    private void onSendToClient(GeneratedMessage data) {
        mTcpSocket.tell(TcpMessage.write(serializeDelimitedMessage(packMessage(data))), self());
    }

    private void handleReceivedMessage(MessageToServer message) {
        switch (message.getType()){
            case PING_SERVER:
                pingClient();

                System.out.println("ping");
                break;

            case LOGIN:
                login(message.getLogin().getLogin());

                System.out.println(mLogin);
                break;

            case REGISTER:
                context().actorFor(GameMechanicsActor.ADDRESS)
                        .tell(new GameMechanicsActor.StartUserGameMechanicsMsg(message.getRegister().getLogin()), self());

                System.out.println(message.getRegister());
                break;

            case SPAWN_PLAYER_UNIT:
                context().actorFor(UserGameMechanicsActor.getPath(mLogin)).tell(message.getSpawnPlayerUnit(), self());
                break;

            case COMMAND_PLAYER_UNIT:
                context().actorFor(UserGameMechanicsActor.getPath(mLogin)).tell(message.getCommandPlayerUnit(), self());
                break;
        }
    }

    private void pingClient(){
        onSendToClient(PingClient.newBuilder().build());
    }

    private void login(String login){
        mLogin = login;

        context().actorFor(UserGameMechanicsActor.getPath(mLogin)).tell(new LoginClientActor(mLogin, self()), self());
    }
    private void unlogin(){
        if (mLogin != null) {
            context().actorFor(UserGameMechanicsActor.getPath(mLogin)).tell(new UnLoginClientActor(mLogin), self());

            mLogin = null;
        }
    }

    private ByteString toByteString(FakeOutputStream fos){
        return ByteString.fromArray(fos.getByteArray());
    }
    private ByteString toByteString(GeneratedMessage msg){
        return ByteString.fromArray(msg.toByteArray());
    }

    private MessageToClient packMessage(GeneratedMessage msg){

        if (msg instanceof PingClient) return MessageToClient.newBuilder().setPing((PingClient)msg).setType(TypeToClient.PING_CLIENT).build();
        if (msg instanceof PlayerUnitState)  return MessageToClient.newBuilder().setPlayerUnitState((PlayerUnitState) msg).setType(TypeToClient.PLAYER_UNIT_STATE).build();

        throw new IllegalArgumentException("message must be from net_messages_to_server.proto");
    }
    private ByteString serializeDelimitedMessage(MessageToClient msg){

        // Dirty hack to write delimited message to byte array
        FakeOutputStream fakeOutputStream = new FakeOutputStream();

        try {
            msg.writeDelimitedTo(fakeOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toByteString(fakeOutputStream);
    }
}
