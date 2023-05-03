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

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private GameStep currentStep;

    @Column(name = "timer_started")
    private Instant timerStarted;

    // Если больше 0 - то победа
    // Если меньше 0 - то проиграл
    // Если 0 - то ничья
    @Column(name = "player_points")
    private Integer playerPoints = 0;

    @Column(name = "time_left")
    private Integer timeLeft;

    @Transient
    private Integer connectionId;

    public Game() {

    }

    public Game(Player player, Integer connectionId) {

        this.player = player;
        this.connectionId = connectionId;
        this.inProgress = true;
        this.currentStep = GameStep.GAME_STEP_1;
        this.timerStarted = Instant.now();
    }

    public void updateStepGame() {
        this.timeLeft = 30;
        this.timerStarted = Instant.now();
        switch (this.currentStep) {
            case GAME_STEP_1:
                currentStep = GameStep.GAME_STEP_2;
                break;
            case GAME_STEP_2:
                currentStep = GameStep.GAME_STEP_3;
                break;
            case GAME_STEP_3:
                currentStep = GameStep.END;
                inProgress = false;
                if (playerPoints == 0) gameResult = GameResult.DRAW;
                if (playerPoints > 0) gameResult = GameResult.WIN;
                if (playerPoints < 0) gameResult = GameResult.LOSE;
                break;
            case END:
                break;
        }
    }

    public int makeMove(Move playerMove, Move computerMove) {
        int point;
        if (computerMove == playerMove) {
            point = 0;
        } else {
            switch (playerMove) {
                case ROCK:
                    point = (computerMove == Move.SCISSORS) ? 1 : -1;
                    break;
                case PAPER:
                    point = (computerMove == Move.ROCK) ? 1 : -1;
                    break;
                case SCISSORS:
                    point = (computerMove == Move.PAPER) ? 1 : -1;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid move");
            }
        }
        this.setPlayerPoints(this.getPlayerPoints() + point);
        return point;
    }

}
