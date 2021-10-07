/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package js11_multisocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ferry
 */

public class ServerBasic {

    public static void main(String args[]) throws IOException {
        ServerSocket ss = new ServerSocket(1236);
        boolean keadaan = true;
        int urut = 1;
        while (keadaan) {
            new server(ss.accept(), urut).start();
            System.out.println("Client ke-" + urut + "Masuk");
            urut++;
        }
    }
}

class server extends Thread {

    static Socket sc = null;
    int angka = 0;

    server(Socket a, int angka) {
        this.angka = angka;
        server.sc = a;
    }

    public void run() {
        System.out.println("Client Connect " + sc.getInetAddress() + "onPort" + sc.getPort());
        try {
            PrintWriter out = new PrintWriter(sc.getOutputStream(), true);
            out.println("Selamat Datang Client ke- " + angka);
        } catch (IOException ex) {
            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
