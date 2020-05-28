package sample;

import Networking.AuthHandler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.io.File;

public class Main extends Application implements EventHandler<ActionEvent> {

    Font titleFont = new Font("Times New Roman", 26);
    Font otherFont = new Font("Times New Roman", 18);

    /**
     * User passed parameters
     */
    String[] artistArr;
    String[] trackArr;

    /**
     * User Related Information
     * */
    static JSONObject userCreds;

    /**
     * User Interface Elements
     * */
    GridPane grid;

    VBox artistVBox;
    HBox artistHBox;

    VBox trackVBox;
    HBox trackHBox;

    int artistChildren = 1;
    int trackChildren = 1;

    Label title;

    TextField artists;
    TextField tracks;

    Button addArtist;
    Button deleteArtist;

    Button addTrack;
    Button deleteTrack;

    Button submit;

    Button loginUser;

    Scene mainScene;

    /**
     * Login User Elements
     * */
    GridPane loginGrid;

    Scene loginScene;

    VBox loginVBox;
    HBox loginHBox;

    Label loginLabel;

    TextField codeText;

    Button getCode;
    Button makeRequest;

    /**
     * Spotify User Elements
     * */
    String redirect_url = "https://msync.azurewebsites.net/";
    String scope = "playlist-modify-public";
    String response_type = "code";
    String client_id = "0cf021fa46ca49689464ba5d5d44746b";

    public static void loadUserCreds(){

        Object rawObj;

        try{

            rawObj = new JSONParser().parse(new FileReader("user_credentials.json"));
            userCreds = (JSONObject) rawObj;

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public static void saveUserCreds(){

        try{

            PrintWriter pw = new PrintWriter("user_credentials.json");
            pw.write(userCreds.toJSONString());

            pw.flush();
            pw.close();

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public static void verifyUserCreds(){

        //If userCreds already exists, load it.
        //Otherwise, create a new userCreds object and default populate it.
        File userFile = new File("./user_credentials.json");

        if(userFile.exists()){

            System.out.println("File exists");
            loadUserCreds();

        }else{

            userCreds = new JSONObject();
            userCreds.put("code", "");
            userCreds.put("token", "");
            userCreds.put("refresh_token", "");
            saveUserCreds();

        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        /**
         * Login Scene
         * */
        loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(10, 10, 10, 10));
        loginGrid.setVgap(2);
        loginGrid.setHgap(2);
        loginGrid.setStyle("-fx-background-color: #00b89c");
        //loginGrid.setGridLinesVisible(true);

        loginVBox = new VBox(20);
        loginGrid.setConstraints(loginVBox, 175, 25);

        loginHBox = new HBox(2);

        loginLabel = new Label("Login");
        loginLabel.setFont(titleFont);
        loginLabel.setTextFill(Color.WHITE);

        codeText = new TextField();
        codeText.setPromptText("Code");
        codeText.setText((String) userCreds.get("code"));
        codeText.setFont(otherFont);
        codeText.setPrefWidth(400);

        getCode = new Button("Get Code");
        getCode.setOnAction(this);
        getCode.setFont(otherFont);

        makeRequest = new Button("Go Back");
        makeRequest.setOnAction(e -> primaryStage.setScene(mainScene));
        makeRequest.setFont(otherFont);

        loginHBox.getChildren().addAll(getCode, makeRequest);
        loginVBox.getChildren().addAll(loginLabel, codeText, loginHBox);

        loginGrid.getChildren().addAll(loginVBox);

        loginScene = new Scene(loginGrid, 1120, 600);

        /**
         * User Interface Scene
         * */
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(2);
        grid.setHgap(2);
        grid.setStyle("-fx-background-color: #00b89c");
        //grid.setGridLinesVisible(true);

        artistVBox = new VBox(2);
        grid.setConstraints(artistVBox, 105, 2);

        artistHBox = new HBox(2);
        grid.setConstraints(artistHBox, 105, 1);

        trackVBox = new VBox(2);
        grid.setConstraints(trackVBox, 109, 2);

        trackHBox = new HBox(2);
        grid.setConstraints(trackHBox, 109, 1);

        title = new Label("Msync");
        grid.setConstraints(title, 107, 0);
        title.setFont(titleFont);
        title.setStyle("-fx-color: white");
        title.setTextFill(Color.WHITE);

        artists = new TextField();
        artists.setPromptText("Artist");
        artists.setFont(otherFont);

        tracks = new TextField();
        tracks.setPromptText("Track");
        tracks.setFont(otherFont);

        addArtist = new Button("Add Artist");
        addArtist.setFont(otherFont);
        addArtist.setOnAction(this);

        deleteArtist = new Button("Remove Artist");
        deleteArtist.setFont(otherFont);
        deleteArtist.setOnAction(this);

        addTrack = new Button("Add Track");
        addTrack.setFont(otherFont);
        addTrack.setOnAction(this);

        deleteTrack = new Button("Remove Track");
        deleteTrack.setFont(otherFont);
        deleteTrack.setOnAction(this);

        submit = new Button("Submit");
        submit.setFont(otherFont);
        submit.setOnAction(this);
        grid.setConstraints(submit, 106, 3);

        loginUser = new Button("Login");
        loginUser.setFont(otherFont);
        loginUser.setOnAction(e -> primaryStage.setScene(loginScene));
        grid.setConstraints(loginUser, 108, 3);

        artistVBox.getChildren().addAll(artists);
        artistHBox.getChildren().addAll(addArtist, deleteArtist);

        trackVBox.getChildren().addAll(tracks);
        trackHBox.getChildren().addAll(addTrack, deleteTrack);

        grid.getChildren().addAll(title, submit, loginUser, artistVBox, artistHBox, trackVBox, trackHBox);

        mainScene = new Scene(grid, 1120, 600);
        primaryStage.setMinWidth(1120);
        primaryStage.setMaxWidth(1120);

        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

    }


    public static void main(String[] args) {
        /**
         * User Credential Handling
         * */
        verifyUserCreds();

        launch(args);
    }

    private void closeWindowEvent(WindowEvent event){

        System.out.println("Window Closing");
        if(!codeText.getText().trim().isEmpty()){

            userCreds.put("code", codeText.getText());

        }

        saveUserCreds();

        System.exit(1);

    }

    @Override
    public void handle(ActionEvent event) {

        Object source = event.getSource();
        if(source == getCode){

            URI url;
            try{

                url = new URI("https://accounts.spotify.com/authorize" + "?" + "client_id=" + client_id + "&response_type=" + response_type + "&redirect_uri=" + redirect_url + "&scope=" + scope);
                java.awt.Desktop.getDesktop().browse(url);

            }catch (Exception e){

                e.printStackTrace();

            }

        }

        if(source == submit){

            artistArr = new String[artistVBox.getChildren().size()];

            int i = 0;
            for(var node : artistVBox.getChildren()){

                TextField inputField;
                try{

                    inputField = (TextField) node;
                    if(!(inputField.getText().isEmpty())){

                        artistArr[i++] = inputField.getText();
                        System.out.println(inputField.getText());

                    }

                }catch (Exception e){

                    //Input is not textfield, continue

                }

            }

            trackArr = new String[trackVBox.getChildren().size()];

            i = 0;
            for(var node : trackVBox.getChildren()){

                TextField inputField;
                try{

                    inputField = (TextField) node;
                    if(!(inputField.getText().isEmpty())){

                        trackArr[i++] = inputField.getText();
                        System.out.println(inputField.getText());

                    }

                }catch (Exception e){

                    //Input is not textfield, continue

                }

            }

            userCreds.put("code", codeText.getText());
            saveUserCreds();

            //AuthenticationHandler sendRequest = new AuthenticationHandler(artistArr, trackArr);
            AuthHandler request = new AuthHandler(userCreds, artistArr, trackArr);

            System.out.println(request.makeRequest());
            saveUserCreds();


        }else if(source == addArtist){

            TextField newArtists = new TextField();
            newArtists.setPromptText("Artist");
            newArtists.setFont(otherFont);

            artistVBox.getChildren().add(newArtists);
            artistChildren++;

        }else if(source == deleteArtist){

            if(artistChildren > 1){

                artistChildren--;
                artistVBox.getChildren().remove(artistChildren);

            }

        }else if(source == addTrack){

            TextField newTrack = new TextField();
            newTrack.setPromptText("Track");
            newTrack.setFont(otherFont);
            trackVBox.getChildren().add(newTrack);
            trackChildren++;

        }else if(source == deleteTrack){

            if(trackChildren > 1){

                trackChildren--;
                trackVBox.getChildren().remove(trackChildren);

            }

        }

    }
}
