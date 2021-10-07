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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient extends JFrame implements ActionListener {

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
    private final JRadioButton none;
    private final JRadioButton dCard;
    private final JRadioButton rCard;
    private final JRadioButton fCard;
    private final JLabel turnIndicator;

    public GameClient() {
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

        none = new JRadioButton("None");
        none.setActionCommand("None");
        none.setSelected(true);
        none.addActionListener(this);

        dCard = new JRadioButton("Double-move");
        dCard.setActionCommand("Double");
        dCard.addActionListener(this);

        rCard = new JRadioButton("Replacement");
        rCard.setActionCommand("Replacement");
        rCard.addActionListener(this);

        fCard = new JRadioButton("Freedom");
        fCard.setActionCommand("Freedom");
        fCard.addActionListener(this);

        ButtonGroup cardGroup = new ButtonGroup();
        cardGroup.add(none);
        cardGroup.add(dCard);
        cardGroup.add(rCard);
        cardGroup.add(fCard);

        turnIndicator = new JLabel();

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(1, 5, 2, 2));
        toolbar.add(none);
        toolbar.add(dCard);
        toolbar.add(rCard);
        toolbar.add(fCard);
        toolbar.add(turnIndicator);
        getContentPane().add(toolbar, BorderLayout.SOUTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(500, 300));
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setLayout(new GridLayout(Game.ROWS, Game.COLUMNS, 2, 2));

        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLUMNS; j++) {
                final int fi = i, fj = j;
                board[i][j] = new Tile();
                board[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (selectedCard == InfluenceCard.NONE) {
                            output.println("MOVE NONE " + fi + " " + fj);
                        } else if (selectedCard == InfluenceCard.DOUBLE) {
                            if (cards[InfluenceCard.DOUBLE.ordinal()]) {
                                output.println("MOVE DOUBLE " + fi + " " + fj);
                            } else {
                                System.out.println("Card Not Available");
                            }
                        } else if (selectedCard == InfluenceCard.REPLACEMENT) {
                            if (cards[InfluenceCard.REPLACEMENT.ordinal()]) {
                                output.println("MOVE REPLACEMENT " + fi + " " + fj);
                            } else {
                                System.out.println("Card Not Available");
                            }
                        } else if (selectedCard == InfluenceCard.FREEDOM) {
                            if (cards[InfluenceCard.FREEDOM.ordinal()]) {
                                output.println("MOVE FREEDOM " + fi + " " + fj);
                            } else {
                                System.out.println("Card Not Available");
                            }
                        }
                    }
                });
                boardPanel.add(board[i][j]);
            }
        }

        getContentPane().add(boardPanel, BorderLayout.CENTER);
        pack();
    }

    private void play() {
        String[] response;
        try {
            while (true) {
                response = input.readLine().trim().split(" ");
                System.out.println("Server Response: " + buildResponse(response));
                if (parseResponse(response).equals("END")) {
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

    private String parseResponse(String[] response) {
        if (response[0].equals("LEGAL_MOVE")) {
            System.out.println("Client Output: legal move. Update influence cards.");
            InfluenceCard card = InfluenceCard.valueOf(response[1]);
            if (card == InfluenceCard.DOUBLE) {
                cards[card.ordinal()] = false;
                dCard.setEnabled(false);
            } else if (card == InfluenceCard.REPLACEMENT) {
                cards[card.ordinal()] = false;
                rCard.setEnabled(false);
            } else if (card == InfluenceCard.FREEDOM) {
                cards[card.ordinal()] = false;
                fCard.setEnabled(false);
            }
            selectedCard = InfluenceCard.NONE;
            none.setSelected(true);
        } else if (response[0].equals("ILLEGAL_MOVE")) {
            System.out.println("Client Output: Illegal Move.");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "None":
                selectedCard = InfluenceCard.NONE;
                break;
            case "Double":
                selectedCard = InfluenceCard.DOUBLE;
                break;
            case "Replacement":
                selectedCard = InfluenceCard.REPLACEMENT;
                break;
            case "Freedom":
                selectedCard = InfluenceCard.FREEDOM;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        GameClient gameClient = new GameClient();
        gameClient.play();
    }
}