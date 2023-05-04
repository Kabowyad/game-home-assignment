package org.server.entity;

import lombok.Getter;
import lombok.Setter;
import org.shared.enums.GameStep;
import org.shared.enums.Move;

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
import javax.persistence.Transient;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "in_progress", nullable = false)
    private boolean inProgress;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_result")
    private GameResult gameResult;

    @Column(name = "timer_started")
    private Instant timerStarted;

    @Column(name = "moves_passed")
    private Integer movesPassed = 0;

    // Если больше 0 - то победа
    // Если меньше 0 - то проиграл
    // Если 0 - то ничья
    @Column(name = "player_points")
    private Integer playerPoints = 0;

    @Column(name = "time_left")
    private Integer timeLeft = 30;

    @Transient
    private Integer connectionId;

    @Transient
    private Integer maxMoveCounts;

    public Game() {

    }

    public Game(Player player, Integer connectionId, Integer maxMoveCounts) {

        this.player = player;
        this.connectionId = connectionId;
        this.inProgress = true;
        this.timerStarted = Instant.now();
        this.maxMoveCounts = maxMoveCounts;
    }

    public void updateStepGame() {
        if (this.movesPassed.equals(this.maxMoveCounts)) {
            inProgress = false;
            if (playerPoints == 0) gameResult = GameResult.DRAW;
            if (playerPoints > 0) gameResult = GameResult.WIN;
            if (playerPoints < 0) gameResult = GameResult.LOSE;
        } else {
            this.movesPassed = this.movesPassed + 1;
            this.timeLeft = 30;
            this.timerStarted = Instant.now();
        }
    }

    public int makeMove(Move playerMove, Move computerMove) {
        int point;
        if (computerMove == playerMove) {
            point = 0;
        } else {
            point = switch (playerMove) {
                case ROCK -> (computerMove == Move.SCISSORS) ? 1 : -1;
                case PAPER -> (computerMove == Move.ROCK) ? 1 : -1;
                case SCISSORS -> (computerMove == Move.PAPER) ? 1 : -1;
            };
        }
        this.setPlayerPoints(this.getPlayerPoints() + point);
        return point;
    }

}
