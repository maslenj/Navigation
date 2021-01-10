package Main.Graphics.View;

import java.awt.*;
import java.awt.event.MouseListener;

import Main.Navigator;

public class ViewCanvas extends EventfulImageCanvas implements MouseListener {

    Navigator nav = new Navigator();

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
        nav.draw(pen, width, height);
        display();
    }
}

