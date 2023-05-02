package org.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.extern.slf4j.Slf4j;
import org.shared.FailedSignIn;
import org.shared.InitializeGameRequest;
import org.shared.Move;
import org.shared.MoveRequest;
import org.shared.RegisterKryo;
import org.shared.SigninRequest;
import org.shared.SignupRequest;
import org.shared.SignupResponse;
import org.shared.SwitchToGameResponse;
import org.shared.SwitchToMenuResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.client.Main.State.GAME_STEP_1;
import static org.client.Main.State.GAME_STEP_2;
import static org.client.Main.State.GAME_STEP_3;
import static org.client.Main.State.MENU;
import static org.client.Main.State.SIGNUP_SIGNIN;

@Slf4j
public class Main {

    private static Long playerId = null;

    private static State currentState = SIGNUP_SIGNIN;

    enum State {
        SIGNUP_SIGNIN,
        MENU,
        GAME_STEP_1,
        GAME_STEP_2,
        GAME_STEP_3
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
                if (input.startsWith("signup=") || input.startsWith("signin=")) {
                    processSignupOrSignin(client, input);
                } else {
                    System.out.println("Unknown command.");
                }
            }

            case MENU -> {
                if (input.equalsIgnoreCase("logout")) {
                    client.close();
                } else if (input.equalsIgnoreCase("start")) {
                    // Инициализируем первую игру
                    InitializeGameRequest request = new InitializeGameRequest();
                    request.playerId = playerId;
                    sendRequest(request, client);
                    System.out.println("You are now in GAME STEP 1. Choose you pick.");
                    currentState = GAME_STEP_1;
                } else {
                    System.out.println("Unknown command.");
                }
            }

            case GAME_STEP_1, GAME_STEP_2, GAME_STEP_3 -> {
                if (input.equalsIgnoreCase("rock") || input.equalsIgnoreCase(
                        "paper") || input.equalsIgnoreCase("scissors")) {
                    MoveRequest request = new MoveRequest();
                    request.playerId = playerId;
                    switch (input) {
                        case "rock" -> request.move = Move.ROCK;
                        case "paper" -> request.move = Move.PAPER;
                        case "scissors" -> request.move = Move.SCISSORS;
                    }
                    sendRequest(request, client);
                } if (input.equalsIgnoreCase("logout")) {
                    // TODO отключиться от сервера
                } else {
                    System.out.println("Unknown command.");
                }
            }
            default -> System.out.println("Unknown state.");
        }
    }

    // TODO разнести на две разные функции
    private static void processSignupOrSignin(Client client, String input) {
        String[] parts = input.split("=");
        if (parts.length == 3) {
            String command = parts[0];
            String login = parts[1];
            String password = parts[2];
            System.out.println(command + "-" + login + "-" + password);
            if (command.equals("signup")) {
                SignupRequest request = new SignupRequest(login, password);
                sendRequest(request, client);
            }

            if (command.equals("signin")) {
                SigninRequest request = new SigninRequest(login, password);
                sendRequest(request, client);
            }

        } else {
            System.out.println("Invalid command format.");
        }
    }

    private static void sendRequest(Object request, Client client) {
        log.info("Sending: {}, clientId: {}", request.getClass(), client.getID());
        client.sendTCP(request);
    }

    private static Client createClient() {

        Client client = new Client();
        Kryo kryo = client.getKryo();
        RegisterKryo.registerClasses(kryo);
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof SwitchToMenuResponse response) {
                    playerId = response.clientId;
                    currentState = MENU;
                }

                if (object instanceof SwitchToGameResponse response) {
                    playerId = response.clientId;
                    switch (response.gameStep) {
                        case "GAME_STEP_1" -> currentState = GAME_STEP_1;
                        case "GAME_STEP_2" -> currentState = GAME_STEP_2;
                        case "GAME_STEP_3" -> currentState = GAME_STEP_3;
                    }
                }

                if (object instanceof FailedSignIn) {
                    System.out.println("Invalid credentials");
                }

                if (object instanceof SignupResponse) {
                    System.out.println("SignupResponse from server");
                }
            }
        });

        return client;
    }
}
