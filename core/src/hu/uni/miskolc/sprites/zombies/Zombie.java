package hu.uni.miskolc.sprites.zombies;


import com.badlogic.gdx.Gdx;
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

    static final int B2D_WIDTH = 46;
    public static int VELOCITY = 80;
    public static int FAST_VELOCITY = 100;

    float SCALE = 0.86f;
    int health;

    boolean lookingLeft = false;

    private SpriteBatch batch;
    Animation<TextureAtlas.AtlasRegion> animation;
    private float elapsedTime;
    private BitmapFont font;

    private World world;
    public Body box2dBody;
    int atlasRegionWidth, atlasRegionHeight;
    private int xPos, yPos;


    public Zombie(World world, SpriteBatch batch, RectangleMapObject spawnPoint) {
        this.world = world;
        this.batch = batch;
        health = 100;
        font = new BitmapFont(Gdx.files.internal("fonts/myfont3.fnt"));

        defineZombie(spawnPoint);
        createAnimation();
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
        fdef.filter.categoryBits = GameScreen.ZOMBIES_MASK;
        fdef.filter.maskBits = GameScreen.WALLS_MASK;
        Fixture fixture = box2dBody.createFixture(fdef);
        fixture.setUserData("zombie");
        box2dBody.setLinearVelocity(VELOCITY / ZombieGame.PPM, 0);
        shape.dispose();
    }

    protected void createAnimation() {
        Random rand = new Random(System.currentTimeMillis());
        int randomNum = rand.nextInt(2) + 1;
        TextureAtlas atlas = ZombieGame.getAssetManager().get("spritesheets/zombie" + randomNum + "/zombie" + randomNum + ".pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.12f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth() * SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight() * SCALE);
    }

    public void updateSpritePosition(float camX, float camY) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        if (elapsedTime > 1.2f)
            elapsedTime = 0f;
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH) - camX * ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH / 2) - camY * ZombieGame.PPM + ZombieGame.HEIGHT / 2);
        lookingLeft = box2dBody.getLinearVelocity().x < 0;
    }

    public void draw(float delta) {
        if (lookingLeft)
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos + atlasRegionWidth, yPos, -atlasRegionWidth, atlasRegionHeight);
        else
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
        font.draw(batch, health + " HP", xPos, yPos);
    }

    @Override
    public void dispose() {
        animation = null;
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
