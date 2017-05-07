package hu.uni.miskolc.sprites.zombies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;

import hu.uni.miskolc.ZombieGame;

public class ZombieFast extends Zombie {

    public ZombieFast(World world, SpriteBatch batch, RectangleMapObject spawnPoint) {
        super(world, batch, spawnPoint);
        health = 110;
    }

    protected void createAnimation() {
        TextureAtlas atlas = ZombieGame.getAssetManager().get("spritesheets/zombiefast/zombiefast.pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.05f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth() * SCALE);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight() * SCALE);
    }
}