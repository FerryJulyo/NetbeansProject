/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilegame;

/**
 *
 * @author ferry
 */
import java.util.ArrayList;
import java.util.List;


public class Game {

    
    public static final int ROWS = 6;
    public static final int COLUMNS = 10;
    private List<GameService> players;
    private int[][] gameBoard;
    private boolean[] isBlocked;
    private boolean[] hadFirst;
    private boolean finished;
    private PlayerMark playerMarkTurn;
    private int playerCount;

     public Game() {
        players = new ArrayList<>();
        gameBoard = new int[ROWS][COLUMNS];
        isBlocked = new boolean[]{true, false, false, false, false, false};
        hadFirst = new boolean[]{false, false, false, false, false, false};
        finished = false;
        playerMarkTurn = PlayerMark.NONE;
        playerCount = 0;
    }

    public void addPlayer(GameService gameService) {
        players.add(gameService);
        playerCount += 1;
        if (playerCount == 2) {
            new Thread(() -> {
                try{
                    Thread.sleep(10000);
                    startGame();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void startGame() {
        playerMarkTurn = PlayerMark.RED;
        informPlayersOfTurn();
    }

    private void informPlayersOfTurn() {
        for (GameService gameService : players) {
            gameService.informClientOfTurn(playerMarkTurn);
        }
    }

    public synchronized boolean makeMove(String card, int x, int y, int playerMark) {
        InfluenceCard curCard = InfluenceCard.valueOf(card);

        if (curCard == InfluenceCard.NONE || curCard == InfluenceCard.DOUBLE) {
            if (gameBoard[x][y] == PlayerMark.NONE.ordinal()) {
                if (!hadFirst[playerMark]) {
                    hadFirst[playerMark] = true;
                    gameBoard[x][y] = playerMark;
                    return true;
                }
                return checkAdjacent(x, y, playerMark);
            } else {
                return false;
            }
        } else if (curCard == InfluenceCard.REPLACEMENT) {
            if (gameBoard[x][y] == playerMark) {
                return false;
            } else {
                return checkAdjacent(x, y, playerMark);
            }
        } else if (curCard == InfluenceCard.FREEDOM) {
            if (gameBoard[x][y] == PlayerMark.NONE.ordinal()) {
                gameBoard[x][y] = playerMark;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkAdjacent(int x, int y, int playerMark) {
        boolean valid = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    valid = gameBoard[x + i][y + j] == playerMark;
                    if (valid) {
                        gameBoard[x][y] = playerMark;
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                
                }
            }
        }
        return false;
    }

    public void endGame() {
        String scores = getScoresAndWinner();
        for (GameService gameService : players) {
            gameService.end(scores);
        }
        System.out.println("WINNER IS: " + scores);
        finished = true;
        System.out.println("GAME OVER.");
    }

    private String getScoresAndWinner() {
        int[] scores = new int[playerCount];
        for (int[] row : gameBoard) {
            for (int position : row) {
                if (position != 0) {
                    scores[position - 1]++;
                }
            }
        }

        int highestScore = 0;
        String curWinner = "";

        String scoresString = " ";

        for (int i = 0; i < scores.length; i++) {
            scoresString += scores[i] + " ";
            if (scores[i] >= highestScore) {
                highestScore = scores[i];
                curWinner = PlayerMark.values()[1 + i].toString();
            }
        }

        return curWinner + scoresString;
    }

    public void setBlocked(int i) {
        isBlocked[i] = true;
    }

    public void checkBlocked() {
        for (GameService player : players) {
            if (player.isBlocked()) {
                
            } else if (player.availableCards()[InfluenceCard.REPLACEMENT.ordinal()]) {
            
            } else if (boardFull()) {
                System.out.println(player.getPlayerMark() + " is blocked");
                isBlocked[player.getPlayerMark().ordinal()] = true;
                player.setBlocked();
            } else if (player.availableCards()[InfluenceCard.FREEDOM.ordinal()]) {
            
            } else {
                PlayerMark mark = player.getPlayerMark();

                 boolean hasSpace = false;

                for (int row = 0; row < Game.ROWS; row++) {
                    if (hasSpace) {
                        break;
                    }
                    for (int column = 0; column < Game.COLUMNS; column++) {
                        if (hasSpace) {
                            break;
                        }
                        if (gameBoard[row][column] == mark.ordinal()) {
                            for (int i = -1; i < 2; i++) {
                                if (hasSpace) {
                                    break;
                                }
                                for (int j = -1; j < 2; j++) {
                                    try {
                                        if (gameBoard[row + i][column + j] == PlayerMark.NONE.ordinal()) {
                                            hasSpace = true;
                                            break;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                    
                                    }
                                }
                            }
                        }
                    }
                }

                if (!hasSpace) {
                    System.out.println(player.getPlayerMark() + " is blocked");
                    isBlocked[player.getPlayerMark().ordinal()] = true;
                    player.setBlocked();
                }
            }
        }
    }

    private boolean allBlocked() {
        for (int i = 1; i <= playerCount; i++) {
            if (!isBlocked[i]) {
                return false;
            }
        }

        return true;
    }

    public boolean boardFull() {
        for (int[] row : gameBoard) {
            for (int tile : row) {
                if (tile == PlayerMark.NONE.ordinal()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isGameOver() {
        checkBlocked();
        if (allBlocked()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void sendBoard() {
        String board = "";
        for (int[] row : gameBoard) {
            for (int pos : row) {
                board += pos + " ";
            }
        }

        for (GameService gameService : players) {
            gameService.updateBoard(board);
        }
    }

    public PlayerMark getPlayerMarkTurn() {
        return playerMarkTurn;
    }

    public void nextPlayer() {
        PlayerMark player = playerMarkTurn;

        PlayerMark nextPlayer = player.ordinal() == playerCount ? PlayerMark.values()[1] : PlayerMark.values()[playerMarkTurn.ordinal() + 1];

        while (nextPlayer.ordinal() != player.ordinal()) {
            if (isBlocked[nextPlayer.ordinal()]) {
                nextPlayer = nextPlayer.ordinal() == playerCount ? PlayerMark.values()[1] : PlayerMark.values()[nextPlayer.ordinal() + 1];
            } else {
                playerMarkTurn = nextPlayer;
                informPlayersOfTurn();
                return;
            }
        }

        if (isBlocked[player.ordinal()]) {
            endGame();
        }
            informPlayersOfTurn();
    }

    public void setGameBoard(int[][] board){
        gameBoard = board;
    }

    public void setTile(int x, int y, int playerMark){
        gameBoard[x][y] = playerMark;
    }

    public void setHadFirstTrue(){
        hadFirst = new boolean[]{true, true, true, true, true, true};
    }

    public boolean useCheckAdjacent(int x, int y, int playerMark){
        return checkAdjacent(x, y, playerMark);
    }
}

