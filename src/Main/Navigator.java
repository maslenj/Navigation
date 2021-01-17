package Main;

import Main.Graphics.View.ViewCanvas;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Navigator {
    // Need to have access to a ViewCanvas for animation
    ViewCanvas viewCanvas;

    // Need to have a links map for animation
    HashMap<Integer, Connection> connectionHashMap = new HashMap<>();

    // something to store the graph (for doing caculations and stuff)
    HashMap<Integer, Node> nodes = new HashMap<>();

    // waypoints: each one correlates to an edge
    HashMap<Integer, ArrayList<Waypoint>> waypointsMap = new HashMap<>();

    // start and end points (for users to add)
    Node start;
    Node end;

    // For choosing whether to display path
    boolean pathFound = false;

    // For choosing what algorithm to use
    String algorithmChoice = "Dijkstra's";

    // screen specific data
    double minLat;
    double maxLat;
    double minLong;
    double maxLong;
    double scaleX;
    double scaleY;

    public Navigator(ViewCanvas viewCanvas) {
        readFiles("RoadData/Catlin1-allroads");
        this.viewCanvas = viewCanvas;
    }

    public double getDistance() {
        return (end != null)? end.distance: 0.0;
    }

    public int getNumNodes() {
        return (end != null)? end.path.size(): 0;
    }

    public void readFiles(String folder) {
        clear();

        // Read nodes
        try {
            String nodesFileName = folder + "/nodes.bin";
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(nodesFileName)));
            int numNodes = inStream.readInt();
            for (int i=0; i<numNodes; i++) {
                int nodeID = inStream.readInt();
                double longitude = inStream.readDouble();
                double latitude = inStream.readDouble();
                Node node = new Node(nodeID, longitude, latitude);
                nodes.put(nodeID, node);
            }
            inStream.close();
        }
        catch (IOException e) {
            System.err.println("error on nodes.bin: " + e.getMessage());
        }

        // Read links
        int numLinks = 0;
        try {
            String nodesFileName = folder + "/links.bin";
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(nodesFileName)));
            numLinks = inStream.readInt();
            for (int i=0; i<numLinks; i++) {
                int linkID = inStream.readInt();
                int firstNodeID = inStream.readInt();
                int lastNodeID = inStream.readInt();
                String linkLabel = inStream.readUTF();
                double length = inStream.readDouble();
                byte way = inStream.readByte();
                Connection c = new Connection(linkID, firstNodeID, lastNodeID, length);
                nodes.get(firstNodeID).connections.add(c);
                connectionHashMap.put(linkID, c);
                if (way == 2) {
                    Connection inverse = new Connection(-linkID, lastNodeID, firstNodeID, length);
                    nodes.get(lastNodeID).connections.add(inverse);
                    connectionHashMap.put(-linkID, inverse);
                }
            }
            inStream.close();
        }
        catch (IOException e) {
            System.err.println("error on links.bin: " + e.getMessage());
        }

        // Read waypoints
        try {
            String nodesFileName = folder + "/links-waypoints.bin";
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(nodesFileName)));
            for(int i=0; i < numLinks; i++){
                int linkID = inStream.readInt();
                int numWaypoints = inStream.readInt();
                waypointsMap.put(linkID, new ArrayList<>());
                for(int count = 0; count < numWaypoints; count += 1){
                    double waypointlong = inStream.readDouble();
                    double waypointlat = inStream.readDouble();
                    waypointsMap.get(linkID).add(new Waypoint(waypointlat, waypointlong));
                }
            }
            inStream.close();
        }
        catch (IOException e) {
            System.err.println("error on waypoints.bin: " + e.getMessage());
        }
    }

    public void draw(Graphics2D pen, int screenWidth, int screenHeight) {
        updateScreen(screenWidth, screenHeight);

        pen.setColor(Color.BLACK);
        for (int linkID: waypointsMap.keySet()) {
            if (pathFound) {
                if (end.path.contains(linkID) || end.path.contains(-linkID)) {
                    pen.setStroke(new BasicStroke(3));
                    pen.setColor(Color.RED);
                } else {
                    pen.setStroke(new BasicStroke(1));
                    pen.setColor(Color.BLACK);
                }
            }

            ArrayList<Waypoint> waypoints = waypointsMap.get(linkID);
            Waypoint prevWaypoint = waypoints.get(0);
            for (int i = 1; i < waypoints.size(); i ++) {
                Waypoint waypoint = waypoints.get(i);
                int x1 = convertCoord(prevWaypoint.longitude, scaleX, minLong, screenWidth);
                int y1 = convertCoord(prevWaypoint.latitude, scaleY, minLat, screenHeight);
                int x2 = convertCoord(waypoint.longitude, scaleX, minLong, screenWidth);
                int y2 = convertCoord(waypoint.latitude, scaleY, minLat, screenHeight);
                pen.drawLine(x1,y1,x2,y2);
                prevWaypoint = waypoints.get(i);
            }
        }

        pen.setStroke(new BasicStroke(4));
        if (start != null) {
            pen.setColor(Color.BLUE);
            pen.drawOval(convertCoord(start.longitude, scaleX, minLong, screenWidth) - 5,
                    convertCoord(start.latitude, scaleY, minLat, screenHeight) - 5,
                    10, 10);
        }
        if (end != null) {
            pen.setColor(Color.RED);
            pen.drawOval(convertCoord(end.longitude, scaleX, minLong, screenWidth) - 5,
                    convertCoord(end.latitude, scaleY, minLat, screenHeight) - 5,
                    10, 10);
        }
    }

    public void highlight(Graphics2D pen, int screenWidth, int screenHeight, int linkID, Color color) {
        pen.setStroke(new BasicStroke(3));
        pen.setColor(color);
        ArrayList<Waypoint> waypoints = waypointsMap.get(linkID);
        Waypoint prevWaypoint = waypoints.get(0);
        for (int i = 1; i < waypoints.size(); i ++) {
            Waypoint waypoint = waypoints.get(i);
            int x1 = convertCoord(prevWaypoint.longitude, scaleX, minLong, screenWidth);
            int y1 = convertCoord(prevWaypoint.latitude, scaleY, minLat, screenHeight);
            int x2 = convertCoord(waypoint.longitude, scaleX, minLong, screenWidth);
            int y2 = convertCoord(waypoint.latitude, scaleY, minLat, screenHeight);
            pen.drawLine(x1,y1,x2,y2);
            prevWaypoint = waypoints.get(i);
        }
    }

    public void addPoint(int x, int y, int screenWidth, int screenHeight) {
        updateScreen(screenWidth, screenHeight);

        // convert x and y to long and lat
        // = ((dim - p) / scale) + min
        double longitude = ((screenWidth - x) / scaleX) + minLong;
        double latitude = ((screenHeight - y) / scaleY) + minLat;

        // find closest node
        double closestDistance = 1000;
        Node closest = null;
        for (Node n: nodes.values()) {
            double distance = Math.sqrt(Math.pow(n.longitude - longitude, 2) + Math.pow(n.latitude - latitude, 2));
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = n;
            }
        }

        if (start == null) {
            start = closest;
        } else {
            end = closest;
        }

    }

    public void findPath() {
        pathFound = false;
        viewCanvas.draw();
        // make sure start and end nodes are set
        if (start != null && end != null) {
            // choose which algorithm to use
            if (algorithmChoice.equals("Dijkstra's")) {
                // set all distances to infinity
                for (Node n: nodes.values()) {
                    n.distance = Double.MAX_VALUE;
                    n.path = new ArrayList<>();
                    n.visited = false;
                }
                start.distance = 0;
                start.visited = true;
                // initialize frontier
                PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingDouble(node -> node.distance));
                for (Connection c: start.connections) {
                    Node n = nodes.get(c.endNode);
                    n.distance = c.length;
                    n.path.add(c.id);
                    frontier.add(n);
                }
                // do dijkstra's
                while (!end.visited && !frontier.isEmpty()) {
                    Node chosen = frontier.poll();
                    chosen.visited = true;
                    // add new nodes to frontier
                    for (Connection c: chosen.connections) {
                        Node n = nodes.get(c.endNode);
                        if (chosen.distance + c.length < n.distance) {
                            n.distance = chosen.distance + c.length;
                            n.path = new ArrayList<>();
                            n.path.addAll(chosen.path);
                            n.path.add(c.id);
                        }
                        if (!n.visited && !frontier.contains(n)) frontier.add(n);
                    }
                    // if animating, highlight connection
                    if (viewCanvas.animating) {
                        viewCanvas.highlight(chosen.path.get(chosen.path.size() - 1), Color.MAGENTA);
                    }
                }
            } else {
                // perform A* search
                for (Node node: nodes.values()) {
                    node.gScore = Double.POSITIVE_INFINITY;
                    node.fScore = Double.POSITIVE_INFINITY;
                }

                PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fScore));
                open.add(start);
                start.gScore = 0;
                start.fScore = h(start);

                while (!open.isEmpty()) {
                    Node currentNode = open.poll();
                    if (currentNode == end) break;
                    for (Connection c: currentNode.connections) {
                        Node neighbor = nodes.get(c.endNode);
                        double newGScore = currentNode.gScore + c.length;
                        if (newGScore < neighbor.gScore) {
                            neighbor.cameFrom = c;
                            neighbor.gScore = newGScore;
                            neighbor.fScore = newGScore + h(neighbor);
                            if (!open.contains(neighbor)) {
                                open.add(neighbor);
                            }
                        }
                        if (viewCanvas.animating) {
                            viewCanvas.highlight(c.id, Color.MAGENTA);
                        }
                    }
                }

                Node currentNode = end;
                while (currentNode != start) {
                    end.path.add(0, currentNode.cameFrom.id);
                    currentNode = nodes.get(currentNode.cameFrom.startNode);
                }
            }


            viewCanvas.draw();
            // path animation
            for (int linkID: end.path) {
                viewCanvas.highlight(linkID, Color.RED);
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pathFound = true;
        }
    }

    public void clearPath() {
        pathFound = false;
        start = null;
        end = null;
        for (Node n: nodes.values()) {
            n.visited = false;
            n.path = new ArrayList<>();
        }
    }

    public void setAlgorithm(String algorithmChoice) {
        this.algorithmChoice = algorithmChoice;
    }

    private void updateScreen(int screenWidth, int screenHeight) {
        minLat = 1000;
        maxLat = -1000;
        minLong = 1000;
        maxLong = -1000;
        for (int linkID: waypointsMap.keySet()) {
            for (Waypoint waypoint: waypointsMap.get(linkID)) {
                minLat = Math.min(minLat, waypoint.latitude);
                maxLat = Math.max(maxLat, waypoint.latitude);
                minLong = Math.min(minLong, waypoint.longitude);
                maxLong = Math.max(maxLong, waypoint.longitude);
            }
        }
        scaleX = screenWidth / (maxLong - minLong);
        scaleY = screenHeight / (maxLat - minLat);
    }

    private int convertCoord(double coord, double scale, double min, double dim) {
        return (int) (dim - ((coord - min) * scale));
    }

    private void clear() {
        nodes = new HashMap<>();
        waypointsMap = new HashMap<>();
    }

    private double h(Node n) {
        return 69 * Math.sqrt(Math.pow(n.latitude - end.latitude, 2) + Math.pow(n.longitude - end.longitude, 2));
    }

    static class Waypoint {
        double latitude;
        double longitude;

        public Waypoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    static class Node {
        int id;
        double longitude;
        double latitude;
        ArrayList<Connection> connections = new ArrayList<>();

        // for Djikstra's
        double distance;
        boolean visited;
        ArrayList<Integer> path = new ArrayList<>();

        // for A*
        double gScore; // cheapest path to node currently known
        double fScore; // gScore + h(n)
        Connection cameFrom; // to reconstruct path

        Node(int id, double longitude, double latitude) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    static class Connection {
        int id;
        int startNode;
        int endNode;
        double length;

        public Connection(int id, int startNode, int endNode, double length) {
            this.id = id;
            this.startNode = startNode;
            this.endNode = endNode;
            this.length = length;
        }
    }
}
