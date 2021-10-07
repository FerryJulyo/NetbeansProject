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
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GameBotClient extends JFrame {

    private static final String SERVER = "localhost";
    private static final int PORT = 8080;
    private static Color[] colours = new Color[]{Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.PINK};
    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private boolean[] cards;
    private InfluenceCard selectedCard;
    private PlayerMark playerMark;
    private PlayerMark playerTurn;
    private Tile[][] board = new Tile[Game.ROWS][Game.COLUMNS];
    private int[][] gameBoard = new int[Game.ROWS][Game.COLUMNS];
    private final JLabel none;
    private final JLabel dCard;
    private final JLabel rCard;
    private final JLabel fCard;
    private final JLabel lastCardUsed;
    private final JLabel turnIndicator;

    public GameBotClient() {
        try {
            System.out.println("Getting connection");
            connection = new Socket(SERVER, PORT);
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new PrintWriter(connection.getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection Failed. You Are Not Connected.", "Connection Failure", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        cards = new boolean[]{true, true, true};
        selectedCard = InfluenceCard.NONE;

        setTitle("Client GameService Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        none = new JLabel("None");
        none.setHorizontalAlignment(SwingConstants.CENTER);

        dCard = new JLabel("Double-move");
        dCard.setHorizontalAlignment(SwingConstants.CENTER);

        rCard = new JLabel("Replacement");
        rCard.setHorizontalAlignment(SwingConstants.CENTER);

        fCard = new JLabel("Freedom");
        fCard.setHorizontalAlignment(SwingConstants.CENTER);

        lastCardUsed = new JLabel("Last Card: NONE");
        lastCardUsed.setHorizontalAlignment(SwingConstants.CENTER);

        turnIndicator = new JLabel();
        turnIndicator.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel t1 = new JPanel();
        t1.setLayout(new GridLayout(1, 4, 2, 2));
        t1.add(none);
        t1.add(dCard);
        t1.add(rCard);
        t1.add(fCard);


        JPanel t2 = new JPanel();
        t2.setLayout(new GridLayout(1, 2, 2, 2));
        t2.add(lastCardUsed);
        t2.add(turnIndicator);

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(2, 1));
        toolbar.add(t1);
        toolbar.add(t2);
        getContentPane().add(toolbar, BorderLayout.SOUTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(500, 300));
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setLayout(new GridLayout(Game.ROWS, Game.COLUMNS, 2, 2));

        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLUMNS; j++) {
                board[i][j] = new Tile();
                boardPanel.add(board[i][j]);
            }
        }

        getContentPane().add(boardPanel, BorderLayout.CENTER);
        pack();
    }

    private void play() {
        String[] response;
        String action;
        try {
            while (true) {
                response = input.readLine().trim().split(" ");
                System.out.println("Server Response: " + buildResponse(response));
                action = parseResponse(response);
                if (action.equals("PLAY")) {
                    botPlay();
                } else if (action.equals("END")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void botPlay(){
        System.out.println("Entered botPlay");
        Random r = new Random();
        int x = r.nextInt(Game.ROWS);
        int y = r.nextInt(Game.COLUMNS);

        if(mustUseReplacement()){
            output.println("MOVE REPLACEMENT " + x + " " + y);
        } else if(mustUseFreedom() || countEmptyTiles() < ((Game.ROWS * Game.COLUMNS)/2) && cards[InfluenceCard.FREEDOM.ordinal()]) {
            output.println("MOVE FREEDOM " + x + " " + y);
        } else if(countEmptyTiles() > 2 && r.nextInt(20) < 5 && cards[InfluenceCard.DOUBLE.ordinal()]) {
            output.println("MOVE DOUBLE " + x + " " + y);
        } else{
            output.println("MOVE NONE " + x + " " + y);
        }
    }

    private boolean mustUseReplacement(){
        if((boardFull() || hasNoAdjacents()) && cards[InfluenceCard.REPLACEMENT.ordinal()]) {
            return true;
        } else {
            return false;
        }
    }

    private boolean mustUseFreedom() {
        if(!boardFull() && hasNoAdjacents() && cards[InfluenceCard.FREEDOM.ordinal()]){
            return true;
        } else {
            return false;
        }
    }

    private boolean boardFull(){
        for(int[] row : gameBoard) {
            for(int column : row) {
                if(column == PlayerMark.NONE.ordinal()){
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasNoAdjacents(){
        for(int x = 0; x < Game.ROWS; x++) {
            for(int y = 0; y < Game.COLUMNS; y++) {
                if(gameBoard[x][y] == playerMark.ordinal()){
                    if(checkFreeAdjacent(x, y)){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkFreeAdjacent(int x, int y) {
        boolean valid = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    valid = gameBoard[x + i][y + j] == PlayerMark.NONE.ordinal();
                    if (valid) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    
                }
            }
        }
        return false;
    }

    private int countEmptyTiles(){
        int count = 0;
        for(int[] row : gameBoard) {
            for(int column : row) {
                if(column == PlayerMark.NONE.ordinal()){
                    count++;
                }
            }
        }

        return count;
    }

    private String parseResponse(String[] response) {
        if (response[0].equals("LEGAL_MOVE")) {
            System.out.println("Client Output: legal move. Update influence cards.");
            InfluenceCard card = InfluenceCard.valueOf(response[1]);
            String ifDouble = "";
            if (card == InfluenceCard.DOUBLE) {
                cards[card.ordinal()] = false;
                dCard.setText(dCard.getText() + " - USED");
                dCard.setEnabled(false);
                lastCardUsed.setText("Last Card: " + card.toString());
                try{
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ifDouble = "PLAY";
            } else if (card == InfluenceCard.REPLACEMENT) {
                cards[card.ordinal()] = false;
                rCard.setText(rCard.getText() + " - USED");
                rCard.setEnabled(false);
            } else if (card == InfluenceCard.FREEDOM) {
                cards[card.ordinal()] = false;
                fCard.setText(fCard.getText() + " - USED");
                fCard.setEnabled(false);
            }
            lastCardUsed.setText("Last Card: " + card.toString());
            return ifDouble;
        } else if (response[0].equals("ILLEGAL_MOVE")) {
            System.out.println("Client Output: Illegal Move.");
            return "PLAY";
        } else if (response[0].equals("INVALID_MOVE")) {
            System.out.println("Client Output: Invalid Move.");
        } else if (response[0].equals("BOARD")) {
            int index;
            for (int i = 0; i < Game.ROWS; i++) {
                for (int j = 0; j < Game.COLUMNS; j++) {
                    index = Integer.parseInt(response[1 + ((i * Game.COLUMNS) + j)]);
                    gameBoard[i][j] = index;
                    board[i][j].setColor(colours[index]);
                    board[i][j].repaint();
                }
            }
        } else if (response[0].equals("MARK")) {
            System.out.println("Client Output: Adding player playerMark");
            playerMark = PlayerMark.valueOf(response[1]);
        } else if (response[0].equals("TURN")) {
            playerTurn = PlayerMark.valueOf(response[1]);
            updateTurnIndicator();
            if(playerMark.ordinal() == playerTurn.ordinal()){
                try{
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Bot should play");
                return "PLAY";
            } else {
                return "NOPLAY";
            }
        } else if (response[0].equals("MESSAGE")) {
            System.out.println("Client Output: " + buildResponse(response));
        } else if (response[0].equals("END")) {
            String scores = "";
            for (int i = 2; i < response.length; i++) {
                scores += PlayerMark.values()[i - 1].toString() + ":" + response[i] + " | ";
            }
            if (response[1].equals(playerMark.toString())) {
                JOptionPane.showMessageDialog(this, "WINNER! " + scores, "Game Finished. Final Scores", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "LOSER! " + scores, "Game Finished. Final Scores", JOptionPane.INFORMATION_MESSAGE);

            }
            System.out.println("Client Output: Ending session.");
            output.println("END");
            return "END";
        }
        return "OK";
    }

    private String buildResponse(String[] response) {
        String rspString = "";
        for (String word : response) {
            rspString += word + " ";
        }
        return rspString;
    }

    private void updateTurnIndicator() {
        if (playerTurn.toString().equals(PlayerMark.NONE.toString())) {
            turnIndicator.setText("STARTING...");
        } else if (playerTurn.toString().equals(playerMark.toString())) {
            turnIndicator.setText("YOUR TURN");
        } else {
            turnIndicator.setText(playerTurn.toString() + "'s TURN");
        }
    }

    class Tile extends JPanel {

        public Tile() {
            setBackground(Color.WHITE);
        }

        public void setColor(Color color) {
            setBackground(color);
        }
    }

    public static void main(String[] args) {
        GameBotClient gameBotClient = new GameBotClient();
        gameBotClient.play();
    }
}
