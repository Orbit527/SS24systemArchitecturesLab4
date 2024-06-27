package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Game;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

import java.sql.Statement;

public class Physics implements ContactListener, StepListener {

    private final World world;

    public Physics() {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.getSettings().setStepFrequency(1.0/120.0);

        this.world.addListener(this);
    }

    public void updateWorld(double dt) {
        this.world.update(dt);
    }

    public void addBodyToWorld(Body body) {
        this.world.addBody(body);
    }

    @Override
    public void begin(Step step, World world) {

    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {

    }

    @Override
    public void end(Step step, World world) {
        // TBD: check here if all objects rest or if they started to move. Notify through listeners
        // you can retrieve all bodies from the world (world.getBodies()) and then check for each
        // body if it is moving (e.g., b.getLinearVelocity().getMagnitude()
    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        // TBD: use listener to notify necessary subsystems

        System.out.println(point.getBody1().getUserData() + " " + point.getBody2().getUserData());
        return true;
    }

    @Override
    public void end(ContactPoint point) {

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        // TBD: use this method to check if a ball and a pocket (sensor) overlap and notify
        // the necessary subsystems. For checking if a sensor is involved use point.isSensor()

        if(point.isSensor()) {
            System.out.println("SENSOR " + point.isSensor() + " " + point.getBody1().getUserData());
            // removes it only for the physics, not for the renderer
            world.removeBody(point.getBody1());
        }


        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }

    public World getWorld() {
        return this.world;
    }
}
