package org.server.service;

import org.server.entity.Player;
import org.server.repository.PlayerRepository;
import org.shared.request.SigninRequest;
import org.springframework.stereotype.Service;

@Service
public class SigninService {

    private final PlayerRepository playerRepository;

    public SigninService(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    public Player processSignin(SigninRequest request) throws RuntimeException {
        return playerRepository.findByLoginAndPassword(request.getLogin(), request.getPassword()).orElseThrow(() -> new RuntimeException("no player found"));
    }


}
