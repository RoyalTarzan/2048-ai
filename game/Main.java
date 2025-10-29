package game;

public class Main {
    public static void main(String[] args) {
        new Window();
        Engine engine=new Engine();
        engine.setBoard(new int[][]{
                {0,4,4,4},
                {0,4,4,4},
                {0,4,4,4},
                {0,4,4,4}});
        engine.moveUp();
    }
}