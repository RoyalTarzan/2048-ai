package game;

import agent.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame implements KeyListener {
    public final Engine engine=new Engine();
    private final JLabel label=new JLabel();
    private final JLabel[][] labels=new JLabel[][]{
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()}};
    private final JLabel points=new JLabel();
    private int numberOfAgents=0;

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
        this.add(label);
        this.setVisible(true);
        update();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if ("a".equals(String.valueOf(e.getKeyChar()))){
            new Agent(numberOfAgents);
            numberOfAgents++;
            this.requestFocus();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case 37:engine.moveLeft();System.out.println("left");update();break;
            case 39:engine.moveRight();System.out.println("right");update();break;
            case 38:engine.moveUp();System.out.println("up");update();break;
            case 40:engine.moveDown();System.out.println("down");update();break;
        }
    }

    private void update(){
        int[][] board=engine.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[j][i].setText(String.valueOf(board[i][j]));
            }
        }
        points.setText(String.valueOf(engine.getPoints()));
        if (engine.lose()){
            label.setText("You Lost!");
            engine.reset();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
