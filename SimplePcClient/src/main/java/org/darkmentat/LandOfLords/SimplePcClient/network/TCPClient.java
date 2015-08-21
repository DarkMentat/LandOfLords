package org.darkmentat.LandOfLords.SimplePcClient.network;

import com.google.protobuf.GeneratedMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.*;
import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.*;

public class TCPClient {
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private final String mHost;
    private final int mPort;

    private Socket mSocket;
    private DataInputStream mInputStream;
    private DataOutputStream mOutputStream;

    private Optional<TCPClientListener> mClientListener = Optional.empty();

    public TCPClient(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public void setListener(TCPClientListener listener) {
        mClientListener = Optional.of(listener);
    }

    public void connect(){
        try {
            mSocket = new Socket(mHost, mPort);
            mInputStream = new DataInputStream(mSocket.getInputStream());
            mOutputStream = new DataOutputStream(mSocket.getOutputStream());

            mExecutor.submit((Runnable) this::receive);
        }
        catch (IOException e) {
            mClientListener.ifPresent(c -> c.onError(e));
        }
    }
    public void close(){
        if (mSocket == null) return;

        try {
            mSocket.close();
            mExecutor.shutdownNow();
            mExecutor.awaitTermination(100, TimeUnit.MICROSECONDS);
        }
        catch (Exception e) {
            mClientListener.ifPresent(c -> c.onError(e));
        }
    }

    public void send(GeneratedMessage message){
        try {
            packMessage(message).writeTo(mOutputStream);
        } catch (IOException e) {
            mClientListener.ifPresent(c -> c.onError(e));
        }
    }
    public void receive(){
        while (!Thread.currentThread().isInterrupted()) {
            mClientListener.ifPresent(client -> {
                try {
                    unpackMessageToListener(MessageToClient.parseDelimitedFrom(mInputStream), client);
                } catch (IOException e) {
                    if(!mSocket.isClosed()) // I have no idea how to close socket without exception
                        client.onError(e);
                }
            });
        }
    }


    private MessageToServer packMessage(GeneratedMessage msg){

        if (msg instanceof PingServer) return MessageToServer.newBuilder().setType(TypeToServer.PING_SERVER).build();
        if (msg instanceof Login) return MessageToServer.newBuilder().setType(TypeToServer.LOGIN).setLogin((Login) msg).build();
        if (msg instanceof Register) return MessageToServer.newBuilder().setType(TypeToServer.REGISTER).setRegister((Register) msg).build();


        throw new IllegalArgumentException("message must be from net_messages_to_server.proto");
    }
    private void unpackMessageToListener(MessageToClient msg, TCPClientListener listener){
        switch (msg.getType()) {
            case PING_CLIENT:
                listener.onReceive(msg.getPing());
                break;
        }
    }
}


