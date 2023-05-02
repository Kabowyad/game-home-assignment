package org.server.service;

import org.server.entity.Game;
import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.shared.SigninRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SigninService {

    private PlayerRepository playerRepository;

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
