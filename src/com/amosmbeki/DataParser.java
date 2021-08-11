package com.amosmbeki;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
    /** Saves the nodes read from the file */
    private List<Node> nodes;
    /** Path to file that contains the graph data (Nodes, Links). */
    public static String DATASET = "";
    /** After this point, all nodes are read and we start reading the links */
    private final String LINE_SPLITTER_IN_FILE = "---";

    public DataParser() {
        nodes = new ArrayList<>();
        readDataFromFile();
    }

    private void readDataFromFile(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATASET));
            String line = null;

            /** Counter that keeps track of the line numbers. */
            int lineNumber = 0;
            /** When true: type of line extraction changes. */
            boolean linkNodesFormat = false;

            while ((line = bufferedReader.readLine()) != null){
                lineNumber++;

                /** If splitter found, start extracting the links. */
                if(line.equals(LINE_SPLITTER_IN_FILE)){
                    linkNodesFormat = true;
                    continue;
                }

                if(!linkNodesFormat){
                    /** Extract Node data from current line. */
                    Node newNode = extractNodeFromLine(line);
                    /** Add Node to the list */
                    nodes.add(newNode);
                }else{
                    /** Extract link data from current line. */
                    extractLinkFromLine(line, lineNumber);
                }
            }
            bufferedReader.close();
        } catch (IOException | FileFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void extractLinkFromLine(String line, int lineNumber) throws FileFormatException{
        String[] linkData = line.split(" ");

        String nodeName1 = linkData[0];
        String nodeName2 = linkData[1];
        int dist = Integer.parseInt(linkData[2]);

        Node node1 = null, node2 = null;

        /** Search for these two nodes in the node list and save them temporarily. */
        for(Node node: nodes){
            if(node.getName().equals(nodeName1)){
                node1 = node;
            }else if(node.getName().equals(nodeName2)){
                node2 = node;
            }
        }

        /** If either node1 or node2 not found in the list */
        if(node1 == null || node2 == null){
            throw new FileFormatException(line, lineNumber);
        }

        /** node1 connects with node2. */
        node1.addNeighbor(node2, dist);
        node2.addNeighbor(node1, dist);
    }

    private Node extractNodeFromLine(String line){
        String[] nodeData = line.split(" ");
        return new Node(nodeData[0], Integer.parseInt(nodeData[1]));
    }

    /** Retrieve the node list. */
    public List<Node> getNodes(){
        return nodes;
    }

    public void setFilePath(String filePath){
        this.DATASET = filePath;
    }

}
