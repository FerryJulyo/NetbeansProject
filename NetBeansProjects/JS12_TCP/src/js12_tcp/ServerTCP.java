/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package js12_tcp;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ferry
 */
public class ServerTCP {
public static void main(String[] args) {
 // TODO code application logic here
 try {
 ServerSocket ss=new ServerSocket(12345);
 Socket sk=ss.accept();
 //BufferedReader br=new BufferedReader(
 // new InputStreamReader(sk.getInputStream()));
 //String line=br.readLine();
 //System.out.println("ini dari klient ="+line);
 sk.close();
 } catch (Exception e) {
 }
}}
 