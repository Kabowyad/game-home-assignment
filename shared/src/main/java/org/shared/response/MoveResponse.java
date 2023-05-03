package org.shared.response;

import org.shared.enums.GameStep;

public class MoveResponse {
    private String message;
    private String gameResult;

    public String getMessage() {

        return message;
    }

    public String getGameResult() {

        return gameResult;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public void setGameResult(String gameResult) {

        this.gameResult = gameResult;
    }
}
