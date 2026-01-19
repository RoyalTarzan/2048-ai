package game;

import agent.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Window extends JFrame implements KeyListener,ActionListener {
    public Engine engine;
    private static final JLabel label=new JLabel();
    public final JLabel[][] labels=new JLabel[][]{
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()}};
    public final JLabel points=new JLabel();
    public ArrayList<Agent> agents=new ArrayList<>();
    public ArrayList<JButton> buttons=new ArrayList<>();
    public final JButton startButton=new JButton();
    public final JButton resetButton=new JButton();
    public final JButton updateButton=new JButton();
    private int currentAgent;
    public final JButton ownEngineButton=new JButton();
    private final Engine ownEngine=new Engine();
    private boolean simulationStarted=false;

    public Window(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Main Window");
        this.setBackground(Color.cyan);
        this.setOpacity(1.0f);
        this.setLayout(null);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        label.setBounds(30,30,400,400);
        label.setBackground(Color.black);
        label.setOpaque(true);
        points.setBounds(30,0,400,30);
        points.setBackground(Color.cyan);
        points.setOpaque(true);
        startButton.setBounds(430,0,100,25);
        startButton.setText("Start");
        startButton.setVisible(true);
        resetButton.setBounds(430,25,100,25);
        resetButton.setText("Reset");
        resetButton.setVisible(true);
        updateButton.setBounds(530,0,100,25);
        updateButton.setText("Update");
        updateButton.setVisible(true);
        ownEngineButton.setBounds(530,25,100,25);
        ownEngineButton.setText("Own Game");
        ownEngineButton.setVisible(true);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[i][j].setBackground(Color.white);
                labels[i][j].setOpaque(true);
                labels[i][j].setBounds(10+i*70,10+j*70,60,60);
                label.add(labels[i][j]);
            }
        }
        this.add(points);
        this.addKeyListener(this);
        startButton.addActionListener(this);
        resetButton.addActionListener(this);
        updateButton.addActionListener(this);
        ownEngineButton.addActionListener(this);
        this.add(label);
        this.setVisible(true);
        this.add(resetButton);
        this.add(startButton);
        this.add(updateButton);
        this.add(ownEngineButton);
        this.engine=ownEngine;
        update();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case 37:engine.moveLeft();System.out.println("left");update();break;
            case 39:engine.moveRight();System.out.println("right");update();break;
            case 38:engine.moveUp();System.out.println("up");update();break;
            case 40:engine.moveDown();System.out.println("down");update();break;
        }
    }

    public void update(){
        int[][] board=engine.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[j][i].setText(String.valueOf(board[i][j]));
            }
        }
        points.setText(String.valueOf(engine.getPoints()));
        if (engine.lose()){
            label.setText("You Lost!");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    private void newGeneration(){
        int halfPopulation=agents.size()/2;
        agents.removeIf(agent -> agents.indexOf(agent)>=(halfPopulation));
        for (Agent agent:agents){
            agent.mutate();
        }
        //System.out.println("Agents mutated successfully");
        for (int i = 0; i < halfPopulation; i++) {
            //System.out.println("Creating new agent: "+(i+1));
            agents.add(new Agent(agents.get(new Random().nextInt(0,halfPopulation)),agents.get(new Random().nextInt(0,halfPopulation))));
            //System.out.println("Created new agent: "+(i+1));
        }
        update(this.getGraphics());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==startButton){
            if (!simulationStarted){
                for (int i = 0; i < 100; i++) {
                    agents.add(new Agent());
                    int finalI = i;
                    buttons.add(new JButton());
                    buttons.get(i).addActionListener((event)->{
                        engine=agents.get(finalI).getEngine();
                        update();
                        currentAgent=finalI;
                    });
                    buttons.get(i).setText("Agent "+(finalI+1));
                    if (50+(i*25)<getSize().getHeight()){
                        buttons.get(i).setBounds(430,50+i*25,100,25);
                    }else if (50+(i-getSize().getHeight())*25<getSize().getHeight()){
                        buttons.get(i).setBounds(530, (int) (50+(i-getSize().getHeight()/25)*25),100,25);
                    } else if (50+(i-2*(getSize().getHeight()-50)/25)*25<getSize().getHeight()) {
                        buttons.get(i).setBounds(630, (int) (50+(i-(getSize().getHeight()-50)*2/25)*25),100,25);
                    }else{
                        buttons.get(i).setBounds(730, (int) (50+(i-(getSize().getHeight()-50)*3/25)*25),100,25);
                    }
                    requestFocus();
                    add(buttons.get(i));
                }
                update(this.getGraphics());
                simulationStarted=true;
                startButton.setText("Next Gen");
            }else {
                for (Agent agent:agents){
                    agent.calculateScore();
                }
                agents.sort(Comparator.comparingInt(agent -> agent.score*-1));
                for (Agent agent:agents){
                    if (agents.indexOf(agent)>4 && agents.indexOf(agent)<((agents.size()/2)-3)){continue;}
                    if (agents.indexOf(agent)<(agents.size()-5) && agents.indexOf(agent)>((agents.size()/2)+1)){continue;}
                    System.out.println((agents.indexOf(agent)+1)+" "+agent.score);
                }
                engine=agents.getFirst().getEngine();
                update();
                update(this.getGraphics());
                newGeneration();
            }
        } else if (e.getSource()==resetButton) {
            engine.reset();
        } else if (e.getSource()==updateButton) {
            agents.get(currentAgent).setEngine(engine);
            agents.get(currentAgent).outputMove();
            System.out.println(agents.get(currentAgent).lastMove);
            update();
        } else if (e.getSource()==ownEngineButton){
            engine=ownEngine;
            updateButton.addActionListener((event)->update());
        }
    }
}
