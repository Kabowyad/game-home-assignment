package org.shared.enums;

public enum Move {
    ROCK,
    PAPER,
    SCISSORS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
