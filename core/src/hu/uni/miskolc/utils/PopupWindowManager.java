package hu.uni.miskolc.utils;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.screens.MenuScreen;

public class PopupWindowManager {

    private Stage stage;
    private Preferences saveFile;
    private AssetManager assetManager;

    public PopupWindowManager(Stage stage, Preferences saveFile, AssetManager assetManager) {
        this.stage = stage;
        this.saveFile = saveFile;
        this.assetManager = assetManager;
    }

    public void createOptionsPopup()    {
        if (stage.getActors().size == 5 || stage.getActors().size == 10) {
            TextureAtlas optionsButtons = assetManager.get("buttons/optionsbuttons.pack");

            Image popupWindow = new Image((Texture) assetManager.get("background/optionspopup.png"));
            ImageButton musicButton = new ImageButton(new TextureRegionDrawable(optionsButtons.getRegions().get(4)),
                    new TextureRegionDrawable(optionsButtons.getRegions().get(5)), new TextureRegionDrawable(optionsButtons.getRegions().get(5)));
            ImageButton soundButton = new ImageButton(new TextureRegionDrawable(optionsButtons.getRegions().get(0)),
                    new TextureRegionDrawable(optionsButtons.getRegions().get(1)), new TextureRegionDrawable(optionsButtons.getRegions().get(1)));
            ImageButton backButton = new ImageButton(new TextureRegionDrawable(optionsButtons.getRegions().get(2)),
                    new TextureRegionDrawable(optionsButtons.getRegions().get(3)));

            stage.addActor(popupWindow);
            stage.addActor(musicButton);
            stage.addActor(soundButton);
            stage.addActor(backButton);

            popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
            musicButton.setPosition(6 * stage.getWidth() / 10, 6 * stage.getHeight() / 10, Align.center);
            soundButton.setPosition(6 * stage.getWidth() / 10, 4 * stage.getHeight() / 10, Align.center);
            backButton.setPosition(5 * stage.getWidth() / 10, 1.5f * stage.getHeight() / 10, Align.center);

            if (saveFile.getBoolean("music", true))
                musicButton.setChecked(true);
            if (saveFile.getBoolean("sounds", true))
                soundButton.setChecked(true);

            backButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    stage.getActors().removeRange(stage.getActors().size - 4, stage.getActors().size - 1);
                    saveFile.flush();
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
                    } else {
                        ZombieGame.volume = 1.0f;
                        saveFile.putBoolean("sounds", true);
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
                    } else {
                        MenuScreen.music.play();
                        MenuScreen.music.setLooping(true);
                        saveFile.putBoolean("music", true);
                    }
                    MenuScreen.buttonClick.play(ZombieGame.volume);
                }
            });
        } else  {
            stage.getActors().removeRange(stage.getActors().size - 4, stage.getActors().size - 1);
            saveFile.flush();
        }
    }

    public void createLevelSelectorPopup(final ZombieGame screenManager, final SpriteBatch batch)   {

        TextureAtlas levelButtons = assetManager.get("buttons/levelthumbnails/levelthumbnails.pack");
        TextureAtlas optionsButtons = assetManager.get("buttons/optionsbuttons.pack");

        Image popupWindow = new Image((Texture) assetManager.get("background/levelpopup.png"));
        ImageButton firstButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(levelButtons.getRegions().get(0))));
        ImageButton secondButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(levelButtons.getRegions().get(1))));
        ImageButton thirdButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(levelButtons.getRegions().get(2))));
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(optionsButtons.getRegions().get(2)),
                new TextureRegionDrawable(optionsButtons.getRegions().get(3)));

        stage.addActor(popupWindow);
        stage.addActor(firstButton);
        stage.addActor(secondButton);
        stage.addActor(thirdButton);
        stage.addActor(backButton);

        popupWindow.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        firstButton.setPosition(3.8f*stage.getWidth()/10, 6.2f*stage.getHeight()/10, Align.center);
        secondButton.setPosition(3.8f*stage.getWidth()/10, 3.8f*stage.getHeight()/10, Align.center);
        thirdButton.setPosition(6.2f*stage.getWidth()/10, 3.8f*stage.getHeight()/10, Align.center);
        backButton.setPosition(5 * stage.getWidth() / 10, 1.5f * stage.getHeight() / 10, Align.center);

        firstButton.addListener(new ActorGestureListener()  {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                screenManager.setScreen(new GameScreen(screenManager, batch, (byte)1));
                saveFile.putBoolean("continue", true);
            }
        });
        secondButton.addListener(new ActorGestureListener()  {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                screenManager.setScreen(new GameScreen(screenManager, batch, (byte)2));
                saveFile.putBoolean("continue", true);
            }
        });
        thirdButton.addListener(new ActorGestureListener()  {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                MenuScreen.buttonClick.play(ZombieGame.volume);
                screenManager.setScreen(new GameScreen(screenManager, batch, (byte)3));
                saveFile.putBoolean("continue", true);
            }
        });
        backButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                stage.getActors().removeRange(stage.getActors().size - 5, stage.getActors().size - 1);
                MenuScreen.buttonClick.play(ZombieGame.volume);
            }
        });
    }
}
