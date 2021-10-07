/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knock.knock.game;

/**
 *
 * @author ferry
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ClientGui extends JFrame{
	String fromServer;
    String fromUser;
	private final JButton[] buttons;
	private String inputString;
	private String serverMessage;
	private PrintWriter out;
	private BufferedReader in;
	
	// main panel
	private final JPanel mainPanel;
	
	// button panel
	private final JPanel buttonPanel;
	private final JButton stopButton;
	private final JButton sendButton;
	
	// input panel
	private final JPanel inputPanel;
	private final JTextField inputField;
	
	
	// instructions panel
	private final JPanel instructionsPanel;
	private JLabel errorsLabel;
	private JLabel serverMessageLabel;
	private JLabel instructionsLabel;
	
	/**
	 * Socket is passed to the gui constructor from the client so that it can be closed
	 * with the click of a button
	 * @param socket
	 */
	public ClientGui(Socket socket){
		super("Client");
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {}
		inputString = "";
		serverMessage = "";
		
		
		/**
		 *  initialize main panel
		 */
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1));
		
		/**
		 *  initialize instructions panel
		 */
		instructionsPanel = new JPanel();
		instructionsPanel.setLayout(new GridLayout(3, 1));
		instructionsLabel = new JLabel("", SwingConstants.CENTER);
		instructionsPanel.add(instructionsLabel);
		errorsLabel = new JLabel("", SwingConstants.CENTER);
		instructionsPanel.add(errorsLabel);
		serverMessageLabel = new JLabel("No server message yet!", SwingConstants.CENTER);
		instructionsPanel.add(serverMessageLabel);
		mainPanel.add(instructionsPanel);
		
		/**
		 *  initialize input panel
		 */
		inputPanel = new JPanel();
		inputField = new JTextField("", 15);
		inputPanel.add(inputField);
		mainPanel.add(inputPanel);
		
		/**
		 *  initialize button panel
		 */
		buttons = new JButton[2];
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		stopButton = new JButton("Stop");
		stopButton.setVisible(true);
		buttons[0] = stopButton;
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				/**
				 * immediately close socket and ClientGui
				 */
				try {
					out.close();
					in.close();
					socket.close();
					dispose();
				} catch (Exception e) {
					errorsLabel.setText(e.toString());
				}
		    }
		});
		
		sendButton = new JButton("Send");
		sendButton.setVisible(true);
		buttons[1] = sendButton;
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				/**
				 * send user message to server
				 */
				inputString = inputField.getText();
				System.out.println(inputString);
				out.println(inputString);
				inputField.setText("");
				serverMessage = "";
				inputString = "";
		    }
		});
		
		for (int count = 0; count < buttons.length; count++){
			buttonPanel.add(buttons[count]);
		}		
		add(buttonPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
		try {
			this.start();
		} catch (Exception e){}
		
	}
		
	/**
	 * opens view as a JFrame
	 * @throws Exception
	 */
	public void start() throws Exception{
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 400);
		this.setVisible(true);
	
			while ((fromServer = in.readLine()) != null) {
			    setServerMessageLabel("Server: " + fromServer);
			    if (fromServer.equals("Bye."))
			        break;
			}
	}

	/**
	 * @param str
	 */
	public void setInstructionLabel(String str){
		this.instructionsLabel.setText(str);
	}
	
	/**
	 * @param str
	 */
	public void setErrorsLabel(String str){
		this.errorsLabel.setText(str);
	}
	
	/**
	 * @param str
	 */
	public void setServerMessageLabel(String str){
		this.serverMessageLabel.setText(str);
	}
	
}
