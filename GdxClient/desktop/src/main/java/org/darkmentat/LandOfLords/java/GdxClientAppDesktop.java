package org.darkmentat.LandOfLords.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.darkmentat.LandOfLords.core.GdxClientApp;

public class GdxClientAppDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GdxClientApp(), config);
	}
}
