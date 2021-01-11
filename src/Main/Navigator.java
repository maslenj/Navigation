package Main;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Navigator {
    // something to store the graph (for doing caculations and stuff)
    HashMap<Integer, Node> nodes = new HashMap<>();

    // waypoints: each one correlates to an edge
    HashMap<Integer, ArrayList<Waypoint>> waypointsMap = new HashMap<>();

    public Navigator() {
        readFiles("RoadData/Catlin1-allroads");
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
                nodes.get(firstNodeID).connections.add(new Connection(linkID, firstNodeID, lastNodeID, length));
                if (way == 2) {
                    nodes.get(lastNodeID).connections.add(new Connection(-linkID, lastNodeID, firstNodeID, length));
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
        // find the max and min latitudes and longitudes
        // use these to find the corner and scale
        double minLat = 1000;
        double maxLat = -1000;
        double minLong = 1000;
        double maxLong = -1000;
        for (int linkID: waypointsMap.keySet()) {
            for (Waypoint waypoint: waypointsMap.get(linkID)) {
                minLat = Math.min(minLat, waypoint.latitude);
                maxLat = Math.max(maxLat, waypoint.latitude);
                minLong = Math.min(minLong, waypoint.longitude);
                maxLong = Math.max(maxLong, waypoint.longitude);
            }
        }
        double scaleX = screenWidth / (maxLong - minLong);
        double scaleY = screenHeight / (maxLat - minLat);

        pen.setColor(Color.BLACK);
        for (int linkID: waypointsMap.keySet()) {
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
    }

    private int convertCoord(double coord, double scale, double min, double dim) {
        return (int) (dim - ((coord - min) * scale));
    }

    private void clear() {
        nodes = new HashMap<>();
        waypointsMap = new HashMap<>();
    }

    static class Waypoint {
        double latitude;
        double longitude;

        public Waypoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    class Node {
        int id;
        double longitude;
        double latitude;
        ArrayList<Connection> connections = new ArrayList<>();

        Node(int id, double longitude, double latitude) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    class Connection {
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
