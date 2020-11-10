package client;

import javafx.application.Application;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;

// TODO: 게시판에 올려진 예제를 JavaFx 코드로 바꾸기
// TODO: JSON 통신 예제 만들기
public class IctTalkClient extends Application implements ServerListener {
    private UdpClientManager mUdpClientManager;
    private static final String SERVER_IP = "220.68.65.43";
    private static final int SERVER_PORT = 3000;
    private Stage mSignUpStage;
    private Text mActionText;
    private Stage mPrimaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mUdpClientManager = new UdpClientManager(SERVER_IP, SERVER_PORT);
        mUdpClientManager.setServerListener(this);
        mPrimaryStage = primaryStage;
        mPrimaryStage.setTitle("ICT Talk");

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
            mUdpClientManager.sendMessage("signin/" + userIDTextField.getText() + "," + userPasswordField.getText());
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

        Text actionText = new Text("HI!");
        loginGrid.add(actionText, 1, 5);

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Stage createRegisterStage() {
        mSignUpStage = new Stage();
        mSignUpStage.setTitle("Sign Up to ICT Talk");
        mSignUpStage.setResizable(false);

        GridPane signUpGrid = new GridPane();
        signUpGrid.setAlignment(Pos.CENTER);
        signUpGrid.setHgap(10);
        signUpGrid.setVgap(20);
        Scene signUpScene = new Scene(signUpGrid, 300, 300);
        mSignUpStage.setScene(signUpScene);

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

            mUdpClientManager.sendMessage("register/" + userId + "," + userPassword);
        });

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(registerButton);
        signUpGrid.add(hbox, 1, 3);

        mActionText = new Text("Welcome!");
        signUpGrid.add(mActionText, 1, 5);
        return mSignUpStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void getServerMessage(String serverMessage) {
        System.out.println("Server : " + serverMessage);
        String result = serverMessage.split("/")[0];
        String data = serverMessage.split("/")[1];

        switch(result) {
            case "OK":
                mSignUpStage.close();
                break;
            case "ERROR":
                mActionText.setFill(Color.FIREBRICK);
                mActionText.setText(data);
                break;
            case "SOK":
                StackPane stackPane = new StackPane();
                Text text = new Text("Your Imagine!");
                stackPane.getChildren().add(text);

                Scene scene1 = new Scene(stackPane, 500, 400);
                mPrimaryStage.setScene(scene1);
                break;
        }
    }
}
