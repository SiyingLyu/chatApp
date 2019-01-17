import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
public class Client {
	// GUI part
	static JFrame chatWindow = new JFrame("Chat Application");
	static JTextArea chatArea = new JTextArea(22, 38); // size of chat area (row, col)
	static JTextField textField = new JTextField(38);
	static JLabel blankLabel = new JLabel("           ");
	static JButton sendButton = new JButton("Send");
	// function part
	static BufferedReader in;
	static PrintWriter out;
	static JLabel nameLabel = new JLabel("           ");
	
	public Client() {
		chatWindow.setLayout(new FlowLayout());
		
		chatWindow.add(nameLabel);
		chatWindow.add(new JScrollPane(chatArea));
		chatWindow.add(blankLabel);
		chatWindow.add(textField);
		chatWindow.add(sendButton);
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatWindow.setSize(475, 500);
		chatWindow.setVisible(true);
		// before server connected
		textField.setEditable(false);	
		chatArea.setEditable(false);
		
		sendButton.addActionListener(new Listener());
		
		//by pressing enter, the message send as well
		textField.addActionListener(new Listener());
	}
	
	public void startChat() throws Exception {
		/**
		 * ask the user to enter the IP address of the server (dialog pops)
		 */
		String ipAddress = JOptionPane.showInputDialog(
				chatWindow,
				"Enter IP Address:",
				"IP Address Required!!",
				JOptionPane.PLAIN_MESSAGE);  
		Socket soc = new Socket(ipAddress, 9806);
		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		out = new PrintWriter(soc.getOutputStream(), true);
		
		while (true) {
			// read data from server
			String str = in.readLine(); // first is namerequried
			if (str.equals("NAMEREQUIRED")) {
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter a unique name:",
						"Name Required!!",
						JOptionPane.PLAIN_MESSAGE);
				out.println(name);
			} else if (str.equals("NAMEALREADYEXISTS")) {
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter another name:",
						"Name Already Exists!!",
						JOptionPane.WARNING_MESSAGE);
				out.println(name);
			} else if (str.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
				nameLabel.setText("You are logged in as: " + str.substring(12));
			} else {
				chatArea.append(str+"\n"); // some chatting content
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		/**
		 * Create GUI here
		 */
		Client client = new Client();
		client.startChat();
	}
}

class Listener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Client.out.println(Client.textField.getText());//send to outstream
		Client.textField.setText("");// clean the text field
	}
	
}