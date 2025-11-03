package agent;

import game.Engine;
import game.Window;

import java.awt.*;
import java.awt.event.KeyEvent;

public class AgentWindow extends Window {
    public static Engine engine=new Engine();
    public AgentWindow(String title){
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(title);
    }

    @Override
    public void update(Graphics g) {
        super.update(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
