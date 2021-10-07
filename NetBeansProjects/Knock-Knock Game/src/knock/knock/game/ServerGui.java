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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ServerGui extends JFrame{
	Thread thread;
	KKMultiServer server;
	int port = 4449;
	
	// main panel
	private final JPanel mainPanel;
	
	// button panel
	private JButton[] buttons = new JButton[3];
	private final JPanel buttonPanel;
	private final JButton createClient;
	private final JButton startServer;	
	private final JButton stopServer;	
	
	// instructions panel
	private final JPanel instructionsPanel;
	private JLabel errorsLabel;
	private JLabel instructionsLabel;
	
	public ServerGui(){
		super("Server");
		
		// initialize main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1));
		
		// initialize instructions panel
		instructionsPanel = new JPanel();
		instructionsPanel.setLayout(new GridLayout(3, 1));
		errorsLabel = new JLabel("", SwingConstants.CENTER);
		instructionsPanel.add(errorsLabel);
		instructionsLabel = new JLabel("", SwingConstants.CENTER);
		instructionsPanel.add(instructionsLabel);
		mainPanel.add(instructionsPanel);
		
		// initialize button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		startServer = new JButton("Start Server");
		startServer.setVisible(true);
		buttons[0] = startServer;
		startServer.addActionListener(new ActionListener(){
			/**
			 * anonymous inner class used to start server on click and rearrange buttons
			 */
			public void actionPerformed(ActionEvent event){
				try {
					startServer();
					startServer.setVisible(false);
					stopServer.setVisible(true);
					createClient.setVisible(true);
					instructionsLabel.setText("Server running");
				} catch (Exception e) {}
				
		    }
		});
		
		stopServer = new JButton("Stop Server");
		stopServer.setVisible(true);
		buttons[1] = stopServer;
		stopServer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				/**
				 * stops server and hides create client button
				 */
				try {
					stopServer();
					instructionsLabel.setText("Server stopped");
					startServer.setVisible(true);
					stopServer.setVisible(false);
					createClient.setVisible(false);
				} catch (Exception e) {}
		    }
		});
		
		createClient = new JButton("Create Client");
		createClient.setVisible(false);
		buttons[2] = createClient;
		createClient.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				createClient();
		    }
		});
		
		for (int count = 0; count < buttons.length; count++){
			buttonPanel.add(buttons[count]);
		}		
		add(buttonPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
		
	}
		
	/**
	 *  opens view as a JFrame
	 */
	public void start(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 400);
		this.setVisible(true);
	}
	
	/**
	 * start server
	 * @throws Exception
	 */
	public void startServer() throws Exception{
		server = new KKMultiServer();
		thread = new Thread(server);
		thread.start();
	}
	
	/**
	 * stop server
	 * @throws Exception
	 */
	public void stopServer() throws Exception{
		server.listening = false;
	}
	
	public void createClient(){
    	new Thread(new KnockKnockClient()).start();
    }
	
}
