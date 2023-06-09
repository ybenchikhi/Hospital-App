package edu.wpi.tacticaltritons.controllers.signage;

import edu.wpi.tacticaltritons.App;
import edu.wpi.tacticaltritons.database.DAOFacade;
import edu.wpi.tacticaltritons.database.Signage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import edu.wpi.tacticaltritons.styling.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SignagePageController {
    @FXML private GridPane basePane;

    @FXML private FlowPane signageForwardBlock;
    @FXML private VBox signageForwardLocations;
    @FXML private Rectangle signageForwardSeparator;

    @FXML private FlowPane signageLeftBlock;
    @FXML private VBox signageLeftLocations;
    @FXML private Rectangle signageLeftSeparator;

    @FXML private FlowPane signageRightBlock;
    @FXML private VBox signageRightLocations;
    @FXML private Rectangle signageRightSeparator;

    @FXML private FlowPane signageBackBlock;
    @FXML private VBox signageBackLocations;
    @FXML private Rectangle signageBackSeparator;

    private double seperatorRatio = 0.6;
    private double arrowIconSize = 400;
    private int fontSize = 70;
    private double referenceWidth = 3840;
    VBox[] signageLocationBlocks;

    ArrayList<Label> locationLabels;
    public void initialize() throws SQLException {
        if(SignagePageInteraction.firstDisplay){
            List<Signage> signageList = DAOFacade.getAllSignage();
            SignagePageInteraction.forwardLocations = signageList.get(0).getForwarddir();
            SignagePageInteraction.leftLocations = signageList.get(0).getLeftdir();
            SignagePageInteraction.rightLocations = signageList.get(0).getRightdir();
            SignagePageInteraction.backLocations = signageList.get(0).getBackdir();
            SignagePageInteraction.singleDisplay = signageList.get(0).isSingleDisplay();
            SignagePageInteraction.firstDisplay = false;
        }
        locationLabels = new ArrayList<>();
        signageLocationBlocks = new VBox[]{signageForwardLocations,signageLeftLocations,signageRightLocations,signageBackLocations};
        horizontalResizing(App.getPrimaryStage().getWidth());
        generatePage();
        EffectGenerator.generateShadowEffect(basePane);
        EffectGenerator.generateSpacing(basePane,20);
        setResize();
        resizeTexts(App.getPrimaryStage().getWidth());
    }

    private void generatePage(){
        loadLocation(signageForwardLocations, SignagePageInteraction.forwardLocations); // forward direction block is the default display area
        if(SignagePageInteraction.singleDisplay){
            formatAsSingleDisplay();
        }else{
            loadLocation(signageLeftLocations, SignagePageInteraction.leftLocations);
            loadLocation(signageRightLocations, SignagePageInteraction.rightLocations);
            loadLocation(signageBackLocations, SignagePageInteraction.backLocations);
            removeEmpty();
        }
    }
    // load all locations
    private void loadLocation(VBox target,String[] source){
        for(String location: source){
            Label locationLabel= new Label(location);
            locationLabel.setStyle("-fx-font-size: " + fontSize);
            target.getChildren().add(locationLabel);
            locationLabels.add(locationLabel);
        }
    }
    // if assign as single display, reformat the block as a single display block
    private void formatAsSingleDisplay(){
        Label singleDisplayTitle = new Label("Stop Here For");
        fontSize *= 3;
        singleDisplayTitle.setStyle("-fx-text-fill: " + ThemeColors.YELLOW.getColor() + ";" + "-fx-font-size: " + (fontSize + 10));
        for(int i = 0; i < signageForwardLocations.getChildren().size();i++){
            signageForwardLocations.getChildren().get(i).setStyle("-fx-font-size: " + fontSize);
        }
        signageForwardLocations.getChildren().add(0,singleDisplayTitle);
        signageForwardBlock.getChildren().remove(0,2); // remove arrow and seperator
        basePane.getChildren().remove(1,4); // remove the rest four blocks
        signageForwardBlock.setAlignment(Pos.CENTER); // make text center
    }
    //remove unfilled blocks
    private void removeEmpty(){
        for(int i = 0,j = 0; i < basePane.getChildren().size() && j < signageLocationBlocks.length; i++,j++){
            if(signageLocationBlocks[j].getChildren().isEmpty()){
                basePane.getChildren().remove(i);
                i--;
            }
        }
        // have enough space for big font size under 4K resolution
        if(locationLabels.size() < 13){
            fontSize *= 2;
            setFontSize(fontSize);
        }
    }
    // set the on actions
    private void setResize(){
        App.getPrimaryStage().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.doubleValue() > 600){
                    horizontalResizing(newValue);
                    resizeTexts(newValue);
                }
            }
        });
        signageForwardBlock.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                signageForwardSeparator.setHeight(newValue.doubleValue() * seperatorRatio);
            }
        });
        signageLeftBlock.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                signageLeftSeparator.setHeight(newValue.doubleValue() * seperatorRatio);
            }
        });
        signageRightBlock.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                signageRightSeparator.setHeight(newValue.doubleValue() * seperatorRatio);
            }
        });
        signageBackBlock.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                signageBackSeparator.setHeight(newValue.doubleValue() * seperatorRatio);
            }
        });
    }
    private void setFontSize(double fontSize){
        for(Label locaionLabel: locationLabels){
            locaionLabel.setStyle("-fx-font-size: " + fontSize);
        }
        if(SignagePageInteraction.singleDisplay){
            signageForwardLocations.getChildren().get(0).setStyle("-fx-text-fill: " + ThemeColors.YELLOW.getColor() + ";" + "-fx-font-size: " + (fontSize + 10));
        }
    }
    // do an initial resizing for fit the screen
    private void horizontalResizing(Number newValue){
        basePane.setPrefWidth(newValue.doubleValue());
        for(Node block: basePane.getChildren()){
            FlowPane pane = (FlowPane) block;
            pane.setPrefWidth(newValue.doubleValue());
            for(Node item: pane.getChildren()){
                if(item instanceof VBox box){
                    box.setPrefWidth(newValue.doubleValue() - arrowIconSize);
                }
            }
        }
    }
    // resize the text to fit the screen size
    private void resizeTexts(Number newValue){
        double newFontSize = newValue.doubleValue() / referenceWidth * fontSize;
        setFontSize(newFontSize);
    }
}
