package Main.Graphics;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow(String title, Component viewPanel, Component controlPanel) {
        super(title);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(viewPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
    }
}
