package hu.uni.miskolc.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.states.GameState;
import hu.uni.miskolc.utils.PopupWindowManager;
import hu.uni.miskolc.utils.WaveManager;

public class Hud implements Disposable {

    private Stage stage;

    private int wave;
    private int livesLeft;
    private int money;

    private Label waveLabel;
    private Label moneyLabel;
    private TextureAtlas healthBarAtlas;
    private Image currentHealth;
    private ImageButton startWaveButton;
    private ImageButton pauseButton;
    private WaveManager waveManager;

    private PopupWindowManager popupWindowManager;

    public Hud(GameScreen gameScreen) {
        AssetManager assetManager = ZombieGame.getAssetManager();
        Viewport viewPort = new FitViewport(ZombieGame.WIDTH, ZombieGame.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewPort, ZombieGame.getSpriteBatch());
        TextureAtlas buttons = assetManager.get("buttons/buttons.pack");
        healthBarAtlas = assetManager.get("spritesheets/healthbar/healthbar.pack");

        popupWindowManager = new PopupWindowManager(stage);

        currentHealth = new Image(healthBarAtlas.findRegion(String.valueOf(livesLeft)));
        Image currentWave = new Image(healthBarAtlas.findRegion("wave"));
        Image currentMoney = new Image(healthBarAtlas.findRegion("money"));
        startWaveButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("forwardbutton")),
                new TextureRegionDrawable(buttons.findRegion("forwardbuttonpressed")));
        pauseButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("pausegamebutton")),
                new TextureRegionDrawable(buttons.findRegion("pausegamebuttonpressed")));
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("fonts/myfont2.fnt"));
        waveLabel = new Label(String.format("%01d", wave), new Label.LabelStyle(bitmapFont, Color.WHITE));
        moneyLabel = new Label(String.format("%03d", money), new Label.LabelStyle(bitmapFont, Color.WHITE));

        stage.addActor(currentHealth);
        stage.addActor(currentWave);
        stage.addActor(currentMoney);
        stage.addActor(waveLabel);
        stage.addActor(moneyLabel);
        stage.addActor(startWaveButton);
        stage.addActor(pauseButton);

        currentHealth.setPosition(stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
        currentWave.setPosition(stage.getWidth() / 2, 9.3f * stage.getHeight() / 10, Align.center);
        waveLabel.setPosition(0.93f * stage.getWidth() / 2, 9.3f * stage.getHeight() / 10, Align.center);
        currentMoney.setPosition(4 * stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
        moneyLabel.setPosition(4.05f * stage.getWidth() / 5, 9.3f * stage.getHeight() / 10, Align.center);
        startWaveButton.setPosition(4.6f * stage.getWidth() / 5, 1.3f * stage.getHeight() / 10, Align.center);
        pauseButton.setPosition(0.4f * stage.getWidth() / 5, 1.3f * stage.getHeight() / 10, Align.center);

        addOnClickListeners(gameScreen);
    }

    private void addOnClickListeners(final GameScreen gameScreen) {
        startWaveButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                gameScreen.setZombiesToSpawn(waveManager.getWave());
                wave++;
            }
        });

        pauseButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (gameScreen.getGameState().equals(GameState.RUNNING)) {
                    gameScreen.setState(GameState.PAUSED);
                    popupWindowManager.createPausePopup(gameScreen.getScreenManager(), gameScreen);
                } else {
                    gameScreen.setState(GameState.RUNNING);
                    stage.getActors().removeRange(stage.getActors().size - 5, stage.getActors().size - 1);
                }
            }
        });
    }

    public void doubleClickPopUp(GameScreen gameScreen) {
        gameScreen.setState(GameState.PAUSED);
        popupWindowManager.createTowerSelectorPopUp(gameScreen);
    }

    public void update(float delta) {
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

    public void createWaveManager() {
        waveManager = new WaveManager(0, money);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}