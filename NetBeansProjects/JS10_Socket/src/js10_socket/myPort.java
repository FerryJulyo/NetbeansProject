/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package js10_socket;

/**
 *
 * @author ferry
 */
import java.net.*;
import java.io.*;

public class myPort {
  public static void main(String[] args){
        Socket theSocket;
        String host = "localhost";
        for (int i = 0; i <= 100; i++) {
            try {
                theSocket = new Socket(host, i);
                System.out.println("There is a server on port " + i + " of " + host);
            } catch (UnknownHostException e) {
                System.err.println(e);
                break;
            }
            catch(IOException e){
            }
        }
    }
}
