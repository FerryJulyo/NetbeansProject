/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tugas;

/**
 *
 * @author ferry
 */
import java.io.IOException;
import java.net.ServerSocket;

public class Tugas1 {
    public static void main(String[] args) {
        ServerSocket theServer;
        for (int i = 500; i <= 1000; i++) {
            try {
                theServer = new ServerSocket(i);
                theServer.close();
            } catch (IOException e) {
                System.out.println("There is a server on port " + i + ".");
            }
        }
    }
}
