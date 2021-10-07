/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package js11_multisocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author ferry
 */
public class ClientBasic {

    public static void main(String args[]) {
        try {
            Socket sk = new Socket("127.0.0.1", 1236);
            BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            String put = "";
            while (true) {
                if ((put = in.readLine()) != null) {
                    System.out.println(put);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
