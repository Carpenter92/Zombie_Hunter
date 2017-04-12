package hu.uni.miskolc.sprites;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;

public class Zombie implements Disposable {

    private static final int B2D_WIDTH = 46;
    public static final int VELOCITY = 80;
    private static final float SCALE = 0.86f;

    private boolean lookingLeft = false;

    private SpriteBatch batch;
    private Animation<TextureAtlas.AtlasRegion> animation;
    private float elapsedTime;
    private int health;
    private BitmapFont font;

    private World world;
    public Body box2dBody;
    private int atlasRegionWidth, atlasRegionHeight, xPos, yPos;


    public Zombie(World world, SpriteBatch batch, AssetManager assetManager, RectangleMapObject spawnPoint) {
        this.world = world;
        this.batch = batch;
        health = 100;
        font = new BitmapFont();

        defineZombie(spawnPoint);
        createAnimation(assetManager);
    }

    private void defineZombie(RectangleMapObject spawnPoint) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(spawnPoint.getRectangle().getX() / ZombieGame.PPM, (spawnPoint.getRectangle().getY() + spawnPoint.getRectangle().getHeight() / 2) / ZombieGame.PPM);
        xPos = (int) (spawnPoint.getRectangle().getX() / ZombieGame.PPM);
        yPos = (int) (spawnPoint.getRectangle().getY() / ZombieGame.PPM);

        bdef.type = BodyDef.BodyType.DynamicBody;
        box2dBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Zombie.B2D_WIDTH / ZombieGame.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = GameScreen.DYNAMIC_ENTITY;
        fdef.filter.maskBits = GameScreen.STATIC_WALL_ENTITY;
        Fixture fixture = box2dBody.createFixture(fdef);
        fixture.setUserData("zombie");
        box2dBody.setLinearVelocity(VELOCITY / ZombieGame.PPM, 0);
        shape.dispose();
    }

    private void createAnimation(AssetManager assetManager) {
        Random rand = new Random(System.currentTimeMillis());
        int randomNum = rand.nextInt(2) + 1;
        TextureAtlas atlas = assetManager.get("spritesheets/zombie" + randomNum + "/zombie" + randomNum + ".pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.12f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth() * SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight() * SCALE);
    }

    public void updateSpritePosition(float camX, float camY) {
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH) - camX * ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH / 2) - camY * ZombieGame.PPM + ZombieGame.HEIGHT / 2);
        lookingLeft = box2dBody.getLinearVelocity().x < 0;
    }

    public void draw(float delta) {
        elapsedTime += delta;
        if (elapsedTime > 1.2f)
            elapsedTime = 0f;
        if (lookingLeft)
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos + atlasRegionWidth, yPos, -atlasRegionWidth, atlasRegionHeight);
        else
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
        font.draw(batch, health + " HP", xPos, yPos);
    }

    @Override
    public void dispose() {
    }

    public Body getBox2dBody() {
        return box2dBody;
    }

    public void getShot(int damage) {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }
}
