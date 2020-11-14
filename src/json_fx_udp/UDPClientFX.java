package json_fx_udp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * 2020 네트워크 데이터 분석
 * 2014161141 조성윤
 * JSON과 JavaFX를 활용한 UDP 통신
 */
public class UDPClientFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UDP 채팅 2");
        primaryStage.setResizable(false);

        VBox vBox = new VBox();

        HBox topBox = createTopBox();
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        vBox.getChildren().add(topBox);
        vBox.getChildren().add(textArea);

        Scene scene = new Scene(vBox, 920, 460);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTopBox() {
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(10));

        Label destIPLabel = new Label("Dest IP :");
        topBox.getChildren().add(destIPLabel);
        destIPLabel.setFont(Font.font("Tahoma", 20));
        TextField destIpTF = new TextField();
        topBox.getChildren().add(destIpTF);

        Label destPortLabel = new Label("Dest Port :");
        topBox.getChildren().add(destPortLabel);
        destPortLabel.setFont(Font.font("Tahoma", 20));
        TextField destPortTF = new TextField();
        topBox.getChildren().add(destPortTF);

        Label srcPortLabel = new Label("Src Port :");
        topBox.getChildren().add(srcPortLabel);
        srcPortLabel.setFont(Font.font("Tahoma", 20));
        TextField srcPortTF = new TextField();
        topBox.getChildren().add(srcPortTF);

        Button setButton = new Button("SET");
        setButton.setFont(Font.font(15));
        setButton.setOnAction((event) -> setIpPort(destIpTF.getText(), destPortTF.getText()));
        topBox.getChildren().add(setButton);

        topBox.setAlignment(Pos.CENTER);
        return topBox;
    }

    private void setIpPort(String destIP, String destPort) {

    }
}
