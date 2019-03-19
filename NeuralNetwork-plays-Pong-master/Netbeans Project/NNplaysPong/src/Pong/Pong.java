package Pong;

import NeuralNetwork.NeuralNetwork;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Pong {

    protected static final int WIDTH = 640;
    protected static final int HEIGHT = 400;
    protected static JFrame frame;
    protected static Panel panel;

    public static int GAMESPEED = 7;

    // Setup neural network
    private final int genomes_per_generation = 10;
    private final int neurons_amount[] = {5, 5, 3, 1};

    private final double random_mutation_probability = 0.1;
    private final double minWeight = -1;
    private final double maxWeight = 1;

    private final NeuralNetwork nn = new NeuralNetwork(neurons_amount, genomes_per_generation, random_mutation_probability, minWeight, maxWeight);
    protected boolean autoplay = true;
    private final double inputs[] = new double[5];
    private double outputs[] = new double[1];

    public static void main(String[] args) {
        new Pong();        
    }

    public Pong() {
        frame = new JFrame("Pong");
        panel = new Panel(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        
        
        System.out.println(toString());
    }

    protected void learn() {
        // Bot
        if (autoplay) {
            if (panel.ball.y > panel.p2.y) {
                panel.p2.direction = 'd';
            } else {
                panel.p2.direction = 'u';
            }
        }

        // Get the inputs from the game and the output from the neural network
        inputs[0] = panel.ball.y;
        inputs[1] = panel.ball.x;
        inputs[2] = panel.p1.y;
        inputs[3] = panel.ball.direction.x;
        inputs[4] = panel.ball.direction.y;

        outputs = nn.getOutputs(inputs);

        // Do an action according to the output value
        if (outputs[0] > 0.5) {
            panel.p1.direction = 'u';
        } else {
            panel.p1.direction = 'd';
        }
    }

    protected void gameOver() throws InterruptedException {
        // Get the fitness of the current genome, then create a new genome
        nn.newGenome(panel.p1.score);

        Thread.sleep(2000 / Pong.GAMESPEED);

        // RESET ALL
        panel.ball.x = WIDTH / 2 - panel.ball.DIAMETER / 2;
        panel.ball.y = HEIGHT / 2 - panel.ball.DIAMETER / 2;
        Random r = new Random();
        panel.ball.going_right = r.nextBoolean();
        panel.ball.going_up = r.nextBoolean();
        panel.ball.currentSpeed = panel.ball.standardCurrentSpeed;
        int random = r.nextInt(((int) (Ball.MAXBOUNCEANGLE * 100) - 10) + 1) + 10;
        panel.ball.direction = new Vector2D(panel.ball.going_right ? ((panel.ball.currentSpeed - (float) random / 100)) : -((panel.ball.currentSpeed - (float) random / 100)), panel.ball.going_up ? (float) random / 100 : -(float) random / 100);

        panel.p1.score = 0;
        panel.p2.score = 0;
        panel.p1.x = 10;
        panel.p1.y = Pong.HEIGHT / 2 - Paddle.HEIGHT / 2;
        panel.p2.x = Pong.WIDTH - 10 - Paddle.WIDTH;
        panel.p2.y = Pong.HEIGHT / 2 - Paddle.HEIGHT / 2;

        panel.score1.setText("0");
        panel.score2.setText("0");
    }
    
    public String toString(){
        String returnstring = "";
        returnstring += "Random mutation probability = " + random_mutation_probability + "\n MinWeight = " + minWeight + "\n maxWeight = " + maxWeight + "\n Genomes per generation = " + genomes_per_generation + "\n Neural network setup = ";
        for (int i = 0; i < neurons_amount.length; i++){
            returnstring += ", " + neurons_amount[i]; 
        }
        return returnstring;
    }
}
