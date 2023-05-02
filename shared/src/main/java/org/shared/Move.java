package org.shared;

public enum Move {
    ROCK,
    PAPER,
    SCISSORS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
