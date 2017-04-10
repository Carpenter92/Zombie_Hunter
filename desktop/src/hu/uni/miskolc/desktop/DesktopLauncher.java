package hu.uni.miskolc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import hu.uni.miskolc.ZombieGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) ZombieGame.WIDTH;
		config.height = (int) ZombieGame.HEIGHT;
		config.foregroundFPS = 60;
		//config.fullscreen = true;
		new LwjglApplication(new ZombieGame(), config);
	}
}
