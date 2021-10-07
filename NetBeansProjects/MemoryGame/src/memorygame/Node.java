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
public class Node {
    char letter;
    Node next;
    Node bottom;
    int flag;

    public Node(char Letter, int Flag, Node Next, Node Bottom) {
        letter = Letter;
        flag = Flag;
        next = Next;
        bottom = Bottom;
    }
  
    public char getData()
    {
        return letter;
    }
    
    public int getFlag()
    {
        return flag;
    }

    public Node getNext()
    {
        return next;
    }
    
    public Node getBottom()
    {
        return bottom;
    }
    
    public void setLetter(char x)
    {
        letter = x;
    }
    
    public void setFlag(int x)
    {
        flag = x;
    }
    
    public void setNext(Node x)
    {
        next = x;
    }
    
    public void setBottom(Node x)
    {
        bottom = x;
    }
    
}
