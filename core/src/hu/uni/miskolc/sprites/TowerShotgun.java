package hu.uni.miskolc.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.states.TowerState;


public class TowerShotgun extends Tower {
    public TowerShotgun(World world, SpriteBatch batch, AssetManager assetManager, int xPos, int yPos) {
        super(world, batch, assetManager, xPos, yPos);
        shotDamage = 30;
        shootInterval = 1.8f;
        range = (int) (180 / ZombieGame.PPM);
    }

    protected void createAnimation() {
        TextureAtlas atlasIdle = ZombieGame.getAssetManager().get("spritesheets/soldier4/idle/soldieridle.pack");
        TextureAtlas atlasShoot = ZombieGame.getAssetManager().get("spritesheets/soldier4/shoot/soldiershoot.pack");
        animationIdle = new Animation<TextureAtlas.AtlasRegion>(0.5f, atlasIdle.getRegions());
        animationIdle.setPlayMode(Animation.PlayMode.NORMAL);
        animationShooting = new Animation<TextureAtlas.AtlasRegion>(shootInterval / 10, atlasShoot.getRegions());
        animationShooting.setPlayMode(Animation.PlayMode.NORMAL);
        atlasRegionWidth = (int) (atlasIdle.getRegions().first().getRegionWidth() * SCALE);
        atlasRegionHeight = (int) (atlasIdle.getRegions().first().getRegionHeight() * SCALE);
    }

    public void checkForZombiesInRange(GameScreen screen) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        //Every second check for zombies
        if (elapsedTime > shootInterval) {
            elapsedTime = 0f;
            Array<Zombie> zombies = screen.getZombies();
            //all zombies loop
            for (Zombie current : zombies) {
                float x = current.getBox2dBody().getPosition().x;
                float y = current.getBox2dBody().getPosition().y;
                //if zombie is in range, it gets shot
                if ((((x - this.box2dBody.getPosition().x) * (x - this.box2dBody.getPosition().x)) + ((y - this.box2dBody.getPosition().y) * (y - this.box2dBody.getPosition().y))) < range * range) {
                    current.getShot(shotDamage);
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

}
