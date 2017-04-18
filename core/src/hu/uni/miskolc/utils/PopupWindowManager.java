package hu.uni.miskolc.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.screens.MenuScreen;
import hu.uni.miskolc.states.GameState;

public class PopupWindowManager {

    private Stage stage;
    private Preferences saveFile;
    private AssetManager assetManager;

    public PopupWindowManager(Stage stage, Preferences saveFile) {
        this.stage = stage;
        this.saveFile = saveFile;
        this.assetManager = ZombieGame.getAssetManager();
    }

    public PopupWindowManager(Stage stage) {
        this.stage = stage;
        this.assetManager = ZombieGame.getAssetManager();
    }

    public void createOptionsPopup() {
        if (stage.getActors().size == 5 || stage.getActors().size == 10) {
            Image popupWindow = new Image((Texture) assetManager.get("background/popup.png"));
            TextureAtlas buttons = assetManager.get("buttons/buttons.pack");

            ImageButton musicButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("musicoff")),
                    new TextureRegionDrawable(buttons.findRegion("musicon")), new TextureRegionDrawable(buttons.findRegion("musicon")));
            ImageButton soundButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("audiooff")),
                    new TextureRegionDrawable(buttons.findRegion("audioon")), new TextureRegionDrawable(buttons.findRegion("audioon")));
            ImageButton backButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("backbutton")),
                    new TextureRegionDrawable(buttons.findRegion("backbuttonpressed")));
            BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
            Label soundLabel = new Label("Sounds:", new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label musicLabel = new Label("Music:", new Label.LabelStyle(bitmapFont, Color.WHITE));

            stage.addActor(popupWindow);
            stage.addActor(musicButton);
            stage.addActor(soundButton);
            stage.addActor(soundLabel);
            stage.addActor(musicLabel);
            stage.addActor(backButton);

            popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
            musicButton.setPosition(6 * stage.getWidth() / 10, 6 * stage.getHeight() / 10, Align.center);
            musicLabel.setPosition(4 * stage.getWidth() / 10, 6 * stage.getHeight() / 10, Align.center);
            soundButton.setPosition(6 * stage.getWidth() / 10, 4 * stage.getHeight() / 10, Align.center);
            soundLabel.setPosition(4 * stage.getWidth() / 10, 4 * stage.getHeight() / 10, Align.center);
            backButton.setPosition(5 * stage.getWidth() / 10, 1.5f * stage.getHeight() / 10, Align.center);

            if (saveFile.getBoolean("music", true))
                musicButton.setChecked(true);
            if (saveFile.getBoolean("sounds", true))
                soundButton.setChecked(true);

            backButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    stage.getActors().removeRange(stage.getActors().size - 6, stage.getActors().size - 1);
                    MenuScreen.buttonClick.play(ZombieGame.volume);
                }
            });

            soundButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    if (saveFile.getBoolean("sounds", true)) {
                        ZombieGame.volume = 0.0f;
                        saveFile.putBoolean("sounds", false);
                        saveFile.flush();
                    } else {
                        ZombieGame.volume = 1.0f;
                        saveFile.putBoolean("sounds", true);
                        saveFile.flush();
                    }
                    MenuScreen.buttonClick.play(ZombieGame.volume);
                }
            });

            musicButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    if (saveFile.getBoolean("music", true)) {
                        MenuScreen.music.stop();
                        saveFile.putBoolean("music", false);
                        saveFile.flush();
                    } else {
                        MenuScreen.music.play();
                        MenuScreen.music.setLooping(true);
                        saveFile.putBoolean("music", true);
                        saveFile.flush();
                    }
                    MenuScreen.buttonClick.play(ZombieGame.volume);
                }
            });
        } else {
            stage.getActors().removeRange(stage.getActors().size - 6, stage.getActors().size - 1);
            saveFile.flush();
        }
    }

    public void createLevelSelectorPopup(final ZombieGame screenManager) {
        TextureAtlas buttons = assetManager.get("buttons/buttons.pack");
        Image popupWindow = new Image((Texture) assetManager.get("background/popup.png"));

        ImageButton firstButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttons.findRegion("map1thumbnail"))));
        ImageButton secondButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttons.findRegion("map2thumbnail"))));
        ImageButton thirdButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttons.findRegion("map3thumbnail"))));
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("backbutton")),
                new TextureRegionDrawable(buttons.findRegion("backbuttonpressed")));
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
        Label levelLabel = new Label("Select a map:", new Label.LabelStyle(bitmapFont, Color.WHITE));

        stage.addActor(popupWindow);
        stage.addActor(firstButton);
        stage.addActor(secondButton);
        stage.addActor(thirdButton);
        stage.addActor(backButton);
        stage.addActor(levelLabel);

        popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        firstButton.setPosition(3.8f * stage.getWidth() / 10, 6.2f * stage.getHeight() / 10, Align.center);
        secondButton.setPosition(3.8f * stage.getWidth() / 10, 3.8f * stage.getHeight() / 10, Align.center);
        thirdButton.setPosition(6.2f * stage.getWidth() / 10, 3.8f * stage.getHeight() / 10, Align.center);
        backButton.setPosition(5 * stage.getWidth() / 10, 1.5f * stage.getHeight() / 10, Align.center);
        levelLabel.setPosition(6.2f * stage.getWidth() / 10, 6.2f * stage.getHeight() / 10, Align.center);

        firstButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                saveFile.putBoolean("continue", true);
                resetInGameValues();
                screenManager.setScreen(new GameScreen(screenManager, (byte) 1));
            }
        });
        secondButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                saveFile.putBoolean("continue", true);
                resetInGameValues();
                screenManager.setScreen(new GameScreen(screenManager, (byte) 2));
            }
        });
        thirdButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                saveFile.putBoolean("continue", true);
                resetInGameValues();
                screenManager.setScreen(new GameScreen(screenManager, (byte) 3));
            }
        });
        backButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                stage.getActors().removeRange(stage.getActors().size - 6, stage.getActors().size - 1);
                MenuScreen.buttonClick.play(ZombieGame.volume);
            }
        });
    }

    public void createPausePopup(final ZombieGame screenManager, final GameScreen screen) {
        Image popupWindow = new Image((Texture) assetManager.get("background/popup.png"));
        TextureAtlas buttons = assetManager.get("buttons/buttons.pack");

        //Exit
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("exitbutton")),
                new TextureRegionDrawable(buttons.findRegion("exitbuttonpressed")));
        //Continue
        ImageButton continueButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("continuebutton")),
                new TextureRegionDrawable(buttons.findRegion("continuebuttonpressed")));
        ImageButton debugButton;

        if (screen.isShowDebugLines()) {
            debugButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("tickbutton")),
                    new TextureRegionDrawable(buttons.findRegion("tickbuttonpressed")), new TextureRegionDrawable(buttons.findRegion("xbutton")));
        } else {
            debugButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("xbutton")),
                    new TextureRegionDrawable(buttons.findRegion("xbuttonpressed")), new TextureRegionDrawable(buttons.findRegion("tickbuttonpressed")));
        }

        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
        Label debugLabel = new Label("Debug lines:", new Label.LabelStyle(bitmapFont, Color.WHITE));

        stage.addActor(popupWindow);
        stage.addActor(continueButton);
        stage.addActor(exitButton);
        stage.addActor(debugButton);
        stage.addActor(debugLabel);

        continueButton.setPosition(stage.getWidth() / 2, 3.5f * stage.getHeight() / 5, Align.center);
        exitButton.setPosition(stage.getWidth() / 2, 2.5f * stage.getHeight() / 5, Align.center);
        debugButton.setPosition(3 * stage.getWidth() / 5, 1.5f * stage.getHeight() / 5, Align.center);
        debugLabel.setPosition(2 * stage.getWidth() / 5, 1.5f * stage.getHeight() / 5, Align.center);
        popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);

        continueButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                stage.getActors().removeRange(stage.getActors().size - 5, stage.getActors().size - 1);
                screen.setState(GameState.RUNNING);
                MenuScreen.buttonClick.play(ZombieGame.volume);
            }
        });
        exitButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                screenManager.setScreen(new MenuScreen(screenManager));
                MenuScreen.buttonClick.play(ZombieGame.volume);
            }
        });
        debugButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (screen.isShowDebugLines())
                    screen.setShowDebugLines(false);
                else
                    screen.setShowDebugLines(true);
            }
        });
    }

    public void createTowerSelectorPopUp(final GameScreen screen) {
        Image popupWindow = new Image((Texture) assetManager.get("background/popup.png"));
        TextureAtlas buttons = assetManager.get("buttons/buttons.pack");
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(buttons.findRegion("backbutton")),
                new TextureRegionDrawable(buttons.findRegion("backbuttonpressed")));

        stage.addActor(popupWindow);
        stage.addActor(backButton);

        popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        backButton.setPosition(5 * stage.getWidth() / 10, 1.5f * stage.getHeight() / 10, Align.center);

        backButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                stage.getActors().removeRange(stage.getActors().size - 2, stage.getActors().size - 1);
                screen.setState(GameState.RUNNING);
                MenuScreen.buttonClick.play(ZombieGame.volume);
            }
        });
    }

    private void resetInGameValues() {
        saveFile.putInteger("currentLivesLeft", 10);
        saveFile.putInteger("currentWave", 1);
        saveFile.putInteger("currentMoney", 1000);
    }
}
