package org.darkmentat.LandOfLords.Server.network;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.darkmentat.LandOfLords.Common.utils.FakeOutputStream;

import java.io.IOException;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.*;
import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;
import static org.darkmentat.LandOfLords.Server.network.FrontNetworkActor.*;

public class NetworkClientActor extends AbstractActor {

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

        if (mLogin != null) {
            context().parent().tell(new UnLoginClientActor(mLogin), self());
        }
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
        mTcpSocket.tell(TcpMessage.write(toByteString(packMessage(data))), self());
    }

    private void handleReceivedMessage(MessageToServer message) {
        switch (message.getType()){
            case PING_SERVER:
                pingClient();

                System.out.println("ping");
                break;

            case LOGIN:
                Login login = message.getLogin();
                mLogin = login.getLogin();

                context().parent().tell(new LoginClientActor(mLogin, self()), self());

                System.out.println(login);
                break;

            case REGISTER:
                System.out.println(message.getRegister());
                break;
        }
    }

    private void pingClient(){
        try {
            // Dirty hack to write delimited message to byte array
            FakeOutputStream fakeOutputStream = new FakeOutputStream();
            MessageToClient.newBuilder().setType(TypeToClient.PING_CLIENT).build().writeDelimitedTo(fakeOutputStream);
            mTcpSocket.tell(TcpMessage.write(toByteString(fakeOutputStream)), self());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteString toByteString(FakeOutputStream fos){
        return ByteString.fromArray(fos.getByteArray());
    }
    private ByteString toByteString(GeneratedMessage msg){
        return ByteString.fromArray(msg.toByteArray());
    }

    private MessageToClient packMessage(GeneratedMessage msg){

        if (msg == null) return MessageToClient.newBuilder().setType(TypeToClient.PING_CLIENT).build();


        throw new IllegalArgumentException("message must be from net_messages_to_server.proto");
    }
}
