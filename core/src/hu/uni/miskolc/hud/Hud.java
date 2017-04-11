package hu.uni.miskolc.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import hu.uni.miskolc.ZombieGame;

public class Hud implements Disposable {

    private Stage stage;

    private int wave;
    private int livesLeft;
    private int money;

    private Label waveLabel;
    private Label moneyLabel;
    private TextureAtlas healthBarAtlas;
    private Image currentHealth;

    public Hud(SpriteBatch batch, AssetManager assetManager) {
        Viewport viewPort = new FitViewport(ZombieGame.WIDTH, ZombieGame.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewPort, batch);

        healthBarAtlas = assetManager.get("spritesheets/healthbar/healthbar.pack");
        currentHealth = new Image(healthBarAtlas.findRegion(String.valueOf(livesLeft)));
        Image currentWave = new Image(healthBarAtlas.findRegion("wave"));
        Image currentMoney = new Image(healthBarAtlas.findRegion("money"));
        waveLabel = new Label(String.format("%01d", wave), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        moneyLabel = new Label(String.format("%03d", money), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        waveLabel.setFontScale(2.0f);
        moneyLabel.setFontScale(2.0f);

        stage.addActor(currentHealth);
        stage.addActor(currentWave);
        stage.addActor(currentMoney);
        stage.addActor(waveLabel);
        stage.addActor(moneyLabel);

        currentHealth.setPosition(stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
        currentWave.setPosition(stage.getWidth() / 2, 9.3f * stage.getHeight() / 10, Align.center);
        waveLabel.setPosition(0.95f * stage.getWidth() / 2, 9.3f * stage.getHeight() / 10, Align.center);
        currentMoney.setPosition(4 * stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
        moneyLabel.setPosition(4.05f * stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
    }

    public void update(float delta)    {
        moneyLabel.setText(money + "$");
        waveLabel.setText("Wave " + wave);
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
        currentHealth.setDrawable(new SpriteDrawable(new Sprite(healthBarAtlas.findRegion(String.valueOf(livesLeft)))));
    }

    public void decreaseLives() {
        livesLeft--;
        currentHealth.setDrawable(new SpriteDrawable(new Sprite(healthBarAtlas.findRegion(String.valueOf(livesLeft)))));
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
