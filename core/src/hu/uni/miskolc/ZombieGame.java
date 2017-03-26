package hu.uni.miskolc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import hu.uni.miskolc.screens.MenuScreen;

public class ZombieGame extends Game {

	public static float volume;
	public static final float WIDTH = 1280;
	public static final float HEIGHT = 720;
	public static final float PPM = 100;

	private SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this, batch));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
}
