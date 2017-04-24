package hu.uni.miskolc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
import hu.uni.miskolc.sprites.towers.Tower;
import hu.uni.miskolc.sprites.towers.TowerRanged;
import hu.uni.miskolc.sprites.zombies.Zombie;
import hu.uni.miskolc.sprites.zombies.ZombieArmored;
import hu.uni.miskolc.sprites.zombies.ZombieHeavy;
import hu.uni.miskolc.sprites.zombies.ZombieMummy;
import hu.uni.miskolc.states.GameState;
import hu.uni.miskolc.utils.Box2DObjectCreator;
import hu.uni.miskolc.utils.ZombieContactListener;
import hu.uni.miskolc.utils.ZombieTypes;

public class GameScreen extends InputAdapter implements Screen {

    private static final int MAP_OFFSET_X = 128; //128
    private static final int MAP_OFFSET_Y = 64; //64

    public static final short ZOMBIES_MASK = 0x1;    // 0001
    public static final short WALLS_MASK = 0x1 << 1; // 0010 or 0x2 in hex
    public static final short TOWERS_MASK = 0x1 << 2;
    public static final short OBSTACLES_MASK = 0x1 << 3;

    private ZombieGame screenManager;
    private Preferences saveFile;
    private boolean showDebugLines;
    private int currentLevel;
    private float timePassed;
    private GameState gameState;
    private Music music;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private AssetManager assetManager;
    private Vector2 lastTouch;
    private long lastTouchTime;
    private short placableTowerNumber = 0;

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
    private Array<ZombieTypes> zombiesToSpawn;
    private Array<Zombie> zombies;
    private Array<Tower> towers;

    public GameScreen(ZombieGame screenManager, byte currentLevel) {
        this.screenManager = screenManager;
        this.assetManager = ZombieGame.getAssetManager();
        this.batch = ZombieGame.getSpriteBatch();
        this.currentLevel = currentLevel;
        gameState = GameState.RUNNING;
        lastTouch = new Vector2();
        showDebugLines = false;
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
        hud = new Hud(this);
        hud.setWave(saveFile.getInteger("currentWave", 0));
        hud.setMoney(saveFile.getInteger("currentMoney", 100));
        hud.setLivesLeft(saveFile.getInteger("currentLivesLeft", 10));
        hud.createWaveManager();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(hud.getStage());
        Gdx.input.setInputProcessor(inputMultiplexer);

        initializeMap();
        createBox2DWorld();
    }

    private void initializeAssets() {
        Box2D.init();
        assetManager.load("music/ingame1.mp3", Music.class);
        assetManager.load("sounds/shoot.mp3", Sound.class);
        assetManager.load("spritesheets/healthbar/healthbar.pack", TextureAtlas.class);
        //Zombie textures
        assetManager.load("spritesheets/zombie1/zombie1.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombie2/zombie2.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombiearmored/zombiearmored.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombieheavy/zombieheavy.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombiefast/zombiefast.pack", TextureAtlas.class);
        assetManager.load("spritesheets/zombiemummy/zombiemummy.pack", TextureAtlas.class);
        //Soldier textures
        assetManager.load("spritesheets/soldier1/idle/soldieridle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier1/shoot/soldiershoot.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier2/idle/soldieridle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier2/shoot/soldiershoot.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier3/idle/soldieridle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier3/shoot/soldiershoot.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier4/idle/soldieridle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier4/shoot/soldiershoot.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier5/idle/soldieridle.pack", TextureAtlas.class);
        assetManager.load("spritesheets/soldier5/shoot/soldiershoot.pack", TextureAtlas.class);
        assetManager.load("buyscreen/soldiers.pack", TextureAtlas.class);
        assetManager.finishLoading();

        music = assetManager.get("music/ingame1.mp3");
        if (saveFile.getBoolean("music", true)) music.play();

        //Creating the empty zombies and towers arrays
        zombies = new Array<Zombie>();
        zombiesToSpawn = new Array<ZombieTypes>();
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
        B2Dcreator.createStaticObjects(map.getLayers().get(0), "walls", WALLS_MASK, ZOMBIES_MASK);
        B2Dcreator.createStaticObjects(map.getLayers().get(1), "walls", OBSTACLES_MASK, TOWERS_MASK);
        B2Dcreator.createStaticObjects(map.getLayers().get(3), "base", WALLS_MASK, ZOMBIES_MASK);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        //Clearing the array of zombies that collided with tha base int the previous frame
        clearDeadBodies();

        //Render map
        mapRenderer.setView(camera);
        mapRenderer.render();

        //Box2D (Debug Lines)
        if (showDebugLines) box2DDebugRenderer.render(world, camera.combined);

        //Box2D stepping, if game is paused
        if (gameState.equals(GameState.RUNNING)) {
            timePassed += delta;
            world.step(delta, 6, 2);
            zombieSpawner();
        }

        batch.begin();
        updateZombieLocations(delta);
        updateTowers();
        batch.end();

        //Render hud
        hud.update(delta);
        hud.getStage().draw();
    }

    private void zombieSpawner() {
        if (timePassed > 2 && zombiesToSpawn.size != 0) {
            switch (zombiesToSpawn.pop()) {
                case STANDARD:
                    zombies.add(new Zombie(world, batch, spawnPoint));
                    break;
                case MUMMY:
                    zombies.add(new ZombieMummy(world, batch, spawnPoint));
                    break;
                case ARMORED:
                    zombies.add(new ZombieArmored(world, batch, spawnPoint));
                    break;
                case HEAVY:
                    zombies.add(new ZombieHeavy(world, batch, spawnPoint));
                    break;
            }
            timePassed = 0;
        }
    }

    private void updateZombieLocations(float delta) {
        for (Zombie individualZombie : zombies) {
            if (gameState.equals(GameState.RUNNING))
                individualZombie.updateSpritePosition(camera.position.x, camera.position.y);
            individualZombie.draw(delta);
        }
    }

    private void updateTowers() {
        for (Tower individualTower : towers) {
            individualTower.updateSpritePosition(camera.position.x, camera.position.y);
            individualTower.draw(Gdx.graphics.getDeltaTime());
            if (gameState.equals(GameState.RUNNING)) individualTower.checkForZombiesInRange(this);
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (placableTowerNumber != 0) {
            switch (placableTowerNumber) {
                case 1:
                    hud.setMoney(hud.getMoney() - 50);
                    towers.add(new Tower(world, batch, assetManager,
                            (int) (Gdx.input.getX() + (camera.position.x * ZombieGame.PPM) - Gdx.graphics.getWidth() / 2),
                            (int) ((Gdx.graphics.getHeight() - Gdx.input.getY()) + (camera.position.y * ZombieGame.PPM) - Gdx.graphics.getHeight() / 2)));
                    placableTowerNumber = 0;
                    break;
                case 2:
                    hud.setMoney(hud.getMoney() - 75);
                    towers.add(new TowerRanged(world, batch, assetManager,
                            (int) (Gdx.input.getX() + (camera.position.x * ZombieGame.PPM) - Gdx.graphics.getWidth() / 2),
                            (int) ((Gdx.graphics.getHeight() - Gdx.input.getY()) + (camera.position.y * ZombieGame.PPM) - Gdx.graphics.getHeight() / 2)));
                    placableTowerNumber = 0;
                    break;
            }
        }
        lastTouch.set(screenX, screenY);
        if (System.currentTimeMillis() - lastTouchTime <= 250 && gameState.equals(GameState.RUNNING)) {
            hud.doubleClickPopUp(this);
        }
        lastTouchTime = System.currentTimeMillis();
        return super.touchDown(screenX, screenY, pointer, button);
    }

    //Camera
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //Camera movement with touch gesture, limited to map size
        if (gameState.equals(GameState.RUNNING)) {
            Vector2 newTouch = new Vector2(screenX, screenY);
            Vector2 delta = newTouch.cpy().sub(lastTouch);
            lastTouch = newTouch;
            camera.position.set(camera.position.x - delta.x / ZombieGame.PPM, camera.position.y + delta.y / ZombieGame.PPM, 0);
            if (camera.position.x < (0 + ZombieGame.WIDTH / 2) / ZombieGame.PPM)
                camera.position.set(((0 + ZombieGame.WIDTH / 2) / ZombieGame.PPM), camera.position.y, 0);
            if (camera.position.x > (1536 - ZombieGame.WIDTH / 2) / ZombieGame.PPM)
                camera.position.set(((1536 - ZombieGame.WIDTH / 2) / ZombieGame.PPM), camera.position.y, 0);
            if (camera.position.y > (864 - ZombieGame.HEIGHT / 2) / ZombieGame.PPM)
                camera.position.set(camera.position.x, (864 - ZombieGame.HEIGHT / 2) / ZombieGame.PPM, 0);
            if (camera.position.y < (0 + ZombieGame.HEIGHT / 2) / ZombieGame.PPM)
                camera.position.set(camera.position.x, (0 + ZombieGame.HEIGHT / 2) / ZombieGame.PPM, 0);
            camera.update();
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.getStage().getViewport().update(width, height, true);
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
        saveFile.putInteger("currentMoney", hud.getMoney() + towers.size * 50);
        saveFile.flush();
        music.stop();
        dispose();
    }

    @Override
    public void dispose() {
        for (Zombie current : zombies) current.dispose();
        for (Tower current : towers) current.dispose();
        assetManager.unload("music/ingame1.mp3");
        assetManager.unload("sounds/shoot.mp3");
        assetManager.unload("spritesheets/healthbar/healthbar.pack");
        //Zombie textures
        assetManager.unload("spritesheets/zombie1/zombie1.pack");
        assetManager.unload("spritesheets/zombie2/zombie2.pack");
        assetManager.unload("spritesheets/zombiearmored/zombiearmored.pack");
        assetManager.unload("spritesheets/zombieheavy/zombieheavy.pack");
        assetManager.unload("spritesheets/zombiefast/zombiefast.pack");
        assetManager.unload("spritesheets/zombiemummy/zombiemummy.pack");
        //Soldier textures
        assetManager.unload("spritesheets/soldier1/idle/soldieridle.pack");
        assetManager.unload("spritesheets/soldier1/shoot/soldiershoot.pack");
        assetManager.unload("spritesheets/soldier2/idle/soldieridle.pack");
        assetManager.unload("spritesheets/soldier2/shoot/soldiershoot.pack");
        assetManager.unload("spritesheets/soldier3/idle/soldieridle.pack");
        assetManager.unload("spritesheets/soldier3/shoot/soldiershoot.pack");
        assetManager.unload("spritesheets/soldier4/idle/soldieridle.pack");
        assetManager.unload("spritesheets/soldier4/shoot/soldiershoot.pack");
        assetManager.unload("spritesheets/soldier5/idle/soldieridle.pack");
        assetManager.unload("spritesheets/soldier5/shoot/soldiershoot.pack");
        assetManager.unload("buyscreen/soldiers.pack");

        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }

    //GETTERS AND SETTERS
    public Hud getHud() {
        return hud;
    }

    public Array<Zombie> getZombies() {
        return zombies;
    }

    public void setZombies(Array<Zombie> zombies) {
        this.zombies = zombies;
    }

    public void setState(GameState state) {
        this.gameState = state;
    }

    public GameState getGameState() {
        return gameState;
    }

    public ZombieGame getScreenManager() {
        return screenManager;
    }

    public void setZombiesToSpawn(Array<ZombieTypes> zombiesToSpawn) {
        this.zombiesToSpawn = zombiesToSpawn;
    }

    public boolean isShowDebugLines() {
        return showDebugLines;
    }
    public void setShowDebugLines(boolean showDebugLines) {
        this.showDebugLines = showDebugLines;
    }

    public void setPlacableTowerNumber(short placableTowerNumber) {
        this.placableTowerNumber = placableTowerNumber;
    }
}
