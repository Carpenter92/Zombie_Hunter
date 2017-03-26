package hu.uni.miskolc.utils;


import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import hu.uni.miskolc.ZombieGame;

public class Box2DObjectCreator {

    private World world;

    public Box2DObjectCreator(World world)    {
        this.world = world;
    }

    public void createStaticObjects(MapLayer mapLayer, String layerName)   {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        Polyline line = new Polyline();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for (RectangleMapObject object : mapLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = object.getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / ZombieGame.PPM , (rectangle.getY() + rectangle.getHeight() / 2) / ZombieGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rectangle.getWidth()/ 2) / ZombieGame.PPM, (rectangle.getHeight()/ 2) / ZombieGame.PPM);
            fdef.shape = shape;
            Fixture fixture = body.createFixture(fdef);
            fixture.setUserData(layerName);
        }
    }
}
