package org.darkmentat.LandOfLords.core;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient.PingClient;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient.PlayerUnitState;
import org.darkmentat.LandOfLords.Common.NetMessagesToServer.Login;
import org.darkmentat.LandOfLords.Common.NetMessagesToServer.PingServer;
import org.darkmentat.LandOfLords.Common.NetMessagesToServer.Register;
import org.darkmentat.LandOfLords.Common.NetMessagesToServer.SpawnPlayerUnit;
import org.darkmentat.LandOfLords.core.network.TCPClient;
import org.darkmentat.LandOfLords.core.network.TCPClientListener;

public class GdxClientApp implements ApplicationListener, TCPClientListener {
	Texture texture;
	SpriteBatch batch;
	float elapsed;

    private TCPClient tcpClient;

	@Override
	public void create () {
		texture = new Texture(Gdx.files.internal("libgdx-logo.png"));
		batch = new SpriteBatch();

        tcpClient = new TCPClient("localhost", 8080);
        tcpClient.connect();
        tcpClient.setListener(this);


        // server will ignore messages if we send them without sleeping
        // Maybe we must delimit messages to send
        try {
            tcpClient.send(PingServer.newBuilder().build());
            Thread.sleep(100);
            tcpClient.send(Register.newBuilder().setLogin("DarkMentat").setEmail("indraua@gmail.com").build());
            Thread.sleep(100);
            tcpClient.send(Login.newBuilder().setLogin("DarkMentat").build());
            Thread.sleep(100);
            tcpClient.send(SpawnPlayerUnit.newBuilder().build());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		elapsed += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(texture, 100+100*(float)Math.cos(elapsed), 100+25*(float)Math.sin(elapsed));
		batch.end();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
        tcpClient.close();
	}



    @Override public void onSocketError(Exception exception) {
        exception.printStackTrace();
    }
    @Override public void onSocketClose() {
        System.out.println("socket closed");
    }
    @Override public void onSocketMessageReceive(PingClient ping) {
        System.out.println("ping received");
    }
    @Override public void onSocketMessageReceive(PlayerUnitState state) {
        System.out.print(state.getGameObjectState());
        System.out.println("\tX: " + state.getX() + " Y: " + state.getY());

        state.getCellsAroundList().forEach(cell -> {
            System.out.print("Cell: " + cell.getDescription() + " at (" + cell.getX() + ", " + cell.getY() + ")");

            if(cell.getUnitsCount() == 0){
                System.out.println(" with no units");
            } else {
                System.out.print(" with units: ");

                cell.getUnitsList()
                        .stream()
                        .reduce((unit, acc) -> acc += ", " + unit)
                        .ifPresent(System.out::println);
            }
        });

        System.out.println("\n");
    }
}
