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

public class serverPort {
    public static void main(String[] args) {
        ServerSocket theServer;
        for (int i = 1024; i <= 65535; i++) {
            try {
                theServer = new ServerSocket(i);
                theServer.close();
            } catch (IOException e) {
                System.out.println("There is a server on port " + i + ".");
            }
        }
    }
}
