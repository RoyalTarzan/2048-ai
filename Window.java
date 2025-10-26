import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame implements KeyListener {
    private final Engine engine=new Engine();
    private JLabel[][] labels=new JLabel[][]{
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()},
            {new JLabel(),new JLabel(),new JLabel(),new JLabel()}};

    public Window(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBackground(Color.cyan);
        this.setOpacity(1.0f);
        this.setLayout(null);
        this.setSize(500,500);
        engine.random2or4();

        JLabel label = new JLabel();
        label.setBounds(30,30,400,400);
        label.setBackground(Color.black);
        label.setOpaque(true);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[i][j].setBackground(Color.white);
                labels[i][j].setOpaque(true);
                labels[i][j].setBounds(10+i*70,10+j*70,60,60);
                label.add(labels[i][j]);
            }
        }
        this.addKeyListener(this);
        this.add(label);
        this.setVisible(true);
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

    private void update(){
        int[][] board=engine.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[j][i].setText(String.valueOf(board[i][j]));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
