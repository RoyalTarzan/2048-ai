//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        new Window("2048");
        Engine engine=new Engine();
        engine.setBoard(new int[][]{
                {0,4,4,4},
                {0,4,4,4},
                {0,4,4,4},
                {0,4,4,4}});
        engine.moveUp();
    }
}