package Main.Graphics;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import Main.Graphics.View.ViewCanvas;

public class ControlPanel extends JPanel {
    public ViewCanvas view;
    private JComboBox<String> chooseBox;
    private JLabel distance;
    private JLabel numNodes;

    public ControlPanel(ViewCanvas view) {
        this.view = view;

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        actionPanel.setBorder(new TitledBorder("Path Finding"));

        JButton findPathButton = new JButton("Find Path");
        findPathButton.addActionListener(new FindPathButtonListener());
        actionPanel.add(findPathButton);

        JPanel choosePanel = new JPanel();
        choosePanel.setLayout(new FlowLayout());
        choosePanel.setBorder(new TitledBorder("Choose"));

        chooseBox = new JComboBox<>();
        chooseBox.addItem("Catlin1-allroads");
        chooseBox.addItem("Catlin2-allroads");
        chooseBox.addItem("Portland1-primary");
        chooseBox.addItem("Portland1-secondary");
        chooseBox.addItem("Oregon-primary");
        chooseBox.addItem("Oregon-secondary");
        chooseBox.addItem("Oregon-allroads");
        chooseBox.addActionListener(new ChooseBoxListener());
        choosePanel.add(chooseBox);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new clearButtonListener());
        choosePanel.add(clearButton);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new FlowLayout());
        resultsPanel.setBorder(new TitledBorder("Results"));

        distance = new JLabel("Distance: 0.0");
        numNodes = new JLabel("Number of Nodes: 0");
        resultsPanel.add(distance);
        resultsPanel.add(numNodes);

        setLayout(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);
        add(choosePanel, BorderLayout.WEST);
        add(resultsPanel, BorderLayout.CENTER);
    }

    class FindPathButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            // Find the path
        }
    }

    class clearButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            // Clear screen
        }
    }

    class ChooseBoxListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            // Choose
            System.out.println(Objects.requireNonNull(chooseBox.getSelectedItem()).toString());
            view.navigator.readFiles("RoadData/" + Objects.requireNonNull(chooseBox.getSelectedItem()).toString());
            view.display();
        }
    }
}
