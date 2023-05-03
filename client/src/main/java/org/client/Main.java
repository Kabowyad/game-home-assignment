package org.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.extern.slf4j.Slf4j;
import org.shared.RegisterKryo;
import org.shared.enums.Move;
import org.shared.request.InitializeGameRequest;
import org.shared.request.MoveRequest;
import org.shared.request.SigninRequest;
import org.shared.request.SignupRequest;
import org.shared.request.TimeLeftResponse;
import org.shared.response.FailedSignInResponse;
import org.shared.response.MoveResponse;
import org.shared.response.SwitchToGameResponse;
import org.shared.response.SwitchToMenuResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import static org.client.Main.State.END;
import static org.client.Main.State.MENU;
import static org.client.Main.State.MOVE;
import static org.client.Main.State.SIGNUP_SIGNIN;

@Slf4j
public class Main {

    private static State currentState = SIGNUP_SIGNIN;
    private static Timer moveTimer;

    enum State {
        SIGNUP_SIGNIN,
        MENU,
        MOVE,
        END
    }

    public static void main(String[] args) throws IOException {

        Client client = createClient();
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            String input = reader.readLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            processCommand(client, input);
        }
    }

    private static void processCommand(Client client, String input) throws IOException {

        switch (currentState) {

            case SIGNUP_SIGNIN -> {

                if (input.startsWith("signup=")) {
                    processSignUp(client, input);
                } else if (input.startsWith("signin=")) {
                    processSignIn(client, input);
                } else {
                    log.info("Unknown command.");
                    System.out.print("> ");
                }
            }

            case MENU -> {

                if (input.equalsIgnoreCase("logout")) {
                    client.close();
                } else if (input.equalsIgnoreCase("start")) {
                    InitializeGameRequest request = new InitializeGameRequest();
                    sendRequest(request, client);
                    currentState = MOVE;
                } else {
                    log.info("Unknown command.");
                    System.out.print("> ");
                }
            }

            case MOVE -> {
                if (input.equalsIgnoreCase("rock") ||
                        input.equalsIgnoreCase("paper") ||
                        input.equalsIgnoreCase("scissors")) {
                    MoveRequest request = new MoveRequest();
                    switch (input) {
                        case "rock" -> request.setMove(Move.ROCK);
                        case "paper" -> request.setMove(Move.PAPER);
                        case "scissors" -> request.setMove(Move.SCISSORS);
                    }
                    sendRequest(request, client);
                } else if (input.equalsIgnoreCase("logout")) {
                    client.close();
                } else {
                    log.info("Unknown command.");
                    System.out.print("> ");
                }
            }

            case END -> {
                if (input.equalsIgnoreCase("logout")) {
                    client.close();
                }
            }

            default -> log.info("Unknown state.");
        }
    }

    private static void processSignIn(Client client, String input) {
        String[] parts = input.split("=");
        if (parts.length == 3) {
            String command = parts[0];
            String login = parts[1];
            String password = parts[2];

            if (command.equals("signin")) {
                SigninRequest request = new SigninRequest(login, password);
                sendRequest(request, client);
            }
        } else {
            log.info("Invalid command format.");
            System.out.print("> ");
        }

    }

    private static void processSignUp(Client client, String input) {
        String[] parts = input.split("=");
        if (parts.length == 3) {
            String command = parts[0];
            String login = parts[1];
            String password = parts[2];

            if (command.equals("signup")) {
                SignupRequest request = new SignupRequest(login, password);
                sendRequest(request, client);
            }
        } else {
            log.info("Invalid command format.");
            System.out.print("> ");
        }
    }

    private static Client createClient() {

        Client client = new Client();
        Kryo kryo = client.getKryo();
        RegisterKryo.registerClasses(kryo);

        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {

                if (object instanceof SwitchToMenuResponse) {
                    currentState = MENU;
                }

                if (object instanceof SwitchToGameResponse) {
                    currentState = MOVE;
                    log.info("Игра продолжается. Делайте ход");
                    System.out.print("> ");
                }

                if (object instanceof MoveResponse response) {

                    if (response.getGameResult() != null) {
                        currentState = END;
                        log.info("Игра закончилась, ваш результат {}", response.getGameResult());
                        log.info("Через 5 секунд вы попадете на этап Menu");
                        returnToMenu();
                        System.out.print("> ");
                    } else {
                        log.info(response.getMessage());
                        System.out.print("> ");
                    }
                }

                if (object instanceof FailedSignInResponse) {
                    log.info("Invalid credentials");
                    System.out.print("> ");
                }

                if (object instanceof TimeLeftResponse response) {
                    log.info(response.getMessage());
                }
            }
        });

        return client;
    }

    private static void returnToMenu() {
        Timer endTimer = new Timer();
        endTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentState = MENU;
                log.info("Вы вернулись на этап Menu.");
                System.out.print("> ");
            }
        }, 5000);
    }

    private static void sendRequest(Object request, Client client) {
        log.info("Sending: {}, clientId: {}", request.getClass(), client.getID());
        client.sendTCP(request);
    }

}
