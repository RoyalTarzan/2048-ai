package game;

public class Main {
    public static void main(String[] args) {
        new Window();
        Window.engine.setBoard(new int[][]{
                {2,4,2,4},
                {4,2,4,2},
                {2,4,2,4},
                {4,2,4,2}});
        Window.update();
    }
}