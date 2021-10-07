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
import java.io.IOException;
import java.net.*;

public class GameServer {

    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        frame.setTitle("Server Window");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JLabel serverText = new JLabel("This is the Server window. Close this window to end the server.");
        serverText.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(serverText, BorderLayout.CENTER);

        Game game = new Game();

        ServerSocket server = new ServerSocket(PORT);

        System.out.println("Started The Server On Port " + PORT);
        System.out.println("Waiting for clients to connect...");

        int playerCount = 0;

        while (!game.isFinished()) {
            if (playerCount < 5) {
                try {
                    Socket connection = server.accept();
                    System.out.println("Client Connected");
                    playerCount += 1;
                    GameService gameService = new GameService(game, connection, PlayerMark.values()[playerCount]);
                    new Thread(gameService).start();
                    game.addPlayer(gameService);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}