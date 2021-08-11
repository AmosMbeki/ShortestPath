package com.amosmbeki;

import java.util.LinkedHashMap;
import java.util.Map;

public class Node {
    private String name;
    /** Map that contains {neighbor of this node, distance to neighbor}.*/
    private Map<Node, Integer> neighbors;
    /** Where the chosen nodes come from. */
    private Node source;
    /** Node's heuristic */
    private int heuristic;

    public Node(String name, int heuristic) {
        this.name = name;
        this.heuristic = heuristic;
        neighbors = new LinkedHashMap<>();
    }

    public String getName(){
        return name;
    }

    public void setSource(Node source){
        this.source = source;
    }

    public Node getSource(){
        return source;
    }

    public int getHeuristic(){
        return heuristic;
    }

    public void addNeighbor(Node neighbor, int distance){
        this.neighbors.put(neighbor, distance);
    }

    public Map<Node, Integer> getNeighbors(){
        return neighbors;
    }

    @Override
    public String toString() {
        String out = "";

        out = "[" + " name = " + this.name + ", neighbors = [ ";
        for (Node node : this.neighbors.keySet()) {
            out += node.getName() + " ";
        }
        out += "] ]";

        return out;
    }
}
