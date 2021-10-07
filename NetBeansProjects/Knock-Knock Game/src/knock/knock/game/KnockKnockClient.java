/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knock.knock.game;

/**
 *
 * @author ferry
 */

import java.net.*;

public class KnockKnockClient implements Runnable{
	/**
	 * sets up a socket and a gui
	 */
    public void run(){
		Socket kkSocket = null;
		int port = 4449;
        ClientGui gui = null;
        String laptopName = "127.0.0.1";

        try {
            kkSocket = new Socket(laptopName, port);
            gui = new ClientGui(kkSocket);
            gui.setInstructionLabel("Client listening on port " + port);
        } catch (UnknownHostException e) {
        	System.out.println("Don't know about host: " + laptopName + ".");
            System.exit(1);
        } catch (Exception e) {
        	System.out.println("Couldn't get I/O for the connection to: " + laptopName + ".");
            System.exit(1);
        }
	}
}
