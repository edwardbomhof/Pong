package Pong;

import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.SaveLoad;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import java.util.Random;

public class Pong {

    private static Pong _instance;

    public static Pong GetInstance() {
        if (_instance == null) {
            _instance = new Pong();
        }
        return _instance;
    }

    public static void RemovePong() {
        _instance = null;
    }

    protected static final int WIDTH = 640;
    protected static final int HEIGHT = 400;
    protected static JFrame frame;
    protected static Panel panel;

    public static int generationsToRun = 2;

    public static int GAMESPEED = 10;

    private static double[][] inputRuns = { ///Genomes per generation, runs, min_weight, max_weight, random_mutation_probability
        {10, 125, -1, 1, 0.5},
        {10, 125, -2, 2, 0.5},
        {10, 125, -1, 1, 1},
        {10, 125, -2, 2, 1},
        {10, 125, -2, 2, 2},
        {10, 125, -1.5, 1.5, 1.5},
        {10, 125, -1.5, 1.5, 1.5},
        {3, 417, -1, 1, 1},
        {3, 417, -2, 2, 2},
        {5, 250, -1, 1, 1},
        {5, 250, -2, 2, 2}};

    private static int[][] neurons_amount_runs
            = {
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1},
                {2, 2, 1}};

    public static int totalRuns = inputRuns.length;

    // Setup neural network
    private static int genomes_per_generation = 10;
    private static int neurons_amount[] = {2,2, 1};

    private static double random_mutation_probability = 1;
    private static double minWeight = -1;
    private static double maxWeight = 1;

    private static NeuralNetwork nn; // = new NeuralNetwork(neurons_amount, genomes_per_generation, random_mutation_probability, minWeight, maxWeight);
    protected boolean autoplay = true;
    private static final double inputs[] = new double[2];
    private static double outputs[] = new double[1];

    private static Pong pong;

    private static int currentGame = 0;

    public static void main(String[] args) {
        run();
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

    public void close() {

        frame = null;

        nn.live_view = null;
        nn.file = null;
        nn = null;
        pong = null;
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
        inputs[1] = panel.p1.y;
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

        //Thread.sleep(2000);
        // RESET ALL
        panel.ball.x = WIDTH / 2 - panel.ball.DIAMETER / 2;
        panel.ball.y = HEIGHT / 2 - panel.ball.DIAMETER / 2;
        Random r = new Random();
        panel.ball.going_right = r.nextBoolean();
        panel.ball.going_up = r.nextBoolean();
        panel.ball.currentSpeed = panel.ball.standardCurrentSpeed;
        int random = r.nextInt(((int) (0.6f * 100) - 10) + 1) + 10;
        panel.ball.direction = new Vector2D(panel.ball.going_right ? ((panel.ball.currentSpeed - (float) random / 100)) : -((panel.ball.currentSpeed - (float) random / 100)), panel.ball.going_up ? (float) random / 100 : -(float) random / 100);

        panel.p1.score = 0;
        panel.p2.score = 0;
        panel.p1.x = 10;
        panel.p1.y = Pong.HEIGHT / 2 - Paddle.HEIGHT / 2;
        panel.p2.x = Pong.WIDTH - 10 - Paddle.WIDTH;
        panel.p2.y = Pong.HEIGHT / 2 - Paddle.HEIGHT / 2;

        panel.score1.setText("0");
        panel.score2.setText("0");

        if (nn.current_generation == generationsToRun) {
            run();
        }
    }

    public String toString() {
        String returnstring = "";
        returnstring += "Random mutation probability = " + random_mutation_probability + "\n MinWeight = " + minWeight + "\n maxWeight = " + maxWeight + "\n Genomes per generation = " + genomes_per_generation + "\n Neural network setup = ";
        for (int i = 0; i < neurons_amount.length; i++) {
            returnstring += ", " + neurons_amount[i];
        }
        return returnstring;
    }

    public static void run() {
        if (currentGame < totalRuns) {
            if (pong != null) {
                pong.close();
            }
            neurons_amount = neurons_amount_runs[currentGame];

            genomes_per_generation = (int) inputRuns[currentGame][0];
            random_mutation_probability = inputRuns[currentGame][4];
            minWeight = (int) inputRuns[currentGame][2];
            maxWeight = (int) inputRuns[currentGame][3];
            generationsToRun = (int) inputRuns[currentGame][1];

            nn = new NeuralNetwork(neurons_amount, genomes_per_generation, random_mutation_probability, minWeight, maxWeight, "synapses" + currentGame + ".txt");

            nn.file = new File("scores" + currentGame + ".txt");

            pong = Pong.GetInstance();

            currentGame++;
        } else {
            System.exit(0);
        }
    }
}
