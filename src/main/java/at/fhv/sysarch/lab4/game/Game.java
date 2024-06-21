package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.physics.Physics;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

public class Game {
    private final Renderer renderer;
    private final Physics physics;

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

        // TBD: This is just an example that illustrates how a raycast can be generated and
        // how a force can be applied to an object. Replace this with your implementation of
        // the cue and striking

        //Ball.WHITE.getBody().applyForce(new Vector2(1, 0).multiply(3000));

        Vector2 start = new Vector2(pX, pY);
        Vector2 dir = new Vector2(-1,0);
        Ray ray = new Ray(start, dir);
        List<RaycastResult> results = new ArrayList<>();

        this.physics.getWorld().raycast(ray, 0, true, false, results );

        results.forEach(r -> System.out.println(r.getBody().getUserData()));

        results.get(0).getBody().applyForce(new Vector2(-1,0).multiply(2000));
    }

    public void onMouseReleased(MouseEvent e) {
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);
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
}