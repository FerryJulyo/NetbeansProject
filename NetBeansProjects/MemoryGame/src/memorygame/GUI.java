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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GUI extends JFrame {

    

    private JLabel label;
    private static JButton[][] button;
    private JTextField textfield;
    private Panel paneli;
    private Panel paneli2;
    private Panel paneli3;
    private JButton Check;
    private static int size;
    private static Node n;
    static Label Upoints;
    static Label NotUpoints;
    static Label turn;
    static Game game = new Game();
    public static boolean server = false;
    public static boolean single = false;
    public static boolean coinloss = false;
    
    
    public static GameClient socketClient;
    public static GameServer socketServer;
   
    
    
    
    public GUI()
    {
        
        //The panel that will have all the other panels
        paneli = new Panel();
        paneli.setSize(100, 200);
        add(paneli);
        
        //create list and set resizable to false
        setResizable(false);
        
        //LinkedList list = game.cards; //OLD WAY
        setLayout(new GridLayout(1,1));
        int x = (int) Math.sqrt(game.cards.getSize());
        size = x;
        button = new JButton[x][x];
        //n = list.getHead(); //OLD WAY
        
        //create panel for buttons
        paneli2 = new Panel(); 
        paneli2.setLayout(new GridLayout(6,6));
        paneli.add(paneli2);
        
        //create buttons
        for (int i=0; i<x; i++)
        {
            for (int j=0; j<x; j++)
            {    
                button[i][j] = new JButton(i+","+j);
                button[i][j].setName(i+","+j);
                button[i][j].setText("");
                button[i][j].setPreferredSize(new Dimension(55,35));
                paneli2.add(button[i][j]);
                button[i][j].addActionListener(new Game());  
            }
        }
        
        //create panel for point system
        NotUpoints = new Label("Enemy Points: "+ Game.notupoints + "");
        turn = new Label("Your Turn!");
        Upoints = new Label("Your Points: "+ Game.upoints + "");
        paneli3 = new Panel();
        paneli3.setSize(100, 100);
        paneli3.add(Upoints);
        paneli3.add(NotUpoints);
        paneli3.add(turn);
        add(paneli3);
        
        
        //recursive button
        Check = new JButton("Check Pairs");
        Check.addActionListener(game);
        paneli3.add(Check);
        
        
        //print list for testing purposes
//       game.cards.printLinkedList();
//        System.out.println("");
        //list.printLinkedList();
       
        
        
    }
    //Gives a button back its actionlistener  
    public static void addListen(int x, int y)
    {
        n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                if (i == x && j == y)
                {
                    button[i][j].addActionListener(new Game());
                }
            }
        }
    }
    
    public static void delListen()
    {
        n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                if (Game.cards.IsMatched(i, j))
                {
                    button[i][j].removeActionListener(game);
                    button[i][j].setEnabled(false);
                }
            }
        }
    }
    
      //Enable buttons
    public static void enableButtons()
    {
        n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                button[i][j].setEnabled(true);
            }
        }
        delListen();
    }
    
    //Disable buttons
    public static void disableButtons()
    {
        n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                button[i][j].setEnabled(false);
            }
        }
    }

    //goes to next turn and sends other client/server new info
    public static void NextTurn(Boolean correct)
    {
        disableButtons(); 
        if (correct)
        {
            if (server)
            {
                socketServer.sendData("01"+"pts"+Game.prevloc);
            }
            else
            {
               socketClient.sendData("01"+"pts"+Game.prevloc);
            }
        }
        else
        {
            if (server)
            {
                socketServer.sendData("01"+"0000000");
            }
            else
            {
               socketClient.sendData("01"+"0000000");
            }
        }
        if (Game.upoints > 9)
        {
            JOptionPane.showMessageDialog(null, "You won!");
        } 
    }
    
    //If the flag is not 2 then close cards
    static void Closecards() 
    {
      n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                //String[] lines = button[i][j].getName().split(",");
                if (!Game.cards.IsMatched(i, j))
                {
                    button[i][j].setText(button[i][j].getName());
                    button[i][j].setText("");
                    
                }
            }
        }
    }
    
    static void addEnemypts()
    {
        Game.notupoints++;
        NotUpoints.setText("Enemy Points: "+Game.notupoints + "");
        if (Game.notupoints > 9)
        {
            JOptionPane.showMessageDialog(null, "You lost!");
        }
    }
    
    static void MakeChanges()
    {
        n = game.cards.getHead();
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {    
                //String[] lines = button[i][j].getName().split(",");
                if (Game.cards.IsMatched(i, j))
                {
                    button[i][j].setText(Game.getLetter(button[i][j].getName()));
                    Game.cards.SettoMatched(i, j);
                    button[i][j].removeActionListener(game);
                    button[i][j].setEnabled(false);
                }
            }
        }
    }
    
    public static void main (String args[]) throws InterruptedException
    {
        if (JOptionPane.showConfirmDialog(null, "Singleplayer mode?") == 0)
        {
            single = true;
            game.CreateList();
        }
        
        //SERVER SIDE
        else if (JOptionPane.showConfirmDialog(null, "Do you want to run server?") == 0)
        {
            socketServer = new GameServer();
            socketServer.start();
            Thread.sleep(5000);
            server = true;
            if (JOptionPane.showConfirmDialog(null, "Heads(Yes) or Tails(No)?") == 0)
            {
                Random r = new Random();
                int flip = r.nextInt(2);
                if (flip == 1)
                {
                    JOptionPane.showMessageDialog(null,"Heads! You go first");
                    socketServer.sendData("10");
                }
                else 
                {
                    JOptionPane.showMessageDialog(null,"Tails! Enemy goes first!");
                    coinloss = true;
                }
            }
            else
            {
                Random r = new Random();
                int flip = r.nextInt(2);
                if (flip == 1)
                {
                    JOptionPane.showMessageDialog(null,"Heads! Enemy goes first");
                    coinloss = true;
                }
                else 
                {
                    JOptionPane.showMessageDialog(null,"Tails! You go first!");
                    socketServer.sendData("10");
                }
            }
        }
        else 
        {
        socketClient = new GameClient("localhost");
        socketClient.start();
        game.CreateList();
        socketClient.sendData("00"+Game.list.toString());
        //System.out.println(""+Game.list.toString());
        }
        if (server)
        {
            System.out.println("Waiting for client!"); 
            Thread.sleep(5000);
        }
        
        GUI gui = new GUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.setSize(700, 300);
        if (coinloss)
        {
            turn.setText("Enemy Turn!");
            disableButtons();
        }
    }
}
