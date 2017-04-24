package hu.uni.miskolc.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import hu.uni.miskolc.ZombieGame;
import hu.uni.miskolc.screens.GameScreen;
import hu.uni.miskolc.sprites.zombies.Zombie;

public class ZombieContactListener implements ContactListener {

    private GameScreen screen;

    public ZombieContactListener(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.isTouching()) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            if (fixtureA.getUserData().equals("base") && fixtureB.getUserData().equals("zombie")) {
                screen.toRemove.add(fixtureB.getBody());
                screen.getHud().decreaseLives();
            } else {
                if (fixtureA.getUserData().equals("UP")) {
                    fixtureB.getBody().setLinearVelocity(0, Zombie.VELOCITY / ZombieGame.PPM);
                    return;
                }
                if (fixtureA.getUserData().equals("DOWN")) {
                    fixtureB.getBody().setLinearVelocity(0, -Zombie.VELOCITY / ZombieGame.PPM);
                    return;
                }
                if (fixtureA.getUserData().equals("LEFT")) {
                    fixtureB.getBody().setLinearVelocity(-Zombie.VELOCITY / ZombieGame.PPM, 0);
                    return;
                }
                if (fixtureA.getUserData().equals("RIGHT")) {
                    fixtureB.getBody().setLinearVelocity(Zombie.VELOCITY / ZombieGame.PPM, 0);
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
