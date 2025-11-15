package agent;

import game.Window;

import java.awt.event.KeyEvent;

public class AgentWindow extends Window {
    public AgentWindow(String title){
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setTitle(title);
        this.setVisible(false);
    }

    @Override
    public void update() {
        int[][] board=engine.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[j][i].setText(String.valueOf(board[i][j]));
            }
        }
        points.setText(String.valueOf(engine.getPoints()));
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
