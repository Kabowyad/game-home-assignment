package org.server.repository;

import org.server.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("select (count(p) > 0) from Player p where p.login = ?1")
    boolean existsByLogin(String login);

    @Query("select (count(p) > 0) from Player p where p.login = ?1 and p.password = ?2")
    boolean existsByLoginAndPassword(String login, String password);

    @Query("select p from Player p where p.login = ?1 and p.password = ?2")
    Optional<Player> findByLoginAndPassword(String login, String password);



}
