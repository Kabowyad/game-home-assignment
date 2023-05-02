package org.server.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(name = "last_auth_date")
    private Instant lastAuthDate;

    @Column(name = "registration_date", nullable = false)
    @CreationTimestamp
    private Instant registrationDate;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Game> games;

    public Player() {

    }

    public Player(String login, String password) {

        this.login = login;
        this.password = password;
    }

    public Long getId() {

        return id;
    }

    public String getLogin() {

        return login;
    }

    public String getPassword() {

        return password;
    }

    public Instant getLastAuthDate() {

        return lastAuthDate;
    }

    public Instant getRegistrationDate() {

        return registrationDate;
    }

    public List<Game> getGames() {

        return games;
    }
}
