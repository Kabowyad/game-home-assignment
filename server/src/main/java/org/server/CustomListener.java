package org.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.extern.slf4j.Slf4j;
import org.server.entity.Game;
import org.server.entity.GameRepository;
import org.server.entity.GameStep;
import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.server.service.SigninService;
import org.server.service.SignupService;
import org.shared.FailedSignIn;
import org.shared.InitializeGameRequest;
import org.shared.SignupResponse;
import org.shared.SigninRequest;
import org.shared.SignupRequest;
import org.shared.SwitchToGameResponse;
import org.shared.SwitchToMenuResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Component
public class CustomListener extends Listener {

    private final SignupService signupService;
    private final SigninService signinService;

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    private final ScheduledExecutorService gameExecutor = Executors.newScheduledThreadPool(4);

    String[] moves = {"rock", "paper", "scissors"};

    public CustomListener(SignupService signupService, SigninService signinService,
            PlayerRepository playerRepository, GameRepository gameRepository) {

        this.signupService = signupService;
        this.signinService = signinService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public void received(Connection connection, Object object) {

        // signin - логин
        if (object instanceof SigninRequest) {

            SigninRequest request = (SigninRequest) object;
            System.out.println("SigninRequest from client: " + request);

            try {
                Player player = signinService.processSignin(request);
                Game currentGame = findCurrentGame(player.getGames());
                // Если игра уже есть, то возвращаем инфу о текущей игре
                if (currentGame != null) {
                    SwitchToGameResponse response = new SwitchToGameResponse();
                    response.gameStep = currentGame.getCurrentStep().name();
                    response.clientId = currentGame.getPlayer().getId();
                    response.move = moves[new Random().nextInt(moves.length)];
                    sendResponse(response, connection);
                } else {
                    // Если нет, то кидаем в меню
                    SwitchToMenuResponse response = new SwitchToMenuResponse();
                    response.clientId = player.getId();
                    sendResponse(response, connection);
                }
            } catch (RuntimeException ex) {
                FailedSignIn response = new FailedSignIn();
                connection.sendTCP(response);
                sendResponse(response, connection);
            }

        }

        // signup - регистрация
        if (object instanceof SignupRequest) {

            SignupRequest request = (SignupRequest) object;
            log.info("Received SignupRequest from client-id {}", connection.getID());

            signupService.processSignupRequest(request);

            SignupResponse response = new SignupResponse();
            connection.sendTCP(response);
            log.info("Sent SignupResponse from server with connection {}", connection.getID());
            connection.close();
        }

        // запрос об первой игре
        if (object instanceof InitializeGameRequest) {

            InitializeGameRequest request = (InitializeGameRequest) object;
            Game game = new Game();
            Player player = playerRepository.findById(request.playerId).get();
            game.setPlayer(player);
            game.setInProgress(true);
            game.setCurrentStep(GameStep.GAME_STEP_1);
            gameRepository.save(game);
            TimerTask timerTask = new TimerTask() {
                private int timeRemaining = game.getTimeRemaining();
                @Override
                public void run() {
                    // Отправьте предупреждение игроку, когда остается 30, 15, 5, 3 и 1 секунда
                    if (Arrays.asList(30, 15, 5, 3, 1).contains(timeRemaining)) {
                        String warningMessage = "Time remaining: " + timeRemaining + " seconds";
                        log.info(warningMessage);
                    }

                    timeRemaining--;
                    game.setTimeRemaining(timeRemaining);

                    // Завершите игру, если время истекло
                    if (timeRemaining <= 0) {
                        // Здесь обработайте ситуацию, когда время истекло
                        this.cancel();
                    }
                }
            };

            // Запустите таймер с задержкой в 1 секунду и интервалом в 1 секунду
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(timerTask, 1000, 1000);
            // Сохраните ссылку на TimerTask в объекте Game
            game.setTimerTask(timerTask);
            gameRepository.saveAndFlush(game);
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
