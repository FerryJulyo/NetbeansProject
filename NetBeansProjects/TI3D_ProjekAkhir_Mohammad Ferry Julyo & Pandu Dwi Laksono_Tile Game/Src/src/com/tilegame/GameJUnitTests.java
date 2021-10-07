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
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GameJUnitTests {

    private Game game;
    private PlayerMark mark = PlayerMark.RED;
    private PlayerMark opMark = PlayerMark.GREEN;

    @Test
    public void testFreedom(){
        game = new Game();
        game.setHadFirstTrue();

        int x = 0;
        int y = 0;
        game.setTile(0, 0, mark.ordinal());


        int x1 = 0;
        int y1 = 2;
        assertEquals(false, game.makeMove(InfluenceCard.NONE.toString(), x1, y1, mark.ordinal()));

        assertEquals(true, game.makeMove(InfluenceCard.FREEDOM.toString(), x1, y1, mark.ordinal()));

        int x2 = 5;
        int y2 = 5;
        game.setTile(x2, y2, mark.ordinal());
        assertEquals(false, game.makeMove(InfluenceCard.FREEDOM.toString(), x2, y2, mark.ordinal()));
    }

    @Test
    public void testReplacement(){
        game = new Game();
        game.setHadFirstTrue();

        int x = 0;
        int y = 0;
        game.setTile(x, y, mark.ordinal());

        int xG = 0;
        int yG = 1;
        game.setTile(xG, yG, opMark.ordinal());

        assertEquals(false, game.makeMove(InfluenceCard.NONE.toString(), xG, yG, mark.ordinal()));

        assertEquals(true, game.makeMove(InfluenceCard.REPLACEMENT.toString(), xG, yG, mark.ordinal()));

        int x1 = 5;
        int y1 = 5;
        assertEquals(false, game.makeMove(InfluenceCard.REPLACEMENT.toString(), x1, y1, mark.ordinal()));

        game.setTile(x+1, y, mark.ordinal());
        assertEquals(false, game.makeMove(InfluenceCard.REPLACEMENT.toString(), x+1, y, mark.ordinal()));
    }

    @Test
    public void testCheckAdjacent(){
        game = new Game();
        game.setHadFirstTrue();

        int x = 0;
        int y = 0;
        game.setTile(0, 0, mark.ordinal());

        assertEquals(false, game.useCheckAdjacent(x, y+2, mark.ordinal()));

        assertEquals(true, game.useCheckAdjacent(x, y+1, mark.ordinal()));

        game.setTile(x, y+1, opMark.ordinal());

        assertEquals(false, game.useCheckAdjacent(x, y+2, mark.ordinal()));
    }
}