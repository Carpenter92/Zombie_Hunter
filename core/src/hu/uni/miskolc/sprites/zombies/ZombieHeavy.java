package hu.uni.miskolc.sprites.zombies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;

import hu.uni.miskolc.ZombieGame;

public class ZombieHeavy extends Zombie {
    public ZombieHeavy(World world, SpriteBatch batch, RectangleMapObject spawnPoint) {
        super(world, batch, spawnPoint);
        health = 800;
    }

    protected void createAnimation() {
        TextureAtlas atlas = ZombieGame.getAssetManager().get("spritesheets/zombieheavy/zombieheavy.pack");
        animation = new Animation<TextureAtlas.AtlasRegion>(0.10f, atlas.getRegions());
        atlasRegionWidth = (int) (atlas.getRegions().first().getRegionWidth() * SCALE / 1.3);
        atlasRegionHeight = (int) (atlas.getRegions().first().getRegionHeight() * SCALE / 1.3);
    }
}
