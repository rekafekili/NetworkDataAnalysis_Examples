package client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;

// Timer 설정해서 반응이 안오면 에러 처리!
public class IctTalkClient extends Application implements ServerListener {
    private String message;
    private BufferedReader bufferedReader;
    private UdpClientManager udpClientManager;
    private static final String SERVER_IP = "220.68.65.43";
    private static final int SERVER_PORT = 3000;

    @Override
    public void start(Stage primaryStage) throws Exception {
        udpClientManager = new UdpClientManager(SERVER_IP, SERVER_PORT);
        primaryStage.setTitle("ICT Talk");

        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(loginGrid, 300, 275);
        primaryStage.setScene(scene);

        // 타이틀 추가
        Text sceneTitleText = new Text("ICT Talk");
        sceneTitleText.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        loginGrid.add(sceneTitleText, 0, 0, 2, 1);

        // ID 라벨과 입력창 추가
        Label userIDLabel = new Label("ID");
        loginGrid.add(userIDLabel, 0, 1);
        TextField userIDTextField = new TextField();
        loginGrid.add(userIDTextField, 1, 1);

        // Password 라벨과 입력창 추가
        Label userPasswordLabel = new Label("Password");
        loginGrid.add(userPasswordLabel, 0, 2);
        PasswordField userPasswordField = new PasswordField();
        loginGrid.add(userPasswordField, 1, 2);

        // 로그인 버튼 추가
        Button signInButton = new Button("Sign In");
        signInButton.setOnAction(event -> {
            StackPane stackPane = new StackPane();
            Scene scene1 = new Scene(stackPane, 500, 400);
            primaryStage.setScene(scene1);
        });

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(event -> {
            Stage signUpStage = createRegisterStage();

            signUpStage.show();
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(signUpButton);
        hBox.getChildren().add(signInButton);
        loginGrid.add(hBox, 1, 4);

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Stage createRegisterStage() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up to ICT Talk");
        signUpStage.setResizable(false);

        GridPane signUpGrid = new GridPane();
        signUpGrid.setAlignment(Pos.CENTER);
        signUpGrid.setHgap(10);
        signUpGrid.setVgap(20);
        Scene signUpScene = new Scene(signUpGrid, 300, 300);
        signUpStage.setScene(signUpScene);

        Text welcomeText = new Text("Welcome to ICT Talk!");
        welcomeText.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
        signUpGrid.add(welcomeText, 0, 0, 2, 1);

//            // 학번 입력 칸
//            Label studentCodeLabel = new Label("Student Code");
//            signUpGrid.add(studentCodeLabel, 0, 1);
//            TextField studentCodeTextField = new TextField();
//            signUpGrid.add(studentCodeTextField, 1, 1);

        // ID 입력 칸
        Label idLabel = new Label("ID");
        signUpGrid.add(idLabel, 0, 1);
        TextField idTextField = new TextField();
        signUpGrid.add(idTextField, 1, 1);

        // Password 입력 칸
        Label passwordLabel = new Label("Password");
        signUpGrid.add(passwordLabel, 0, 2);
        PasswordField passwordTextField = new PasswordField();
        signUpGrid.add(passwordTextField, 1, 2);

        // Register 버튼
        Button registerButton = new Button("Register!");
        registerButton.setOnAction(e -> {
//                String studentCode = studentCodeTextField.getText();
            String userId = idTextField.getText();
            String userPassword = passwordTextField.getText();

            udpClientManager.sendMessage("register/" + "," + userId + "," + userPassword);
        });

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(registerButton);
        signUpGrid.add(hbox, 1, 3);
        return signUpStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void getServerMessage(String message) {

    }
}
