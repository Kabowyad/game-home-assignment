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
import javax.persistence.Transient;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

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
    @Column(name = "player_choice")
    private Move playerChoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "computer_choice")
    private Move computerChoice;

    @Column(name = "in_progress", nullable = false)
    private boolean inProgress;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_result")
    private GameResult gameResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private GameStep currentStep;

    @Column(name = "time_remaining")
    private int timeRemaining = 30;

    @Transient
    private TimerTask timerTask;

    // Геттеры и сеттеры для нового поля inProgress
    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Player getPlayer() {

        return player;
    }

    public void setPlayer(Player player) {

        this.player = player;
    }

    public Move getPlayerChoice() {

        return playerChoice;
    }

    public void setPlayerChoice(Move playerChoice) {

        this.playerChoice = playerChoice;
    }

    public Move getComputerChoice() {

        return computerChoice;
    }

    public void setComputerChoice(Move computerChoice) {

        this.computerChoice = computerChoice;
    }

    public GameStep getCurrentStep() {

        return currentStep;
    }

    public void setCurrentStep(GameStep currentStep) {

        this.currentStep = currentStep;
    }

    public int getTimeRemaining() {

        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {

        this.timeRemaining = timeRemaining;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }
}
