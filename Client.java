import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	private JTextField UserIn;
	private JTextArea chatwindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String ServerIP;
	private Socket connection;
	
	public Client(String host) {
		super("Instant Messenger");
		ServerIP = host;
		UserIn = new JTextField();
		UserIn.setEditable(false);
		UserIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				UserIn.setText("");
			}
			
		});
		add(UserIn,BorderLayout.SOUTH);
		chatwindow = new JTextArea();
		add(new JScrollPane(chatwindow),BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,150);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		}catch(EOFException e) {
			showMessage("\n Client terminated connection");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			closeCrap();
		}
	}
	//connect to server
	private void connectToServer() {
		showMessage("Attempting connection... \n");
		try {
			connection = new Socket(InetAddress.getByName(ServerIP),6789);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showMessage("Connected to: "+connection.getInetAddress().getHostName());
	}
	//setup streams
	private void setUpStreams() {
		try {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("You are good to go! \n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException e) {
				showMessage("Object not determined");
			}
		}while(!message.equals("SERVER - END"));
	}
	//closing stuff
	private void closeCrap() {
		showMessage("\n Closing down connection..");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	//send the message
	private void sendMessage(String mes) {
		try {
			output.writeObject("CLIENT - " + mes);
			output.flush();
			showMessage("\nCLIENT - " + mes);
		} catch (IOException e) {
			chatwindow.append("\nSomething went wrong..");
			e.printStackTrace();
		}
	}
	//show message to chat window
	private void showMessage(String m) {
		SwingUtilities.invokeLater(
				new Runnable() {

					@Override
					public void run() {
						chatwindow.append(m);
					}
					
				}
		);
	}
	//permission for the user to type
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				UserIn.setEditable(tof);
			}
			
		});
	}
}
