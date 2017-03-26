package hu.uni.miskolc.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static final int B2D_WIDTH = 35;
    //private static final int B2D_HEIGHT = 35;
    private static final float SCALE = 0.30f;

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private Animation<TextureAtlas.AtlasRegion> animation;
    private float elapsedTime;
    private int range;

    private World world;
    private Body box2dBody;
    private Array<Zombie> zombies;

    private int atlasRegionWidth, atlasRegionHeight, xPos, yPos;

    public Tower(World world, SpriteBatch batch, int xPos, int yPos)  {
        this.world = world;
        this.batch = batch;
        this.xPos = xPos;
        this.yPos = yPos;
        range = (int) (200 / ZombieGame.PPM);
        defineTower();
        createAnimation();
    }

    public void checkForZombies(GameScreen screen) {
        for (Zombie current : zombies) {
            float x = current.getBox2dBody().getPosition().x;
            float y = current.getBox2dBody().getPosition().y;
            if ((((x - this.box2dBody.getPosition().x)*(x - this.box2dBody.getPosition().x)) + ((y - this.box2dBody.getPosition().y)*(y - this.box2dBody.getPosition().y))) < range*range)    {
                screen.toRemove.add(current.getBox2dBody());
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
        atlas = new TextureAtlas(Gdx.files.internal("spritesheets/tower/tower.pack"));
        animation = new Animation<TextureAtlas.AtlasRegion>(0.15f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth()*SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight()*SCALE);

        for (TextureRegion temp : atlas.getRegions())    {
            temp.flip(true,false);
        }
    }

    public void updateSpritePosition(float camX, float camY)  {
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH) - camX*ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH) - camY*ZombieGame.PPM + ZombieGame.HEIGHT / 2);
    }

    public void draw(float delta)  {
        elapsedTime+=delta;
        if (elapsedTime > 1f)
            elapsedTime = 0f;
        batch.draw(animation.getKeyFrame(elapsedTime,true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
    }

    public void setZombies(Array<Zombie> zombies)   {
        this.zombies = zombies;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
