package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.game.Game;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Vector2;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Physics implements ContactListener, StepListener{

    private final World world;

    private BallPocketedListener ballPocketedListener;
    private ObjectsRestListener objectsRestListener;

    public void setBallPocketedListener(BallPocketedListener listener) {
        this.ballPocketedListener = listener;
    }
    public void setObjectsRestListener(ObjectsRestListener listener) {
        this.objectsRestListener = listener;
    }

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

        List<Body> bodies = world.getBodies();
        int i = 0;
        boolean flag = true;
        while(i < bodies.size()) {
            if (bodies.get(i).getLinearVelocity().getMagnitude() != 0) {
                flag = false;
            }
            i++;
        }
        if (flag == true) {
            // inform Game that objects are resting
            objectsRestListener.onStartAllObjectsRest();
        }
        else {
            objectsRestListener.onEndAllObjectsRest();
        }

    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        // TBD: use listener to notify necessary subsystems

        //System.out.println(point.getBody1().getUserData() + " " + point.getBody2().getUserData());
        return true;
    }

    @Override
    public void end(ContactPoint point) {

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        // TBD: use this method to check if a ball and a pocket (sensor) overlap and notify
        // the necessary subsystems. For checking if a sensor is involved use point.isSensor()

        if (point.isSensor()) {
            Ball ball = (Ball) point.getBody1().getUserData();
            Body pocketBody = point.getBody2();
            Body ballBody = point.getBody1();

            Vector2 pocketCenter = pocketBody.getWorldCenter();
            Vector2 ballCenter = ballBody.getWorldCenter();

            double distance = ballCenter.distance(pocketCenter);

            double ballRadius = Ball.Constants.RADIUS;
            if (distance > 0.6) {
                System.out.println("Ball is pocketed: " + ball);
                world.removeBody(ballBody);
                ballPocketedListener.onBallPocketed(ball);
            }
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
