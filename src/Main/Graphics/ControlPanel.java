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
    private final JComboBox<String> chooseBox;
    private final JComboBox<String> algorithmBox;
    private final JLabel distanceLabel;
    private final JLabel numNodesLabel;

    public ControlPanel(ViewCanvas view) {
        this.view = view;

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        actionPanel.setBorder(new TitledBorder("Path Finding"));

        algorithmBox = new JComboBox<>();
        algorithmBox.addItem("Dijkstra's");
        algorithmBox.addItem("A*");
        actionPanel.add(algorithmBox);

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
        chooseBox.addActionListener(new ChooseBoxListener());
        choosePanel.add(chooseBox);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new clearButtonListener());
        choosePanel.add(clearButton);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new FlowLayout());
        resultsPanel.setBorder(new TitledBorder("Results"));

        distanceLabel = new JLabel("Distance: 0.0");
        numNodesLabel = new JLabel("Number of Nodes: 0");
        resultsPanel.add(distanceLabel);
        resultsPanel.add(numNodesLabel);

        setLayout(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);
        add(choosePanel, BorderLayout.WEST);
        add(resultsPanel, BorderLayout.CENTER);
    }

    public void updateLabels() {
        distanceLabel.setText("Distance: " + Math.round(view.navigator.getDistance() * 100) / 100.0);
        numNodesLabel.setText("Number of Nodes: " + view.navigator.getNumNodes());
    }

    class FindPathButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            view.navigator.findPath();
            updateLabels();
            view.draw();
        }
    }

    class clearButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            view.navigator.clearPath();
            updateLabels();
            view.draw();
        }
    }

    class ChooseBoxListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            view.navigator.readFiles("RoadData/" + Objects.requireNonNull(chooseBox.getSelectedItem()).toString());
            view.navigator.clearPath();
            view.draw();
        }
    }
}
