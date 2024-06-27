package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.physics.BallPocketedListener;
import at.fhv.sysarch.lab4.physics.Physics;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

public class Game implements BallPocketedListener {
    private final Renderer renderer;
    private final Physics physics;

    private Vector2 strikeStart;

    private int count;

    public Game(Renderer renderer, Physics physics) {
        this.renderer = renderer;
        this.physics = physics;
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        strikeStart = new Vector2(pX,pY);
        renderer.updateCueStartPosition(pX,pY);

    }

    public void onMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        Vector2 strikeEnd = new Vector2(pX, pY);
        double strikeStartX = strikeStart.x;
        double strikeStartY = strikeStart.y;

        // calculate direction
        double dirAngle = Math.atan2(pY - strikeStartY, pX - strikeStartX);
        double dirX = Math.cos(dirAngle);
        double dirY = Math.sin(dirAngle);

        Ray ray = new Ray(strikeStart, new Vector2(-dirX, -dirY));
        List<RaycastResult> results = new ArrayList<>();

        this.physics.getWorld().raycast(ray, 0, true, false, results );

        //results.forEach(r -> System.out.println(r.getBody().getUserData()));

        double distance = calculateDistance(strikeStart, strikeEnd);

        results.get(0).getBody().applyForce(new Vector2(-dirX,-dirY).multiply(distance*2000));

        // reset Cue
        renderer.updateCueStartPosition(0,0);
        renderer.updateCueEndPosition(0,0);
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);

        renderer.updateCueEndPosition(pX,pY);
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2*Ball.Constants.RADIUS*2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            renderer.addBall(b);

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    private void initWorld() {
        List<Ball> balls = new ArrayList<>();
        
        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
            this.physics.addBodyToWorld(b.getBody());
        }
       
        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        this.physics.addBodyToWorld(Ball.WHITE.getBody());
        
        renderer.addBall(Ball.WHITE);
        
        Table table = new Table();
        this.physics.addBodyToWorld(table.getBody());
        renderer.setTable(table);
    }

    private double calculateDistance(Vector2 v1, Vector2 v2) {
        double dx = v2.x - v1.x;
        double dy = v2.y - v1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        renderer.removeBall(b);
        count++;
        if (count >= 2) {
            resetWorld();
        }
        System.out.println("BALL POCKETED " + count);
        return true;
    }

    private void resetWorld() {
        count = 0;
        renderer.resetBalls();

        List<Ball> balls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }

        this.placeBalls(balls);

        // TODO: if white ball gets pocketed, he needs to be added a body to
        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);

        renderer.addBall(Ball.WHITE);

    }

}