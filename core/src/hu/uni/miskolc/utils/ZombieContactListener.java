package hu.uni.miskolc.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import hu.uni.miskolc.screens.GameScreen;

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
