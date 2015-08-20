package org.darkmentat.LandOfLords.Server.network;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;

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

        if (mLogin != null) {
            context().parent().tell(new FrontNetworkActor.UnLoginClientActor(mLogin), self());
        }
    }
    private void onReceivedData(Tcp.Received received) {
        String data = received.data().utf8String().trim();

        if(data.matches("login .+$")){
            mLogin = data.substring(6).trim();

            context().parent().tell(new FrontNetworkActor.LoginClientActor(mLogin, self()), self());
            mTcpSocket.tell(TcpMessage.write(ByteString.fromArray(("Hello, " + mLogin + "\n").getBytes())), self());
        }
    }
    private void onConnectionClosed(Tcp.ConnectionClosed closed) {
        context().stop(self());
    }
    private void onSendToClient(SendToClient data) {
        mTcpSocket.tell(TcpMessage.write(ByteString.fromArray((data.Data + "\n").getBytes())), self());
    }
}
