package org.shared.response;

import org.shared.enums.GameStep;

public class MoveResponse {
    private String message;
    private GameStep step;
    private String gameResult;

    public String getMessage() {

        return message;
    }

    public GameStep getStep() {

        return step;
    }


    public String getGameResult() {

        return gameResult;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public void setStep(GameStep step) {

        this.step = step;
    }


    public void setGameResult(String gameResult) {

        this.gameResult = gameResult;
    }
}
