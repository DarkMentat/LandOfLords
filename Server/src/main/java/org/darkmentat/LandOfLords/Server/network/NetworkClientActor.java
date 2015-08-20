package org.darkmentat.LandOfLords.Server.network;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;
import static org.darkmentat.LandOfLords.Server.network.FrontNetworkActor.*;

public class NetworkClientActor extends AbstractActor {
    public static class SendToClient {
        public final String Data;

        public SendToClient(String data) {
            Data = data;
        }
    }

    private final ActorRef mTcpSocket;
    public String mLogin;

    public NetworkClientActor(ActorRef tcpSocket) {
        mTcpSocket = tcpSocket;

        receive(ReceiveBuilder
                .match(Tcp.Received.class, this::onReceivedData)
                .match(Tcp.ConnectionClosed.class, this::onConnectionClosed)
                .match(SendToClient.class, this::onSendToClient)
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
            Message message = Message.parseFrom(data);
            handleReceivedMessage(message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            context().stop(self());
        }
    }
    private void onConnectionClosed(Tcp.ConnectionClosed closed) {
        context().stop(self());
    }
    private void onSendToClient(SendToClient data) {
        mTcpSocket.tell(TcpMessage.write(ByteString.fromArray((data.Data + "\n").getBytes())), self());
    }

    private void handleReceivedMessage(Message message) {
        switch (message.getType()){
            case PING:
                mTcpSocket.tell(TcpMessage.write(ByteString.fromArray(("[" + mLogin + "] echo ping\n").getBytes())), self());

                System.out.println("ping");
                break;

            case LOGIN:
                Login login = message.getLogin();
                mLogin = login.getLogin();

                context().parent().tell(new LoginClientActor(mLogin, self()), self());
                mTcpSocket.tell(TcpMessage.write(ByteString.fromArray(("Hello, " + mLogin + "\n").getBytes())), self());

                System.out.println(login);
                break;

            case REGISTER:
                System.out.println(message.getRegister());
                break;
        }
    }
}
