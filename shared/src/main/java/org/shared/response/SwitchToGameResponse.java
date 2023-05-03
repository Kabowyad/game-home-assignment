package org.shared.response;

public class SwitchToGameResponse {

    private String gameStep;

    private Integer timeLeft;

    public SwitchToGameResponse() {

    }

    public SwitchToGameResponse(String gameStep, Integer timeLeft) {

        this.gameStep = gameStep;
        this.timeLeft = timeLeft;
    }

    public String getGameStep() {

        return gameStep;
    }

    public Integer getTimeLeft() {

        return timeLeft;
    }
}

