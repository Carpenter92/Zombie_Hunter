package hu.uni.miskolc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.hud.Hud;
import hu.uni.miskolc.sprites.Tower;
import hu.uni.miskolc.sprites.Zombie;
import hu.uni.miskolc.utils.Box2DObjectCreator;
import hu.uni.miskolc.utils.ZombieContactListener;

public class GameScreen extends InputAdapter implements Screen {

    private static final int MAP_OFFSET_X = 128; //128
    private static final int MAP_OFFSET_Y = 64; //64

    public static final short DYNAMIC_ENTITY = 0x1;    // 0001
    public static final short STATIC_WALL_ENTITY = 0x1 << 1; // 0010 or 0x2 in hex

    private ZombieGame screenManager;
    private Preferences saveFile;
    private boolean showDebugLines;
    private int currentLevel;
    private float timePassed;
    private int zombiesSpawned;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private AssetManager assetManager;

    //Hud
    private Hud hud;

    //Map
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private RectangleMapObject spawnPoint;

    //Box2D
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    public Array<Body> toRemove = new Array<Body>();

    //Objects
    private Array<Zombie> zombies;
    private Array<Tower> towers;

    public GameScreen(ZombieGame screenManager, byte currentLevel) {
        this.screenManager = screenManager;
        this.assetManager = ZombieGame.getAssetManager();
        this.batch = ZombieGame.getSpriteBatch();
        this.currentLevel = currentLevel;
        showDebugLines = true;
    }

    @Override
    public void show() {
        //Checking for saveFile, to load in value
        saveFile = Gdx.app.getPreferences("config");
        saveFile.putInteger("currentLevel", currentLevel);

        initializeAssets();

        //Creating camera and the HUD
        camera = new OrthographicCamera();
        viewport = new FitViewport(ZombieGame.WIDTH / ZombieGame.PPM, ZombieGame.HEIGHT / ZombieGame.PPM, camera);
        viewport.apply(true);
        camera.position.set((ZombieGame.WIDTH / 2 + MAP_OFFSET_X) / ZombieGame.PPM, (ZombieGame.HEIGHT / 2 + MAP_OFFSET_Y) / ZombieGame.PPM, 0);
        hud = new Hud();
        hud.setWave(saveFile.getInteger("currentWave", 1));
        hud.setMoney(saveFile.getInteger("currentMoney", 100));
        hud.setLivesLeft(saveFile.getInteger("currentLivesLeft", 10));

        initializeMap();
        createBox2DWorld();
    }

    private void initializeAssets() {
        Box2D.init();
        Gdx.input.setInputProcessor(this);
        assetManager.load("music/ingame1.mp3", Music.class);
        assetManager.load("sounds/shoot.mp3", Sound.class);
        assetManager.load("spritesheets/healthbar/healthbar.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombie1/zombie1.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombie2/zombie2.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier1/idle/soldier1idle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier1/shoot/soldier1shoot.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier2/soldier2.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier3/soldier3.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier4/soldier4.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier5/soldier5.pack", TextureAtlas.class);
        assetManager.finishLoading();

        Music music = assetManager.get("music/ingame1.mp3");
        if (saveFile.getBoolean("music", true)) music.play();

        //Creating the empty zombies and towers arrays
        zombies = new Array<Zombie>();
        towers = new Array<Tower>();
    }

    private void initializeMap() {
        //Loading in the map based on the saveFile
        map = new TmxMapLoader().load("maps/map" + currentLevel + "new.tmx");
        spawnPoint = (RectangleMapObject) map.getLayers().get(2).getObjects().get(0);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / ZombieGame.PPM);
        batch.setProjectionMatrix(hud.getStage().getCamera().combined);
    }

    private void createBox2DWorld() {
        //Box2d
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ZombieContactListener(this));
        box2DDebugRenderer = new Box2DDebugRenderer();
        box2DDebugRenderer.SHAPE_STATIC.set(1, 0, 0, 1);
        Box2DObjectCreator B2Dcreator = new Box2DObjectCreator(world);
        B2Dcreator.createStaticObjects(map.getLayers().get(0), "walls");
        B2Dcreator.createStaticObjects(map.getLayers().get(3), "base");
    }

    @Override
    public void render(float delta) {
        clearScreen();
        //Clearing the array of zombies that collided with tha base int the previous frame
        clearDeadBodies();

        timePassed += delta;

        //Render map
        mapRenderer.setView(camera);
        mapRenderer.render();

        //Render hud
        hud.update(delta);
        hud.getStage().draw();

        //Box2D (Debug Lines)
        if (showDebugLines) box2DDebugRenderer.render(world, camera.combined);

        handleInput(delta);
        batch.begin();
        updateZombieLocations(delta);
        zombieSpawner();
        towerCreatorListener();
        updateTowers();
        batch.end();
        world.step(delta, 6, 2);
    }

    private void zombieSpawner() {
        if (timePassed > 2 && zombiesSpawned < 8) {
            createZombie();
            timePassed = 0;
        }
    }

    private void createZombie() {
        zombies.add(new Zombie(world, batch, assetManager, spawnPoint));
        zombiesSpawned++;
    }

    private void towerCreatorListener() {
        if (Gdx.input.justTouched() && hud.getMoney() >= 50) {
            hud.setMoney(hud.getMoney() - 50);
            towers.add(new Tower(world, batch, assetManager,
                    (int) (Gdx.input.getX() + (camera.position.x * ZombieGame.PPM) - ZombieGame.WIDTH / 2),
                    (int) ((ZombieGame.HEIGHT - Gdx.input.getY()) + (camera.position.y * ZombieGame.PPM) - ZombieGame.HEIGHT / 2)));
        }
    }

    private void updateZombieLocations(float delta) {
        for (Zombie individualZombie : zombies) {
            individualZombie.updateSpritePosition(camera.position.x, camera.position.y);
            individualZombie.draw(delta);
        }
    }

    private void updateTowers() {
        for (Tower individualTower : towers) {
            individualTower.updateSpritePosition(camera.position.x, camera.position.y);
            individualTower.draw(Gdx.graphics.getDeltaTime());
            individualTower.checkForZombiesInRange(this);
        }
    }

    private void clearDeadBodies() {
        for (Body body : toRemove) {
            world.destroyBody(body);
            for (int i = 0; i < zombies.size; i++) {
                if (zombies.get(i).box2dBody.equals(body)) {
                    zombies.get(i).dispose();
                    zombies.removeIndex(i);
                }
            }
        }
        toRemove.clear();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    //Temporary method for moving the camera
    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            zombiesSpawned = 0;
            hud.setWave(hud.getWave() + 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            screenManager.setScreen(new MenuScreen(screenManager));
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && showDebugLines)
            showDebugLines = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_RIGHT) && !showDebugLines)
            showDebugLines = true;


    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.position.set(((ZombieGame.WIDTH - screenX) / ZombieGame.PPM), (screenY / ZombieGame.PPM), 0);

        if (camera.position.x < (0 + ZombieGame.WIDTH / 2) / ZombieGame.PPM)
            camera.position.set(((0 + ZombieGame.WIDTH / 2) / ZombieGame.PPM), camera.position.y, 0);
        if (camera.position.x > (1536 - ZombieGame.WIDTH / 2) / ZombieGame.PPM)
            camera.position.set(((1536 - ZombieGame.WIDTH / 2) / ZombieGame.PPM), camera.position.y, 0);
        if (camera.position.y > (864 - ZombieGame.HEIGHT / 2) / ZombieGame.PPM)
            camera.position.set(camera.position.x, (864 - ZombieGame.HEIGHT / 2) / ZombieGame.PPM, 0);
        if (camera.position.y < (0 + ZombieGame.HEIGHT / 2) / ZombieGame.PPM)
            camera.position.set(camera.position.x, (0 + ZombieGame.HEIGHT / 2) / ZombieGame.PPM, 0);

        camera.update();
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        saveFile.putInteger("currentLivesLeft", hud.getLivesLeft());
        saveFile.putInteger("currentWave", hud.getWave());
        saveFile.putInteger("currentMoney", hud.getMoney());
        saveFile.flush();
        dispose();
    }

    @Override
    public void dispose() {
        for (Zombie current : zombies) current.dispose();
        for (Tower current : towers) current.dispose();
        assetManager.unload("music/ingame1.mp3");
        assetManager.unload("sounds/shoot.mp3");
        assetManager.unload("spritesheets/healthbar/healthbar.pack");
        assetManager.unload("spritesheets/zombie1/zombie1.pack");
        assetManager.unload("spritesheets/zombie2/zombie2.pack");
        assetManager.unload("spritesheets/soldier1/idle/soldier1idle.pack");
        assetManager.unload("spritesheets/soldier1/shoot/soldier1shoot.pack");
        assetManager.unload("spritesheets/soldier2/soldier2.pack");
        assetManager.unload("spritesheets/soldier3/soldier3.pack");
        assetManager.unload("spritesheets/soldier4/soldier4.pack");
        assetManager.unload("spritesheets/soldier5/soldier5.pack");
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }

    public Hud getHud() {
        return hud;
    }

    public Array<Zombie> getZombies() {
        return zombies;
    }

}
