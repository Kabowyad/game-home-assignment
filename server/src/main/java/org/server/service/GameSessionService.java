package org.server.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.server.entity.Game;
import org.shared.enums.GameStep;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GameSessionService {

    private final ConcurrentHashMap<Integer, Game> gameSessions;

    public GameSessionService() {

        this.gameSessions = new ConcurrentHashMap<>();
    }

    public Game getGameByConnectionID(Integer connectionID) {

        return gameSessions.get(connectionID);
    }

    public void addGameToSessions(Integer connectionId, Game game) {

        gameSessions.put(connectionId, game);
    }

    public void deleteFinishedGameFromSessions(Integer connectionId) {

        gameSessions.remove(connectionId);
    }

    public List<Game> returnActiveGames() {

        return new ArrayList<>(gameSessions.values());
    }

    public List<Game> getGamesWithTimerOut() {

        List<Game> games = new ArrayList<>();
        for (Map.Entry<Integer, Game> entry : gameSessions.entrySet()) {
            if (entry.getValue().getTimerStarted() != null &&
                    entry.getValue().getTimeLeft() <= 0 &&
                    entry.getValue().getCurrentStep() != GameStep.END) {
                val duration = Duration.between(entry.getValue().getTimerStarted(), Instant.now());
                if (duration.getSeconds() >= 30) {
                    log.info("Джоба нашла игру с протухшим таймеров: {}",
                            entry.getValue().getId());
                    games.add(entry.getValue());
                }
            }
        }
        return games;
    }
}

