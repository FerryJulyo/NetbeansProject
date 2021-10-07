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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GameService extends Thread {
    private Game game;
    private PlayerMark playerMark;
    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private boolean[] cards;
    private boolean isBlocked;

    public GameService(Game game, Socket connection, PlayerMark playerMark) {
        this.game = game;
        this.playerMark = playerMark;
        this.connection = connection;
        try{
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new PrintWriter(connection.getOutputStream(), true);
            //Welcomes the players and sends them their player mark for this game.
            output.println("MESSAGE Welcome. You have connected.");
            output.println("MARK " + playerMark.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        cards = new boolean[]{true, true, true};
        isBlocked = false;
    }

    @Override
    public void run() {
        try{
            placeInitialTile();
            while(true) {
                String[] command = input.readLine().trim().split(" ");
                System.out.println("Command was: " + command[0]);
                if(parseCommand(command).equals("END")){
                    break;
                }
            }
        } catch (IOException e) {
            game.endGame();
            this.interrupt();
        } finally {
            try{
                connection.close();
            } catch(IOException e){
                
            }
            this.interrupt();
        }
    }

    private void placeInitialTile(){
        Random r = new Random();

        boolean attemptPlace = true;
        if(game.boardFull()){
            game.setBlocked(playerMark.ordinal());
            attemptPlace = false;
        }

        while(attemptPlace) {
            if(game.makeMove("NONE", r.nextInt(Game.ROWS), r.nextInt(Game.COLUMNS), playerMark.ordinal())){
                break;
            }
        }

        game.sendBoard();
        game.checkBlocked();

        informClientOfTurn(game.getPlayerMarkTurn());

        if(game.isGameOver()){
            game.endGame();
            this.interrupt();
        }
    }

    private String parseCommand(String[] command){
        if(command[0].equals("END")){
            return "END";
        } else if(game.getPlayerMarkTurn() == playerMark) {
            if (command[0].equals("MOVE") && command.length == 4) {
                try{
                    String card = command[1];
                    int x = Integer.parseInt(command[2]);
                    int y = Integer.parseInt(command[3]);
                    if(game.makeMove(card, x, y, playerMark.ordinal())){
                        game.sendBoard();
                        game.checkBlocked();
                        if(InfluenceCard.valueOf(card) != InfluenceCard.DOUBLE) {
                            game.nextPlayer();
                        }
                        if(isBlocked) {
                            game.nextPlayer();
                        }
                        removeCard(card);
                        output.println("LEGAL_MOVE " + card);
                    }
                    else{
                        output.println("ILLEGAL_MOVE");
                    }
                } catch (NumberFormatException e){
                    output.println("INVALID_MOVE");
                } finally {
                    if(game.isGameOver()){
                        game.endGame();
                    }
                }
            } else {
                System.out.println(command[0]);
                output.println("MESSAGE Unknown Command");
            }
        } else {
            output.println("MESSAGE Not your turn.");
        }
        return "OK";
    }

    public void setBlocked(){
        isBlocked = true;
    }

    public boolean isBlocked(){
        return isBlocked;
    }

    public void setPlayerMark(PlayerMark mark){
        playerMark = mark;
    }

    public PlayerMark getPlayerMark(){
        return playerMark;
    }

    public boolean[] availableCards() {
        return cards;
    }

    public void updateBoard(String command) {
        output.println("BOARD " + command);
    }

    public void end(String scores){
        output.println("END " + scores);
    }

    private void removeCard(String card){
        if(!card.equals(InfluenceCard.NONE.toString())){
            cards[InfluenceCard.valueOf(card).ordinal()] = false;
        }
    }

    public void informClientOfTurn(PlayerMark mark){
        output.println("TURN " + mark);
    }
}