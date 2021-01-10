package Graphics.View;

import java.awt.*;
import java.awt.event.MouseListener;

public class ViewCanvas extends EventfulImageCanvas implements MouseListener {
    public ViewCanvas(int width, int height) {
        super(width, height);
    }

    public void resized() {
        draw();
    }

    void draw() {
        int width = getWidth();
        int height = getHeight();
        Graphics2D pen = getPen();
        clear();
        display();
    }
}

