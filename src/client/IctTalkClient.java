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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;

// Timer 설정해서 반응이 안오면 에러 처리!
public class IctTalkClient extends Application {
    private String message;
    private BufferedReader bufferedReader;
    private static final int SERVER_PORT = 3000;

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        signInButton.setOnAction(e -> {
            StackPane stackPane = new StackPane();
            Scene scene1 = new Scene(stackPane, 500, 400);
            primaryStage.setScene(scene1);
        });

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            Stage signUpStage = new Stage();
            signUpStage.setResizable(false);
            GridPane signUpGrid = new GridPane();
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

    public static void main(String[] args) { launch(args); }
}
