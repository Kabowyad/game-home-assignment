package org.server.service;

import lombok.extern.slf4j.Slf4j;
import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.shared.SignupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SignupService {

    private final PlayerRepository playerRepository;

    public SignupService(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    // После того, как запрос на регистрацию был обработан клиент должен быть отключен от сервера.
    // Был ли запрос успешным или нет - это не важно.
    @Transactional
    public void processSignupRequest(SignupRequest request) {

        boolean exists = playerRepository.existsByLogin(request.getLogin());
        if (!exists) {
            Player player = new Player(request.getLogin(), request.getPassword());
            playerRepository.save(player);
            log.info("Saved new player with login {}", player.getLogin());
        } else {
            System.out.println("Client with login already exists");
        }
    }
}
