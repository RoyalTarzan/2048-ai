package game;

import agent.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Window extends JFrame implements KeyListener,ActionListener {
    public final Engine engine=new Engine();
    private static JLabel label=new JLabel();
    public final JLabel[][] labels=new JLabel[][]{
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()}};
    public final JLabel points=new JLabel();
    private int numberOfAgents=0;
    public ArrayList<Agent> agents=new ArrayList<>();
    public ArrayList<JButton> buttons=new ArrayList<>();
    public final JButton startButton=new JButton();
    public final JButton resetButton=new JButton();

    public Window(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Main Window");
        this.setBackground(Color.cyan);
        this.setOpacity(1.0f);
        this.setLayout(null);
        this.setSize(500,500);
        engine.random2or4();

        label.setBounds(30,30,400,400);
        label.setBackground(Color.black);
        label.setOpaque(true);
        points.setBounds(30,0,400,30);
        points.setBackground(Color.cyan);
        points.setOpaque(true);
        startButton.setBounds(430,0,50,25);
        startButton.setVisible(true);
        resetButton.setBounds(430,25,50,25);
        resetButton.setVisible(true);

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
        this.add(label);
        this.setVisible(true);
        this.add(resetButton);
        this.add(startButton);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==startButton){
            for (int i = 0; i < 100; i++) {
                agents.add(new Agent(numberOfAgents+1));
                numberOfAgents++;
                int finalI = i;
                buttons.add(new JButton());
                buttons.get(i).addActionListener((event)->{agents.get(finalI).visible();System.out.println("Yippee");});
                buttons.get(i).setBounds(430,50+i*25,50,25);
                this.requestFocus();
                this.add(buttons.get(i));
            }
        } else if (e.getSource()==resetButton) {
            engine.reset();
        }
    }
}
