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

public class GameServer extends Thread{
    
    private ServerSocket welcomeSocket;
    private Socket connectionSocket;
    public static InetAddress EnemyIpAddress;
    public static String clientlist = null;
    public static int received = 0;
    String message;
    
    public GameServer()
    {
        try 
        {
            welcomeSocket = new ServerSocket(9875);
            connectionSocket = welcomeSocket.accept();
            EnemyIpAddress = welcomeSocket.getInetAddress();
        } catch (IOException ex) {
            
        }
    }
    
    public void run()
    {
        while(true)
        {     
            try 
            {
                PrintStream PS = new PrintStream(connectionSocket.getOutputStream());
                PS.println("00");
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
                message = inFromClient.readLine();
                //System.out.println("CLIENT ["+connectionSocket.getLocalAddress()+connectionSocket.getPort()+"] >"+ message); 
             if (message.trim().substring(0,2).compareTo("00") == 0)
             {
                clientlist = message.substring(2);
                Game.CreateServersList();
             }
             if (message.trim().substring(0,2).compareTo("01") == 0)
             {
                GUI.turn.setText("Your turn!");
                GUI.enableButtons();
                if (message.trim().substring(2,5).compareTo("pts") == 0)
                {
                    GUI.addEnemypts();
                    Game.corrections(message.trim().substring(5,9));
                }
             }
            } catch (IOException e) {
               
            }
            catch (NullPointerException ex)
            {

            }
        }
    }
    
    public void sendData(String data)
    {
        DataOutputStream outToClient;
       
        try 
        {                   
            PrintStream PS = new PrintStream(connectionSocket.getOutputStream());
            PS.println(data);
            
        } catch (IOException ex) {
            
        }
        catch (NullPointerException ex)
        {
            
        }
        
    }
}