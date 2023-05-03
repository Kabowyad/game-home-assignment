package org.server.listeners;

import lombok.val;
import org.server.entity.Game;
import org.server.repository.GameRepository;
import org.server.service.GameSessionService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {

    private GameSessionService gameSessionService;

    private GameRepository gameRepository;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        val games = gameSessionService.returnActiveGames();
        for (Game game : games) {
            gameRepository.saveAndFlush(game);
        }
    }
}
