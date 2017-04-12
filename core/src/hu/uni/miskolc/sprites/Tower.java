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

public class Tower implements Disposable{

    private static final int B2D_WIDTH = 32;
    //private static final int B2D_HEIGHT = 32;
    private static final float SCALE = 0.30f;

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private Animation<TextureAtlas.AtlasRegion> animation;
    private float elapsedTime;
    private int range;
    private boolean lookingLeft;

    private World world;
    private Body box2dBody;
    private Array<Zombie> zombies;
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
        createAnimation();
        shootSound = assetManager.get("sounds/shoot.mp3");
    }

    public void checkForZombiesInRange(GameScreen screen) {

        //Every second check for zombies
        if (elapsedTime > 1f) {
            elapsedTime = 0f;
            zombies = screen.getZombies();
            //all zombies loop
            for (Zombie current : zombies) {
                float x = current.getBox2dBody().getPosition().x;
                float y = current.getBox2dBody().getPosition().y;
                //if zombie is in range, it gets shot
                if ((((x - this.box2dBody.getPosition().x) * (x - this.box2dBody.getPosition().x)) + ((y - this.box2dBody.getPosition().y) * (y - this.box2dBody.getPosition().y))) < range * range) {
                    current.getShot(20);
                    if (x > this.box2dBody.getPosition().x && lookingLeft) flipSprite();
                    if (x < this.box2dBody.getPosition().x && !lookingLeft) flipSprite();
                    shootSound.play(ZombieGame.volume);
                    //if zombie health <= 0 it dies, gets removed from screen, and money increases
                    if (current.getHealth() <= 0) {
                        screen.toRemove.add(current.getBox2dBody());
                        screen.getHud().setMoney(screen.getHud().getMoney() + 10);
                    }
                    break;
                }
            }
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
        //shape.setAsBox( Tower.B2D_WIDTH / ZombieGame.PPM, Tower.B2D_HEIGHT / ZombieGame.PPM);
        fdef.shape = shape;
        Fixture fixture = box2dBody.createFixture(fdef);
        fixture.setUserData("tower");
        shape.dispose();
    }

    private void createAnimation() {
        atlas = ZombieGame.getAssetManager().get("spritesheets/tower/tower.pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.15f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth()*SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight()*SCALE);
    }

    private void flipSprite() {
        if (lookingLeft) {
            lookingLeft = false;
            return;
        }
        lookingLeft = true;
    }

    public void updateSpritePosition(float camX, float camY)  {
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH) - camX*ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH) - camY*ZombieGame.PPM + ZombieGame.HEIGHT / 2);
    }

    public void draw(float delta)  {
        elapsedTime+=delta;
        if (!lookingLeft)
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
        else
            batch.draw(animation.getKeyFrame(elapsedTime, true), xPos + atlasRegionWidth, yPos, -atlasRegionWidth, atlasRegionHeight);
    }

    @Override
    public void dispose() {
    }
}
