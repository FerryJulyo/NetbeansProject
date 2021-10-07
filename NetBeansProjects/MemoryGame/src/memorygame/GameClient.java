/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memorygame;

/**
 *
 * @author ferry
 */

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GameClient extends Thread {
    //private InetAddress ipAddress;
    private Socket clientSocket;
    private int prevTurn = -1;
    //private GUI game;
    
    public GameClient(String ipAddress)
    {
        try 
        {
            clientSocket = new Socket(ipAddress, 9875);
        } catch (IOException ex) {
            
        }
        catch (NullPointerException ex)
        {
            
        }
    }
    
    public void run() 
    {
        while(true)
        {
            try {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = inFromServer.readLine();
                //System.out.println("SERVER >"+ message+" "); 
                if (message.trim().substring(0,2).compareTo("00") == 0)
                {
                    //System.out.println("we are in boys!");
                }
                else if (message.trim().substring(0,2).compareTo("01") == 0)
                {
                    GUI.enableButtons(); 
                    GUI.turn.setText("Your Turn");
                   
                        if (message.trim().substring(2,5).compareTo("pts") == 0)
                        {
                            GUI.addEnemypts();
                            Game.corrections(message.trim().substring(5,9));
                        }
                }
                else if (message.trim().substring(0,2).compareTo("10") == 0)
                {
                    JOptionPane.showMessageDialog(null, "Your opponent won the coin toss!");
                    GUI.turn.setText("Enemy Turn");
                    GUI.disableButtons(); 
                }
                else
                {
                    GUI.turn.setText("Enemy Turn");
                    GUI.disableButtons();
                }
            } catch (IOException ex) {
                
            }
            catch (NullPointerException ex)
            {
                
            }
        }
    }
    
    public void sendData(String data)
    { 
        try 
        { PrintStream PS = new PrintStream(clientSocket.getOutputStream());
          PS.println(data);
        } catch (IOException | NullPointerException ex) {
            
        }
       
    }
    
    public int CloseConnection()
    {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            
        }
        return -1;
    }
}