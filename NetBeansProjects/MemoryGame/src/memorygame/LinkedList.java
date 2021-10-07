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

import javax.swing.JOptionPane;

public class LinkedList {
    private Node head;
    private int size;
    
    public LinkedList()
    {   head = null;
        size = 0;
    }
    
    public LinkedList(Node n)
    {   head = n;
        size = 1;
    }
    
    public boolean isEmpty()
    {   return size == 0;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public void addFirst(Node n)
    {   if (!isEmpty())
           n.setNext(head);
        head = n;
        size++;
    }
    
    //Bottommost last
    public void addLast(Node n)
    {   if (isEmpty())
            addFirst(n);
    
        else 
        {   Node tmp = head;
            while (tmp.getBottom() != null)
            {
                 tmp = tmp.getBottom();
            }
            while (tmp.getNext() != null)
            {
                tmp = tmp.getNext();
            }   
            tmp.setNext(n);
            size++;
        }
    }
    
    public void addBottom(Node n, int x)
    {
        if (isEmpty())
            addFirst(n);
        else
        {
           Node tmp = head;
           int i = 0;
            while (tmp.getNext() != null && i != x)
                tmp = tmp.getNext();
            tmp.setBottom(n);
            size++;
        }
    }
    
    
    public char searchCoordinates(int x, int y)
    {
        //System.out.println(""+x+","+y);
        Node tmp = head;
//        System.out.println("head"+head.getData());
        while (tmp != null){
        for (int i = 0; i < x; i++)
        {
            //System.out.println(""+tmp.getBottom().getData());
            tmp = tmp.bottom;
        }
        for (int i = 0; i < y; i++)
        {
            tmp = tmp.next;
        }
        tmp.flag = 1;
        return tmp.getData();
        }
        return 'a';
    }
    
    public char SettoMatched(int x, int y)
    {
        //System.out.println(""+x+","+y);
        Node tmp = head;
        //System.out.println("head"+head.getData());
        while (tmp != null){
        for (int i = 0; i < x; i++)
        {
            //System.out.println(""+tmp.getBottom().getData());
            tmp = tmp.bottom;
        }
        for (int i = 0; i < y; i++)
        {
            tmp = tmp.next;
        }
        tmp.flag = 2;
        return tmp.getData();
        }
        return 'a';
    }
    
    //Check if flag is 2
    public boolean IsMatched(int x, int y)
    {
        //System.out.println(""+x+","+y);
        Node tmp = head;
       //System.out.println("head"+head.getData());
        while (tmp != null)
        {
            for (int i = 0; i < x; i++)
            {
                //System.out.println(""+tmp.getBottom().getData());
                tmp = tmp.bottom;
            }
            for (int i = 0; i < y; i++)
            {
                tmp = tmp.next;
            }
            if (tmp.flag < 2)
                return false;
            else
                return true;
        }
        return false;
    }
    
        //print linkedlist recursively
    public void printLinkedList()
    {   printRecur(head);
    }
    
    private void printRecur(Node n)
    {   if (n!=null)
        {   
            System.out.print(n.getData()+ " ");
            printRecur(n.getNext());
            printRecur(n.getBottom());
        }
    }
    
    public boolean pairup(String x, String y)
    {
        String[] lines = x.split(",");
        String[] lines2 = y.split(",");
        char first = searchCoordinates(Integer.parseInt(lines[0]),Integer.parseInt(lines[1]));
        char second = searchCoordinates(Integer.parseInt(lines2[0]),Integer.parseInt(lines2[1]));
        if (first == second)
        {
            return true;
        }
        else
            return false;
    }

    public int returnRecPairs() 
    {   
        return (int) findRecur(head);
    }
    
    public double findRecur(Node n)
    {   
        if (n!= null)
        {
            if (n.getFlag() == 2)
            {   
                return (0.5 + findRecur(n.getNext()) + findRecur(n.getBottom()));
            }
            return findRecur(n.getNext()) + findRecur(n.getBottom());
        }
        return 0;
    }
    


}
