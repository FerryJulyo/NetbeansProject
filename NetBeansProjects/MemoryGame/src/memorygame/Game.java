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
import java.awt.Label;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Game implements ActionListener {
    public static LinkedList cards = new LinkedList();
    public static ArrayList<Character> list = new ArrayList<Character>();
    public static char[] list2;
    public static int upoints;
    public static int notupoints;
    public static int opened;
    public static String firstguess;
    public static String firstguessloc;
    public static String secondguess;
    public static String secondguessloc;
    public static String prevloc;
   
    
    public static void CreateServersList()
    {
            String hello = GameServer.clientlist.replace("[", "");
            hello = hello.replace("]", "");
            hello = hello.replace(",", "");
            hello = hello.replace(" ", "");
            hello = hello.trim();
            //System.out.println("List:"+hello);
            list2 = hello.toCharArray();
            //System.out.println(""+Arrays.toString(list2));
        
       int counter = 0;
       Node tmp = null;
       boolean first = true;
         for (char i : list2)
         {   
            //The first column
            if (counter == 0 && first)
            {
                Node n = new Node(i, 0, null, null);
                tmp = n;
                cards.addLast(n);
            }
            //reached the last column
            else if (counter == 6)
            {
            Node n = new Node(i, 0, null, null);
                if (tmp != null)
                {
                    tmp.setBottom(n);
                }
                tmp = n;
                first = false;
                counter = 0;
                cards.setSize(cards.getSize()+1);
            }
            else
            {
                Node n = new Node(i, 0, null, null);
                cards.addLast(n);
            }
            counter++;
        }
    }
    
    
    public void CreateList()
    {
       while(list.size() < 36)
       {
            Random r = new Random();
            char letter = (char)(r.nextInt(26) + 'a');
            list.add(letter);
            list.add(letter);
       }
       Collections.shuffle(list);
       
       int counter = 0;
       Node tmp = null;
       boolean first = true;
       for (char i : list)
        {   
            //The first column
            if (counter == 0 && first)
            {
                Node n = new Node(i, 0, null, null);
                tmp = n;
                cards.addLast(n);
            }
            //reached the last column
            else if (counter == 6)
            {
            Node n = new Node(i, 0, null, null);
                if (tmp != null)
                {
                    tmp.setBottom(n);
                }
                tmp = n;
                first = false;
                counter = 0;
                cards.setSize(cards.getSize()+1);
            }
            else
            {
                Node n = new Node(i, 0, null, null);
                cards.addLast(n);
            }
            counter++;
        }
    }
    
    //Increments opened counter
//    public void Opened()
//    {
//        opened = opened + 1;
//    }
    
    
    //Event for Buttons
    // public void actionPerformed(ActionEvent e) {
   //    JButton jb = (JButton)e.getSource();
    //   JOptionPane.showMessageDialog(null, jb.getName());
     //   jb.setText("");
    //}
    
    //Checks to see if the letters are the same 
    //and used to transition to next turn
    public void checkletters()
    {
        if (firstguess != null && secondguess != null)
        {
            if (firstguess.compareTo(secondguess) == 0)
            {
                upoints++;
                //System.out.println("ssdfsf"+upoints);
                GUI.Upoints.setText("Your Points: "+upoints + "");
                if (!GUI.single)
                {
                    GUI.NotUpoints.setText("Enemy Points: "+notupoints + "");
                    GUI.turn.setText("Enemy Turn");
                }
                    
                flipback();
                if (!GUI.single)
                    GUI.NextTurn(true);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "No match!");
                GUI.Upoints.setText("Your Points: "+upoints + "");
                if (!GUI.single)
                {
                    GUI.NotUpoints.setText("Enemy Points: "+notupoints + "");
                    GUI.turn.setText("Enemy Turn");
                }
                    
                flipback();
                if (!GUI.single)
                    GUI.NextTurn(false);
            }
        }
    }
    
    //Gets coordinates and finds the letter in the linked list NOTE: flags it as 1
    public static String getLetter(String x)
    {
        String[] lines = x.split(",");
        return Character.toString(cards.searchCoordinates(Integer.parseInt(lines[0]),Integer.parseInt(lines[1])));
    }
    
     //Event for Buttons
     public void actionPerformed(ActionEvent e) {
        JButton jb = (JButton)e.getSource();
        if (jb.getText().compareTo("Check Pairs") == 0)
        {
            JOptionPane.showMessageDialog(null, "There are "+cards.returnRecPairs()+" pairs matched!");
        }
        else if (opened < 2)
        {
        //JButton jb = (JButton)e.getSource();
        String coordinates = jb.getName();
        String letter = getLetter(coordinates);
        jb.setText(letter);
            if (firstguess == null)
            {
                firstguess = letter;
                firstguessloc = jb.getName();
            }
            else
            {
                secondguess = letter;
                secondguessloc = jb.getName();
            }
            
        //Opened();
        opened++;
        checkletters();
        jb.removeActionListener(this);
        }
        else
            checkletters();
        
         //System.out.println(""+firstguess+"____"+secondguess);

     }
     
    //Called to flipcards back or set flags to matched
    public void flipback() 
    {
        String[] lines = firstguessloc.split(",");
        String[] lines2 = secondguessloc.split(",");
        if (cards.pairup(firstguessloc, secondguessloc))
            {
                cards.SettoMatched(Integer.parseInt(lines[0]), Integer.parseInt(lines[1])); 
                cards.SettoMatched(Integer.parseInt(lines2[0]), Integer.parseInt(lines2[1]));
            }
        else
            {
                GUI.addListen(Integer.parseInt(lines[0]), Integer.parseInt(lines[1])); 
                GUI.addListen(Integer.parseInt(lines2[0]), Integer.parseInt(lines2[1]));
                GUI.Closecards();
            }
        prevloc = lines[0]+lines[1]+lines2[0]+lines2[1];
        firstguess = null;
        secondguess = null;
        firstguessloc = null;
        secondguessloc = null;
        opened = 0;
        
    }
    
    public static void corrections(String coordinates)
    {
        int x = Integer.parseInt(coordinates.substring(0, 1));
        int y = Integer.parseInt(coordinates.substring(1, 2));
        int x2 = Integer.parseInt(coordinates.substring(2, 3));
        int y2 = Integer.parseInt(coordinates.substring(3, 4));
        cards.SettoMatched(x, y);
        cards.SettoMatched(x2, y2);
        GUI.MakeChanges();
    }
    
}
