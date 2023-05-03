package org.server;

import com.esotericsoftware.kryonet.Server;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.server.repository.GameRepository;
import org.server.service.GameSessionService;
import org.shared.request.TimeLeftResponse;
import org.shared.response.MoveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CheckGameTimers {

    private final GameSessionService gameSessionService;
    private final GameRepository gameRepository;

    private final Server server;

    List<Integer> timeIntervals = Arrays.asList(30, 15, 5, 3, 1);

    @Autowired
    public CheckGameTimers(GameSessionService gameSessionService, GameRepository gameRepository,
            Server server) {

        this.gameSessionService = gameSessionService;
        this.gameRepository = gameRepository;
        this.server = server;
    }

    @Scheduled(fixedRate = 1000)
    public void timerJob() {

        gameSessionService.getAllEntries().forEach(entry -> {

            if (entry.getValue().isInProgress()) {
                // Checking if timer passed and need to take point
                if (entry.getValue().getTimerStarted() != null &&
                        entry.getValue().getTimeLeft() <= 0) {
                    val duration = Duration.between(entry.getValue().getTimerStarted(), Instant.now());
                    if (duration.getSeconds() >= 30) {
                        val game = entry.getValue();
                        log.info("Found game with no time to move: {}",
                                entry.getValue().getId());
                        MoveResponse response = new MoveResponse();
                        if (game.getMovesPassed().equals(game.getMaxMoveCounts())) {
                            response.setGameResult(game.getGameResult().name());
                            gameSessionService.deleteFinishedGameFromSessions(entry.getKey());
                        } else {
                            game.updateStepGame();
                        }
                        gameRepository.saveAndFlush(entry.getValue());
                        response.setMessage("Вы не сделали свой ход вовремя. Вы проиграли этот раунд");
                        server.sendToTCP(entry.getKey(), response);
                    }
                }

                // Checking if we need to send a message to player
                long secondsPassed = Duration.between(
                        entry.getValue().getTimerStarted(),
                        Instant.now()).getSeconds();
                int timeLeft = 30 - (int) secondsPassed;
                if (timeIntervals.contains(timeLeft)) {
                    TimeLeftResponse request = new TimeLeftResponse();
                    request.setMessage(String.format("У вас осталось %s секунд", timeLeft));
                    server.sendToTCP(entry.getKey(), request);
                }

                // Taking second down
                entry.getValue().setTimeLeft(entry.getValue().getTimeLeft() - 1);
            }
        });

    }

}
