package hu.uni.miskolc.sprites;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

import hu.uni.miskolc.ZombieGame;

public class Zombie implements Disposable {

    private static final int B2D_WIDTH = 46;
    private static final int VELOCITY = 80;
    //private static final int HEIGHT = 46;
    private static final float SCALE = 0.48f;

    private boolean lookingLeft = false;

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private Animation<TextureAtlas.AtlasRegion> animation;
    private float elapsedTime;
    private int health;
    private BitmapFont font;

    private World world;
    public Body box2dBody;
    private Direction current;
    private Array<Direction> directions;

    private int atlasRegionWidth, atlasRegionHeight, xPos, yPos;

    private enum Direction   {
        RIGHT, LEFT, UP, DOWN
    }

    public Zombie(World world, SpriteBatch batch, RectangleMapObject spawnPoint, AssetManager assetManager) {
        this.world = world;
        this.batch = batch;
        health = 100;
        font = new BitmapFont();

        directions = new Array<Direction>();
        directions.add(Direction.UP);
        directions.add(Direction.RIGHT);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.DOWN);
        directions.add(Direction.RIGHT);
        directions.add(Direction.UP);
        directions.add(Direction.LEFT);
        directions.add(Direction.UP);
        directions.add(Direction.RIGHT);
        current = Direction.RIGHT;

        defineZombie(spawnPoint);
        createAnimation(assetManager);
    }

    public Body getBox2dBody()  {
        return box2dBody;
    }

    private void defineZombie(RectangleMapObject spawnPoint)  {
        BodyDef bdef = new BodyDef();
        bdef.position.set(spawnPoint.getRectangle().getX() / ZombieGame.PPM , (spawnPoint.getRectangle().getY()+spawnPoint.getRectangle().getHeight()/2) / ZombieGame.PPM);
        xPos = (int) (spawnPoint.getRectangle().getX() / ZombieGame.PPM);
        yPos = (int) (spawnPoint.getRectangle().getY() / ZombieGame.PPM);

        bdef.type = BodyDef.BodyType.DynamicBody;
        box2dBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius( Zombie.B2D_WIDTH / ZombieGame.PPM);
        //shape.setAsBox( Zombie.B2D_WIDTH / ZombieGame.PPM, Zombie.HEIGHT / ZombieGame.PPM);
        fdef.shape = shape;
        Fixture fixture = box2dBody.createFixture(fdef);
        fixture.setUserData("zombie");
        box2dBody.setLinearVelocity(VELOCITY / ZombieGame.PPM, 0);
        shape.dispose();
    }

    private void createAnimation(AssetManager assetManager) {
        Random rand = new Random(System.currentTimeMillis());
        int randomNum = rand.nextInt(2) + 1;
        atlas = assetManager.get("spritesheets/zombie" + randomNum + "/zombie.pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.12f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth()*SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight()*SCALE);
    }

    public void updatePath()    {
        if (box2dBody.getLinearVelocity().isZero() && directions.size >= 1)  {
            current = directions.first();
            directions.removeIndex(0);

            if (current == Zombie.Direction.UP)
                box2dBody.setLinearVelocity(0, VELOCITY / ZombieGame.PPM);
            if (current == Zombie.Direction.RIGHT)
                box2dBody.setLinearVelocity(VELOCITY / ZombieGame.PPM, 0 );
            if (current == Zombie.Direction.DOWN)
                box2dBody.setLinearVelocity(0, -VELOCITY / ZombieGame.PPM);
            if (current == Zombie.Direction.LEFT)
                box2dBody.setLinearVelocity(-VELOCITY / ZombieGame.PPM, 0);

        }
    }

    public void updateSpritePosition(float camX, float camY)    {
        xPos = (int) (box2dBody.getPosition().x * ZombieGame.PPM - (B2D_WIDTH) - camX*ZombieGame.PPM + ZombieGame.WIDTH / 2);
        yPos = (int) (box2dBody.getPosition().y * ZombieGame.PPM - (B2D_WIDTH /2) - camY*ZombieGame.PPM + ZombieGame.HEIGHT / 2);
        if (current == Direction.RIGHT && lookingLeft)  {
            lookingLeft = false;
            for (TextureRegion temp : atlas.getRegions())    {
                temp.flip(true,false);
            }
        }
        if (current == Direction.LEFT && !lookingLeft)  {
            lookingLeft = true;
            for (TextureRegion temp : atlas.getRegions())    {
                temp.flip(true,false);
            }
        }
    }

    public void draw(float delta)  {
        elapsedTime+=delta;
        if (elapsedTime > 1.2f)
            elapsedTime = 0f;
        batch.draw(animation.getKeyFrame(elapsedTime,true), xPos, yPos, atlasRegionWidth, atlasRegionHeight);
        font.draw(batch, health+"", xPos, yPos);
    }

    @Override
    public void dispose() {
    }

    public void getShot(int damage)   {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }
}
