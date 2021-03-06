package hu.uni.miskolc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.utils.PopupWindowManager;

public class MenuScreen extends ScreenAdapter {

    private static ZombieGame screenManager;

    private Stage stage;
    private SpriteBatch batch;

    private Image background;
    private ImageButton newGameButton;
    private ImageButton optionsButton;
    private ImageButton exitButton;
    private ImageButton continueButton;

    private PopupWindowManager popupWindowManager;

    public static Music music;
    public static Sound buttonClick;

    private static Preferences saveFile;
    private boolean hasContinueOption;
    private boolean isMusicEnabled;
    private boolean areSoundsEnabled;

    public MenuScreen(ZombieGame screenManager, SpriteBatch batch)  {
        MenuScreen.screenManager = screenManager;
        this.batch = batch;
    }

    @Override
    public void show() {
        //Loading save if exists. If not, the continue button is grayed out
        saveFile = Gdx.app.getPreferences("config");
        hasContinueOption = saveFile.getBoolean("continue", false);
        isMusicEnabled = saveFile.getBoolean("music", true);
        areSoundsEnabled = saveFile.getBoolean("sounds", true);

        //Creating stage for UI elements
        stage = new Stage(new FitViewport(ZombieGame.WIDTH, ZombieGame.HEIGHT), batch);
        Gdx.input.setInputProcessor(stage);
        popupWindowManager = new PopupWindowManager(stage, saveFile);

        loadInSounds();
        createButtons();
        addButtonsToStage();
        addInputListenersToButtons();
    }

    @Override
    public void render(float delta) {
        clearScreen();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private void createButtons() {
        //Load in textures from atlas
        TextureAtlas buttons = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack"));

        //Wallpaper image
        background = new Image(new Texture(Gdx.files.internal("background/menuwall.jpg")));
        //New Game
        newGameButton = new ImageButton(new TextureRegionDrawable(buttons.getRegions().get(0)),
                new TextureRegionDrawable(buttons.getRegions().get(1)));
        //Options
        optionsButton = new ImageButton(new TextureRegionDrawable(buttons.getRegions().get(7)),
                new TextureRegionDrawable(buttons.getRegions().get(8)));
        //Exit
        exitButton = new ImageButton(new TextureRegionDrawable(buttons.getRegions().get(5)),
                new TextureRegionDrawable(buttons.getRegions().get(6)));
        //Continue (Check from savefile if available)
        if (hasContinueOption) {
            continueButton = new ImageButton(new TextureRegionDrawable(buttons.getRegions().get(2)),
                    new TextureRegionDrawable(buttons.getRegions().get(3)));
        } else  {
            continueButton = new ImageButton(new TextureRegionDrawable(buttons.getRegions().get(4)),
                    new TextureRegionDrawable(buttons.getRegions().get(4)));
        }
    }

    private void addButtonsToStage()    {
        //Adding actors (buttons) to the stage
        stage.addActor(background);
        stage.addActor(newGameButton);
        stage.addActor(optionsButton);
        stage.addActor(exitButton);
        stage.addActor(continueButton);

        //Positioning the buttons
        newGameButton.setPosition(7*stage.getWidth()/10, (4.5f)*stage.getHeight()/10, Align.center);
        continueButton.setPosition(7*stage.getWidth()/10, 7*stage.getHeight()/10, Align.center);
        optionsButton.setPosition(1*stage.getWidth()/10, 5*stage.getHeight()/10, Align.center);
        exitButton.setPosition(7*stage.getWidth()/10, 2*stage.getHeight()/10, Align.center);
    }

    private void addInputListenersToButtons()  {
        exitButton.addListener(new ActorGestureListener()   {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                buttonClick.play(ZombieGame.volume);
                Gdx.app.exit();
            }
        });
        newGameButton.addListener(new ActorGestureListener()    {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                buttonClick.play(ZombieGame.volume);
                popupWindowManager.createLevelSelectorPopup(screenManager, batch);
            }
        });
        optionsButton.addListener(new ActorGestureListener()    {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                buttonClick.play(ZombieGame.volume);
                popupWindowManager.createOptionsPopup();
            }
        });
        continueButton.addListener(new ActorGestureListener()   {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (hasContinueOption) {
                    buttonClick.play(ZombieGame.volume);
                    screenManager.setScreen(new GameScreen(screenManager, batch, (byte)saveFile.getInteger("levelNumber")));
                }
            }
        });
    }

    private void loadInSounds()   {
        //Load menu music and button "click" sound
        music = Gdx.audio.newMusic(Gdx.files.internal("music/menu.mp3"));
        if (isMusicEnabled) {
            music.play();
            music.setLooping(true);
        }
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonclick.mp3"));
        if (areSoundsEnabled)
            ZombieGame.volume = 1.0f;
    }

    @Override
    public void hide() {
        super.hide();
        saveFile.flush();
        dispose();
    }

    private void clearScreen()   {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        stage.dispose();
        music.dispose();
        buttonClick.dispose();
    }
}
