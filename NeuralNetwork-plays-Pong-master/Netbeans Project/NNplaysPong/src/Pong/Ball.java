package Pong;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ball extends Thread {

    private final Pong game;

    public static float MAXBOUNCEANGLE = 0.9f;

    protected final int DIAMETER = 20;

    protected float x = Pong.WIDTH / 2 - DIAMETER / 2;
    protected float y = Pong.HEIGHT / 2 - DIAMETER / 2;

    protected Vector2D direction = new Vector2D(1, 1);

    public float increment = 0.0025f;
    public float currentSpeed = 1.5f;
    public float maxSpeed = 2.2f;

    protected boolean going_up = true;
    protected boolean going_right = true;

    public Ball(Pong game) {
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Move (y, an input, will be updated), then learn
                move();
                game.learn();
            } catch (InterruptedException ex) {
                Logger.getLogger(Ball.class.getName()).log(Level.SEVERE, null, ex);
            }
            Pong.panel.repaint();
            try {
                sleep(3);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void move() throws InterruptedException {
        if (y <= 0) {
            direction.y = direction.y * -1;
        } else if (y + DIAMETER >= Pong.HEIGHT) {
            direction.y = direction.y * -1;
        }
        if (going_right) {
            if (getBounds().intersects(Pong.panel.p2.getBounds())) { // Bounce when it hits the paddle 2 and update the score.
                
                // this is used to prevent the ball from going the wrong way
                going_right = false;
                currentSpeed += (currentSpeed + increment > maxSpeed) ? maxSpeed : increment;

                getBounceAngle(Pong.panel.p2.y, y);

                direction.x = direction.x < 0 ? direction.x : direction.x * -1;

                Pong.panel.p2.score++;
                Pong.panel.score2.setText(Integer.toString(Pong.panel.p2.score));

            }
            if (x + DIAMETER >= Pong.WIDTH) { // Gameover if it hits the right border
                game.gameOver();
            }
        }

        if (!going_right) {

            if (getBounds().intersects(Pong.panel.p1.getBounds())) { // Bounce when it hits the paddle 2 and update the score
                going_right = true;

                currentSpeed += (currentSpeed + increment > maxSpeed) ? maxSpeed : increment;

                direction = getBounceAngle(Pong.panel.p1.y, y);
                direction.x = direction.x > 0 ? direction.x : Math.abs(direction.x);

                // direction.x = Math.abs(direction.x);
                Pong.panel.p1.score++;
                Pong.panel.score1.setText(Integer.toString(Pong.panel.p1.score));
            } else if (x <= 0) { // Gameover if it hits the right border
                game.gameOver();
            }
        }
        x += direction.x;
        y += direction.y;
    }

    private Vector2D getBounceAngle(float paddleY, float intersectY) {
        float relativeIntersectY = intersectY - (paddleY + (Paddle.HEIGHT / 2));

        float normalizedRelativeIntersectionY = (relativeIntersectY / (Paddle.HEIGHT / 2));
        float bounceAngle = normalizedRelativeIntersectionY * MAXBOUNCEANGLE;

        Vector2D returnVector = new Vector2D();

        float relativeY = (relativeIntersectY / (Paddle.HEIGHT / 2)) * MAXBOUNCEANGLE;

        returnVector.y = relativeY / 1 * currentSpeed;

        returnVector.x = (currentSpeed - Math.abs(returnVector.y));

        System.out.println("cs = " + currentSpeed + ", relativeY = " + relativeY + ", relativeIntersect = " + relativeIntersectY + ", x = " + returnVector.x + ", y + " + returnVector.y + ", currentSpeed = " + currentSpeed);

        /*
        returnVector.x = currentSpeed * Math.cos(bounceAngle);
        returnVector.y = currentSpeed * -Math.sin(bounceAngle);*/
        returnVector.multiply(currentSpeed);

        return returnVector;
    }

    protected Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, DIAMETER, DIAMETER);
    }

    protected void paint(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval((int) x, (int) y, DIAMETER, DIAMETER);
    }
}
