package client;

import java.io.BufferedReader;

// Timer 설정해서 반응이 안오면 에러 처리!
public class IctTalkClient {
    private String message;
    private BufferedReader bufferedReader;
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        LoginView.launch(args);
    }
}
