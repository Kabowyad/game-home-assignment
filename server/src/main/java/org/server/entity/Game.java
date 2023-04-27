package org.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;


    @Enumerated(EnumType.STRING)
    @Column(name = "player_choice", nullable = false)
    private Move playerChoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "computer_choice", nullable = false)
    private Move computerChoice;

    @Column(nullable = false)
    private String outcome;

}
