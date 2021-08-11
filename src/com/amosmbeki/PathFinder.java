package com.amosmbeki;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;


public class PathFinder {
    /** Store all the nodes */
    private static List<Node> nodes;
    private static Graph graph;
    /** Parser used to parse the file with the graph data. */
    private static DataParser parser;
    /** Sets visualization delay. */
    private long DELAY = 1500;

    public List<Node> initializeGraph(){
        setupNodes();

        /** Set up graph and its properties. */
        graph = new SingleGraph("PathFinder");

        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.display();

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('com/amosmbeki/resources/style.css')");

        /** Set up graph nodes */
        setupGraphNodes();

        return nodes;
    }

    private static void setupNodes(){
        /** Start the parsing process. */
        parser = new DataParser();

        /** Retrieve the node list. */
        nodes = parser.getNodes();

    }

    public void runAStar(String nodeName){
        /** Reset all node colors. */
        clearNodeColors();
        /** Get Selected node. */
        Node startNode = getNode(nodeName);
        /** Reset source field, if exists(avoid errors). */
        resetNode(startNode);
        /** Goal == Nairobi */
        AStarSearch(startNode, getGoalNode());
    }

    public void runBreadthFirst(String nodeName){
        /** Reset all node colors. */
        clearNodeColors();
        /** Get Selected Node */
        Node startNode = getNode(nodeName);
        /** Reset source field, if exists(avoid errors). */
        resetNode(startNode);
        /** Goal == Nairobi */
        BreadthFirstSearch(startNode, getGoalNode());

    }

    public void setupGraphNodes(){
        for(Node node: nodes){
            /** Create node if doesn't already exist on the graph. */
            if(graph.getNode(node.getName()) == null){
                /** Add node to the graph. */
                org.graphstream.graph.Node graphNode = graph.addNode(node.getName());
                graphNode.addAttribute("ui.label", node.getName() + "	[" + node.getHeuristic() + "]");

                /** Add a little delay. */
                sleep(DELAY);
            }

            /** Iterate through neighbor map. */
            for(Map.Entry<Node, Integer> entry: node.getNeighbors().entrySet()){
                Node neighbor = entry.getKey();
                Integer cost = entry.getValue();

                /** Create neighbour and its edge if it doesn't already exist. */
                if(graph.getNode(neighbor.getName()) == null){
                    org.graphstream.graph.Node graphNeighbor = graph.addNode(neighbor.getName());
                    graphNeighbor.addAttribute("ui.label", neighbor.getName() + "	[" + neighbor.getHeuristic() + "]");

                    /** Create edge id. */
                    StringBuilder edgeName = new StringBuilder();
                    edgeName.append(node.getName());
                    edgeName.append(neighbor.getName());

                    /** Add edge to the graph. */
                    Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
                    edge.addAttribute("ui.label", cost.toString());

                    /** Add a little delay. */
                    sleep(DELAY);
                }else{
                    /** Create edge between current node and its neighbor if not found. */
                    if(graph.getNode(node.getName()).getEdgeBetween(neighbor.getName()) == null){
                        /** Create edge id. */
                        StringBuilder edgeName = new StringBuilder();
                        edgeName.append(node.getName());
                        edgeName.append(neighbor.getName());

                        /** Add edge to the graph. */
                        Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
                        edge.addAttribute("ui.label", cost.toString());

                        /** Add a little delay. */
                        sleep(DELAY);
                    }
                }
            }
        }
    }

    /** ALGORITHMS */
    private void AStarSearch(Node start, Node goal) {
        int NODES_EXPANDED = 0;
        /** Works as a priority queue. First index: node with minimum total score. */
        Map <Node, Integer> nodeQueue = new HashMap<>();
        /** Keeps track of nodes which are expanded. */
        List<Node> expanded = new ArrayList<>();

        /** Initialize start node total score. */
        int startScore = start.getHeuristic() + 0;
        nodeQueue.put(start, startScore);
        /** Current keeps track of the node that is currently being processed. */
        Node current = start;

        /** Sets a color to start node. */
        graph.getNode(current.getName()).addAttribute("ui.class", "start");
        sleep(DELAY);

        /** While current node is not the goal node. */
        while (!current.getName().equals(goal.getName())){
            /** Iterate through the neighbors of the current node. */
            for (Node neighbor : current.getNeighbors().keySet()){
                /** If this neighbor is already expanded, ignore it. */
                if (expanded.contains(neighbor)){
                    continue;
                }

                /** Color neighbor different node, unless it is the start node. */
                if (!start.getName().equals(neighbor.getName())){
                    graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
                    /** Add a little delay */
                    sleep(DELAY);
                }

                /** If the queue contains the neighbor. */
                if (nodeQueue.containsKey(neighbor)){
                    /** Find the total score of the neighbor from the current node */
                    int tempNeighborScore = nodeQueue.get(current)
                            - current.getHeuristic()
                            + current.getNeighbors().get(neighbor)
                            + neighbor.getHeuristic();

                    /** Get neighbor's total score from the queue. */
                    int existedNodeScore = nodeQueue.get(neighbor);

                    /** If the new distance is less than the one in the queue. */
                    if(tempNeighborScore < existedNodeScore){
                        /** Neighbor's new source = current */
                        neighbor.setSource(current);
                        /** And update neighbor's total score in queue. */
                        nodeQueue.put(neighbor, tempNeighborScore);
                    }
                }else {
                    /** Neighbor's source = current. */
                    neighbor.setSource(current);
                    /** Calculate neighbor's total score. */
                    int neighborScore = nodeQueue.get(neighbor.getSource())
                            - neighbor.getSource().getHeuristic()
                            + neighbor.getSource().getNeighbors().get(neighbor)
                            + neighbor.getHeuristic();

                    /** Add the neighbor with its score to the queue-map. */
                    nodeQueue.put(neighbor, neighborScore);
                }
            }

            /** Clear neighbor colors, unless it's the node or already visited node*/
            for(Node node: current.getNeighbors().keySet()){
                if(!node.getName().equals(start.getName()) && !graph.getNode(node.getName()).getAttribute("ui.class").equals("visited")){
                    graph.getNode(node.getName()).removeAttribute("ui.class");
                }
            }

            NODES_EXPANDED++;
            /** Current has been expanded, so add him to the expanded list. */
            expanded.add(current);
            /** We remove from the queue the current node. (Which had the minimum total score). */
            nodeQueue.remove(current);
            /** Draw the expanded nodes, unless it's the start node. */
            if(!start.getName().equals(current.getName())){
                graph.getNode(current.getName()).addAttribute("ui.class", "visited");
            }

            /** Get the node with the minimum total score and make it the current one. */
            current = getMinValue(nodeQueue);

            /** Color the new current, unless it's the start node which remains green. */
            if(!start.getName().equals(current.getName())){
                graph.getNode(current.getName()).addAttribute("ui.class", "current");
                /** Add a little delay. */
                sleep(DELAY);
            }
        }

        /** When goal found, change color. */
        graph.getNode(current.getName()).addAttribute("ui.class", "goal");
        /** Add a little delay. */
        sleep(DELAY);

        /** Used to trace the path back. */
        Node tracker = current;

        System.out.println("NODES EXPANDED DURING A STAR ALGORITHM: " + NODES_EXPANDED);
        System.out.print("Path: " + goal.getName());

        while(tracker.getSource() != null){
            System.out.print(" <-- " + tracker.getSource().getName());
            tracker = tracker.getSource();
            graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
            sleep(DELAY);
        }
        System.out.println();
    }

    private void BreadthFirstSearch(Node start, Node goal) {
        /** Some comments are mutual with AStarSearch algorithm so no repeat */
        int NODES_EXPANDED = 0;
        /** Queue that stores nodes to be expanded. */
        Queue<Node> nodeQueue = new LinkedList<>();
        List<Node> expanded = new ArrayList<>();

        Node current = start;
        graph.getNode(current.getName()).addAttribute("ui.class", "start");
        sleep(DELAY);

        while (!current.getName().equals(goal.getName())){
            for(Node neighbor: current.getNeighbors().keySet()){
                /** If the neighbor is already expanded OR the neighbor is
                 * already in the queue to be expanded in the future, ignore
                 * the neighbor.*/
                if(expanded.contains(neighbor) || nodeQueue.contains(neighbor)){
                    continue;
                }

                neighbor.setSource(current);
                nodeQueue.add(neighbor);

                graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
                sleep(DELAY);
            }

            NODES_EXPANDED++;
            expanded.add(current);
            current = nodeQueue.remove();

            graph.getNode(current.getName()).addAttribute("ui.class", "current");
            sleep(DELAY);
        }
        graph.getNode(current.getName()).addAttribute("ui.class", "goal");
        sleep(DELAY);

        Node tracker = current;

        System.out.println("NODES EXPANDED DURING BFS ALGORITHM: " + NODES_EXPANDED);
        System.out.print("Path: " + goal.getName());

        while (tracker.getSource() != null){
            System.out.print(" <-- " + tracker.getSource().getName());
            tracker = tracker.getSource();

            graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
            sleep(DELAY);
        }
        System.out.println();
    }

    /** Get Min Value  */
    private Node getMinValue(Map<Node, Integer> treeMap){
        Node minNode = null;
        int min = Integer.MAX_VALUE;

        for(Map.Entry<Node, Integer> entry: treeMap.entrySet()){
            Node node = entry.getKey();
            Integer value = entry.getValue();

            if(value < min){
                min = value;
                minNode = node;
            }
        }

        return minNode;
    }

    /** Get specific node from list given its name. */
    private Node getGoalNode(){
        for(Node current: nodes){
            if(current.getHeuristic() == 0){
                return current;
            }
        }
        return null;
    }

    /** Get specific node from list given its name. */
    private Node getNode(String nodeName){
        for(Node node: nodes){
            if(node.getName().equals(nodeName)){
                return node;
            }
        }
        return null;
    }

    private void clearNodeColors(){
        for(org.graphstream.graph.Node node: graph.getNodeSet()){
            node.removeAttribute("ui.class");
        }
    }

    /** Set node's source field null. */
    private void resetNode(Node startNode){
        if(startNode.getSource() != null){
            startNode.setSource(null);
        }
    }

    private void sleep(long ms){
        try {
            Thread.sleep(ms);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setVisualizationDelay(int value){
        this.DELAY = value;
    }

    // Change graph font size
    public void setGraphFontSize(int fontSize){
        for(org.graphstream.graph.Node node: graph.getNodeSet()){
            node.addAttribute("ui.style", "size: " + fontSize + "px;");
            node.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
        }

        for(Edge edge: graph.getEdgeSet()){
            edge.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
        }
    }

    public void setFilePath(){

    }

}
