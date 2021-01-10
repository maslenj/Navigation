import Graphics.ControlPanel;
import Graphics.MainWindow;
import Graphics.View.ViewCanvas;

public class Controller {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Stack");
        ViewCanvas vc = new ViewCanvas(800, 800);
        new MainWindow("main window", vc, new ControlPanel(vc));
    }
}
