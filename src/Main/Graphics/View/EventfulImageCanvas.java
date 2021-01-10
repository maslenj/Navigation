package Main.Graphics.View;

import java.awt.event.*;

/**
 * A Canvas that listens to Mouse and Keyboard events.
 * Override the listeners that you care about in an extension class.
 */

public class EventfulImageCanvas extends ImageCanvas implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    public EventfulImageCanvas(int width, int height)
    {
        super(width, height);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    /** This is called automatically to notify the Canvas that it has been added to the GUI.
     * You should not call this function yourself.
     */
    public void addNotify()
    {
        super.addNotify();
        requestFocusInWindow();
    }




    // you should override any of these methods that you need to

    public void mousePressed(MouseEvent event)
    {
        // int mouseX = event.getX();
        // int mouseY = event.getY();
    }

    public void mouseReleased(MouseEvent event)
    {
    }

    public void mouseDragged(MouseEvent event)
    {
    }

    public void mouseMoved(MouseEvent event)
    {
    }

    public void mouseClicked(MouseEvent event)
    {
    }

    public void mouseEntered(MouseEvent event)
    {
    }

    public void mouseExited(MouseEvent event)
    {
    }

    public void mouseWheelMoved(MouseWheelEvent event)
    {
        // int clicks = event.getWheelRotation();
    }

    public void keyPressed(KeyEvent event)
    {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

		/*  examples:
     int code = event.getKeyCode();
     if (code == KeyEvent.VK_LEFT) {
     }
     if (code == KeyEvent.VK_RIGHT) {
     }
     if (code == KeyEvent.VK_UP) {
     }
     if (code == KeyEvent.VK_DOWN) {
     }
     if (code == KeyEvent.VK_SPACE) {
     }
		 */
    }

    public void keyReleased(KeyEvent event)
    {
    }

    public void keyTyped(KeyEvent event)
    {
    }

}