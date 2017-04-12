package hu.uni.miskolc.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.states.TowerState;

public class Tower implements Disposable{

    private static final int B2D_WIDTH = 32;
    private static final float SCALE = 0.50f;

    private SpriteBatch batch;
    private Animation<TextureAtlas.AtlasRegion> animationIdle;
    private Animation<TextureAtlas.AtlasRegion> animationShooting;
    private float elapsedTime;
    private int range;
    private boolean lookingLeft;
    private TowerState state;

    private World world;
    private Body box2dBody;
    private Sound shootSound;

    private int atlasRegionWidth, atlasRegionHeight, xPos, yPos;

    public Tower(World world, SpriteBatch batch, AssetManager assetManager, int xPos, int yPos) {
        this.world = world;
        this.batch = batch;
        this.xPos = xPos;
        this.yPos = yPos;
        range = (int) (200 / ZombieGame.PPM);
        lookingLeft = false;
        defineTower();
        state = TowerState.IDLE;
        createAnimation();
        shootSound = assetManager.get("sounds/shoot.mp3");
    }

    public void checkForZombiesInRange(GameScreen screen) {

        //Every second check for zombies
        if (elapsedTime > 1f) {
            elapsedTime = 0f;
            Array<Zombie> zombies = screen.getZombies();
            //all zombies loop
            for (Zombie current : zombies) {
                float x = current.getBox2dBody().getPosition().x;
                float y = current.getBox2dBody().getPosition().y;
                //if zombie is in range, it gets shot
                if ((((x - this.box2dBody.getPosition().x) * (x - this.box2dBody.getPosition().x)) + ((y - this.box2dBody.getPosition().y) * (y - this.box2dBody.getPosition().y))) < range * range) {
                    current.getShot(20);
                    state = TowerState.SHOOTING;
                    if (x > this.box2dBody.getPosition().x && lookingLeft) flipSprite();
                    if (x < this.box2dBody.getPosition().x && !lookingLeft) flipSprite();
                    shootSound.play(ZombieGame.volume);
                    //if zombie health <= 0 it dies, gets removed from screen, and money increases
                    if (current.getHealth() <= 0) {
                        screen.toRemove.add(current.getBox2dBody());
                        screen.getHud().setMoney(screen.getHud().getMoney() + 10);
                    }
                    break;
                } else {
                    state = TowerState.IDLE;
                }
            }
            if (zombies.size == 0)
                state = TowerState.IDLE;
        }
    }

    private void defineTower() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(xPos / ZombieGame.PPM , yPos / ZombieGame.PPM);

        //The tower itself
        bdef.type = BodyDef.BodyType.DynamicBody;
        box2dBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius( Tower.B2D_WIDTH / ZombieGame.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = GameScreen.DYNAMIC_ENTITY;
        fdef.filter.maskBits = GameScreen.STATIC_WALL_ENTITY;
        Fixture fixture = box2dBody.createFixture(fdef);
        fixture.setUserData("tower");
        shape.dispose();
    }

    private void createAnimation() {
        TextureAtlas atlasIdle = ZombieGame.getAssetManager().get("spritesheets/soldier1/idle/soldier1idle.pack");
        TextureAtlas atlasShoot = ZombieGame.getAssetManager().get("spritesheets/soldier1/shoot/soldier1shoot.pack");
        animationIdle = new Animation<TextureAtlas.AtlasRegion>(0.5f, atlasIdle.getRegions());
        animationIdle.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        animationShooting = new Animation<TextureAtlas.AtlasRegion>(0.10f, atlasShoot.getRegions());
        animationShooting.setPlayMode(Animation.PlayMode.NORMAL);
        atlasRegionWidth = (int) (atlasIdle.getRegions().first().getRegionWidth() * SCALE);
        atlasRegionHeight = (int) (atlasIdle.getRegions().first().getRegionHeight() * SCALE);
    }

    private void flipSprite() {
        if (lookingLeft) {
            lookingLeft = false;
            return;
        }
        lookingLeft = true;
    }

    public void updateSpritePosition(float camX, float camY)  {
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH * 2) - camX * ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH) - camY*ZombieGame.PPM + ZombieGame.HEIGHT / 2);
    }

    public void draw(float delta)  {
        elapsedTime+=delta;
        if (!lookingLeft) {
            if (state.equals(TowerState.IDLE))
                batch.draw(animationIdle.getKeyFrame(elapsedTime, true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
            else
                batch.draw(animationShooting.getKeyFrame(elapsedTime, false), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
        } else {
            if (state.equals(TowerState.IDLE))
                batch.draw(animationIdle.getKeyFrame(elapsedTime, true), xPos + atlasRegionWidth, yPos, -atlasRegionWidth, atlasRegionHeight);
            else
                batch.draw(animationShooting.getKeyFrame(elapsedTime, false), xPos + atlasRegionWidth, yPos, -atlasRegionWidth, atlasRegionHeight);
        }
    }

    @Override
    public void dispose() {
    }
}
