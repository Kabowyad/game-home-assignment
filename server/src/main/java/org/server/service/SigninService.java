package org.server.service;

import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.shared.request.SigninRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SigninService {

    private final PlayerRepository playerRepository;

    public SigninService(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player processSignin(SigninRequest request) throws RuntimeException {
        Optional<Player> playerOptional = playerRepository.findByLoginAndPassword(request.getLogin(), request.getPassword());

        if (playerOptional.isEmpty()) {
            throw new RuntimeException("no player found");
        }

        return playerOptional.get();
    }


}
