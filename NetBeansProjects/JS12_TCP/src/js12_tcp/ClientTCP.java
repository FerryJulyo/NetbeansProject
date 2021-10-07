/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package js12_tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ferry
 */
public class ClientTCP {
    public static void main(String[] args) throws IOException {
 // TODO code application logic here
 Socket cl=new Socket("localhost", 12345);
 DataOutputStream dos=new
DataOutputStream(cl.getOutputStream());
 dos.writeBytes("ini aq");
}
}

