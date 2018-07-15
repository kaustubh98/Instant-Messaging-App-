import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {
	private JTextField userInput;
	private JTextArea ChatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server() {
		super("Instant Messenger");
		userInput = new JTextField();
		userInput.setEditable(false);
		userInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userInput.setText("");
			}
			
		});
		add(userInput,BorderLayout.SOUTH);
		ChatWindow = new JTextArea();
		ChatWindow.setEditable(false);
		add(new JScrollPane(ChatWindow));
		
		setSize(300,150);
		setVisible(true);
	}
	
	//run and chat
	public void startRunning() {
		try {
			server = new ServerSocket(6789,100);
			while(true) {
				try {
					WaitForConnection();
					setUpStreams();
					WhileChatting();
				}catch(EOFException eof) {
					showMessage("\n Server Ended Connection");
				}finally {
					closeCrap();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//wait for someone to connect
	private void WaitForConnection() throws IOException {
		showMessage("Waiting for connection... \n");
		connection = server.accept();
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	//set up streams to send and receive data
	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now set up \n");
	}
	
	//while chatting
	private void WhileChatting() throws IOException {
		String message = "You are connected";
		sendMessage(message);
		ableToType(true);
		do {
			try {
			message = (String) input.readObject();
			showMessage("\n"+message);
			}catch(ClassNotFoundException e) {
				showMessage("User sent something bad");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	private void closeCrap() {
		showMessage("\n Closing connection... \n");
		ableToType(false);
		try {
		output.close();
		input.close();
		connection.close();
		}catch(IOException io) {
			io.printStackTrace();
		}
	}
	
	private void sendMessage(String mes) {
		try {
			output.writeObject("\n SERVER - " + mes);
			output.flush();
			showMessage("\n SERVER - " + mes);
		}catch(Exception e) {
			ChatWindow.setEditable(true);
			ChatWindow.append("\n ERROR IN SENDING THE MESAGE");
			ChatWindow.setEditable(false);
		}
	}
	
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {

					@Override
					public void run() {
						ChatWindow.setEditable(true);
						ChatWindow.append(text);
						ChatWindow.setEditable(false);
					}
					
				}
		);
	}
	
	//let the user type or not depending on the situation
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				userInput.setEditable(tof);
			}
			
		});
	}
}
