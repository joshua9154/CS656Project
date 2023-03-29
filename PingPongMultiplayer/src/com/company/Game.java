package com.company;

import java.awt.*;

public class Game {
    private Point ball = new Point(700, 500),
            onePaddle = new Point(1150, 500),
            twoPaddle = new Point(650, 500);
    private boolean pauseGame=true;

    public Point getBall() {
        return ball;
    }

    public void setBall(Point ball) {
        this.ball = ball;
    }

    public Point getOnePaddle() {
        return onePaddle;
    }

    public void setOnePaddle(Point onePaddle) {
        this.onePaddle = onePaddle;
    }

    public Point getTwoPaddle() {
        return twoPaddle;
    }

    public void setTwoPaddle(Point twoPaddle) {
        this.twoPaddle = twoPaddle;
    }

    public boolean isPauseGame() {
        return pauseGame;
    }

    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }
}
