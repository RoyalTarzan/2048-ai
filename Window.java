import javax.swing.*;

public class Window {
    private final JFrame frame;
    private final JLabel label;

    public Window(String windowName){
        this.label =new JLabel(windowName);
        this.frame=new JFrame(windowName);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JLabel getLabel() {
        return label;
    }
}
