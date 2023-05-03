package org.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.server.entity.Game;
import org.server.repository.GameRepository;
import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.server.service.GameSessionService;
import org.server.service.SigninService;
import org.server.service.SignupService;
import org.shared.enums.GameStep;
import org.shared.response.FailedSignInResponse;
import org.shared.request.InitializeGameRequest;
import org.shared.enums.Move;
import org.shared.request.MoveRequest;
import org.shared.response.MoveResponse;
import org.shared.request.SigninRequest;
import org.shared.request.SignupRequest;
import org.shared.response.SwitchToGameResponse;
import org.shared.response.SwitchToMenuResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class CustomListener extends Listener {

    private final SignupService signupService;

    private final SigninService signinService;

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    private final GameSessionService gameSessionService;

    // Key: connectionID, Value: playerID
    private final HashMap<Integer, Long> playersConnections = new HashMap<>();

    Move[] moves = { Move.ROCK, Move.PAPER, Move.PAPER };

    public CustomListener(SignupService signupService, SigninService signinService,
            PlayerRepository playerRepository, GameRepository gameRepository,
            GameSessionService gameSessionService) {

        this.signupService = signupService;
        this.signinService = signinService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameSessionService = gameSessionService;
    }

    @Transactional
    @Override
    public void received(Connection connection, Object object) {

        /*
          Processes a SigninRequest object received from a client, authenticates the user, and manages their game state.

          If the SigninRequest is valid, the method will attempt to find the player and their current game, if any.
          The player's connection ID is then mapped to the player's ID in the playersConnections map.

          If the player has an ongoing game, the game session is added to the gameSessionService, and a SwitchToGameResponse
          is sent to the client with the current game step and time left.

          If the player does not have an ongoing game, a SwitchToMenuResponse is sent to the client.

          If the SigninRequest is invalid or an exception occurs during processing, a FailedSignInResponse is sent to the client.
         */
        if (object instanceof SigninRequest) {

            SigninRequest request = (SigninRequest) object;
            log.info("SigninRequest from client: " + request);

            try {
                Player player = signinService.processSignin(request);
                playersConnections.put(connection.getID(), player.getId());
                Game currentGame = findCurrentGame(player.getGames());
                if (currentGame != null) {
                    gameSessionService.addGameToSessions(connection.getID(), currentGame);
                    SwitchToGameResponse response =
                            new SwitchToGameResponse(currentGame.getCurrentStep().name(),
                                    currentGame.getTimeLeft());
                    sendResponse(response, connection);
                } else {
                    SwitchToMenuResponse response = new SwitchToMenuResponse();
                    sendResponse(response, connection);
                }
            } catch (RuntimeException ex) {
                FailedSignInResponse response = new FailedSignInResponse();
                sendResponse(response, connection);
            }

        }

        /*
          Processes a SignupRequest object received from a client and creates a new user account.

          If the SignupRequest is valid, the method will call the signupService's processSignupRequest method
          to create a new user account with the provided login and password.
          Once the user account has been created, the connection to the client is closed.
         */
        if (object instanceof SignupRequest) {

            SignupRequest request = (SignupRequest) object;
            log.info("Received SignupRequest from client-id {}", connection.getID());

            signupService.processSignupRequest(request);
            connection.close();
        }

        /*
          Processes an InitializeGameRequest object received from a client and initializes a new game.

          Upon receiving an InitializeGameRequest, this method retrieves the Player instance associated
          with the current connection ID. It then creates a new Game instance with the Player and connection ID,
          and adds the game to the game sessions through the gameSessionService.
          The newly created game is also saved and flushed to the gameRepository.
         */
        if (object instanceof InitializeGameRequest) {

            Player player =
                    playerRepository.findById(playersConnections.get(connection.getID())).get();
            Game game = new Game(player, connection.getID());
            gameSessionService.addGameToSessions(connection.getID(),
                    gameRepository.saveAndFlush(game));
        }

        if (object instanceof MoveRequest) {

            MoveRequest request = (MoveRequest) object;
            Game game = gameSessionService.getGameByConnectionID(connection.getID());

            Move computerMove = moves[new Random().nextInt(moves.length)];
            val point = game.makeMove(request.getMove(), computerMove);
            game.updateStepGame();
            gameRepository.saveAndFlush(game);

            String result;
            switch (point) {
                case 0:
                    result = "Ничья";
                    break;
                case -1:
                    result = "Очко в пользу компьютера";
                    break;
                default:
                    result = "Очко в пользу человека";
            }

            MoveResponse response = new MoveResponse();
            response.setMessage(
                    String.format("Человек сходил %s, Компьютер сходил %s, %s", request.move,
                            computerMove, result));
            response.setStep(game.getCurrentStep());
            if (game.getCurrentStep().equals(GameStep.END)) {
                response.setGameResult(game.getGameResult().name());
                gameSessionService.deleteFinishedGameFromSessions(connection.getID());
            }

            sendResponse(response, connection);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        // Сохраняем весь стейт по игре в БД
        val gameInProgress = gameSessionService.getGameByConnectionID(connection.getID());
        if (gameInProgress == null || !gameInProgress.isInProgress())
            return;

        val secondsPassed =
                Duration.between(gameInProgress.getTimerStarted(), Instant.now()).getSeconds();
        val secondsRemaining = (int) (30 - secondsPassed);
        if (secondsRemaining > 0) {
            gameInProgress.setTimeLeft(secondsRemaining);
            gameRepository.saveAndFlush(gameInProgress);
            gameSessionService.deleteFinishedGameFromSessions(connection.getID());
        } else {
            gameInProgress.setPlayerPoints(gameInProgress.getPlayerPoints() - 1);
            gameInProgress.updateStepGame();
        }
    }

    private static void sendResponse(Object response, Connection connection) {

        log.info("Sending: {}, clientId: {}", response.getClass(), connection.getID());
        connection.sendTCP(response);
    }

    private Game findCurrentGame(List<Game> games) {

        for (Game game : games) {
            if (game.isInProgress()) {
                return game;
            }
        }
        return null;
    }
}
