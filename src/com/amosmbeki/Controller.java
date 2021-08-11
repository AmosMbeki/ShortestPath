package com.amosmbeki;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable, ChangeListener {
    @FXML
    private JFXButton initializeBtn, startAStarBtn, startBfsBtn;
    @FXML
    private JFXComboBox<String> nodeListComboBox;
    @FXML
    private JFXSlider delaySlideBar, graphFontSizeSlideBar;

    private ExecutorService threadPool = Executors.newWorkStealingPool();
    private PathFinder pathFinder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        delaySlideBar.valueProperty().addListener(this::changed);
        graphFontSizeSlideBar.valueProperty().addListener(this::changed);
        disableButtons(true);
    }

    public void initializeGraphPane(){
        initializeBtn.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Select the file containing the graph data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile != null){
            String filePath = selectedFile.getPath();
            DataParser.DATASET = filePath;
        }else{
            initializeBtn.setDisable(false);
            return;
        }
        try {
            /** Enables multi-threading */
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder = new PathFinder();
                    /** Enable Slider */
                    delaySlideBar.setDisable(false);
                    graphFontSizeSlideBar.setDisable(false);
                    /** Initialize graph and get all nodes from it */
                    List<Node> nodeList = pathFinder.initializeGraph();
                    /** Initialize comboBox values */
                    setComboBoxValues(nodeList);
                    /** Enable buttons after graph is initialized */
                    disableButtons(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startAStarAlgorithm(){
        String startNode = nodeListComboBox.getValue();

        /** If start is not selected, do nothing. */
        if (startNode == null)
            return;

        /** Disable buttons. */
        disableButtons(true);

        try {
            /** Enables multi-threading. */
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder.runAStar(startNode);
                    disableButtons(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startBreadthFirstAlgorithm(){
        String startNode = nodeListComboBox.getValue();

        /** If start node is not selected, do nothing. */
        if (startNode == null)
            return;

        /** Disable buttons */
        disableButtons(true);

        try {
            /** Enables multi-threading. */
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder.runBreadthFirst(startNode);
                    disableButtons(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Add all node names to comboBox */
    private void setComboBoxValues(List<Node> nodes){
        for (Node node: nodes){
            nodeListComboBox.getItems().add(node.getName());
        }
    }

    /** Set button status */
    private void disableButtons(boolean status){
        startAStarBtn.setDisable(status);
        startBfsBtn.setDisable(status);
    }


    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        int delay = (int) delaySlideBar.getValue();
        pathFinder.setVisualizationDelay(delay);

        int fontSize = (int) graphFontSizeSlideBar.getValue();
        pathFinder.setGraphFontSize(fontSize);
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
