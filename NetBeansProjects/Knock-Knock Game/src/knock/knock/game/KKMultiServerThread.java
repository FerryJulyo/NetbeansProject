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
import java.io.*;

public class KKMultiServerThread extends Thread {
    private Socket socket = null;

    public KKMultiServerThread(Socket socket) {
		super("KKMultiServerThread");
		this.socket = socket;
    }

    public void run() {
		try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
					    new InputStreamReader(
					    socket.getInputStream()));
	
		    String inputLine, outputLine;
		    KnockKnockProtocol kkp = new KnockKnockProtocol();
		    outputLine = kkp.processInput(null);
		    out.println(outputLine);
	
		    while ((inputLine = in.readLine()) != null) {
			outputLine = kkp.processInput(inputLine);
			out.println(outputLine);
			if (outputLine.equals("Bye"))
			    break;
		    }
		    out.close();
		    in.close();
		    socket.close();
	
		} catch (Exception e) {}
    }
}
