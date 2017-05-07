package hu.uni.miskolc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import hu.uni.miskolc.ZombieGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int) ZombieGame.WIDTH;
        config.height = (int) ZombieGame.HEIGHT;
        config.useGL30 = false;
        new LwjglApplication(new ZombieGame(), config);
    }
}
