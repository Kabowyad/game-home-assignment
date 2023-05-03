package org.server;

import com.esotericsoftware.kryonet.Server;
import lombok.val;
import org.server.entity.Game;
import org.server.repository.GameRepository;
import org.server.service.GameSessionService;
import org.shared.response.MoveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CheckGameTimers {

    private final GameSessionService gameSessionService;
    private final GameRepository gameRepository;

    private final Server server;

    @Autowired
    public CheckGameTimers(GameSessionService gameSessionService, GameRepository gameRepository,
            Server server) {

        this.gameSessionService = gameSessionService;
        this.gameRepository = gameRepository;
        this.server = server;
    }

    @Scheduled(fixedRate = 1000)
    public void runEverySecond() {

        val gamesWithTimerOut = gameSessionService.getGamesWithTimerOut();
        for (Game game : gamesWithTimerOut) {
            // Ставим что проиграл
            game.setPlayerPoints(game.getPlayerPoints() - 1);
            // обновить шаг игры
            game.updateStepGame();

            MoveResponse response = new MoveResponse();
            response.setStep(game.getCurrentStep());
            server.sendToTCP(game.getConnectionId(), response);
            gameRepository.saveAndFlush(game);
        }
    }

}
