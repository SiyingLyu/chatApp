import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	// user name from client to the server
	static ArrayList<String> userNames = new ArrayList<String>(); 
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	public static void main(String[] args) throws Exception {
		System.out.println("Waiting for clients...");
		ServerSocket ss = new ServerSocket(9806);
		
		while(true) {
			Socket soc = ss.accept();
			System.out.println("Connection established");
			/***
			 * multi-client threads: one thread
			 */
			ConversitionHandler handler = new ConversitionHandler(soc);
			handler.start();
		}
	}
}

class ConversitionHandler extends Thread{
	/***
	 * on this socket, the thread is created
	 */
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String name;
	PrintWriter pw; // for log
	static FileWriter fw;
	static BufferedWriter bw;
	
	public ConversitionHandler(Socket socket) throws IOException {
		this.socket = socket;
		this.fw = new FileWriter("/Users/sallylyu/Desktop/chatApplog.txt", true);
		this.bw = new BufferedWriter(fw);
		this.pw = new PrintWriter(bw,true);
	}
	
	public void run() {
		/**
		 * thread logic
		 */
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			int count = 0;
			while (true) {
				if (count > 0) {
					out.println("NAMEALREADYEXISTS");
					//break; ? can be handle at client side
				} else {
					out.println("NAMEREQUIRED");
				}
				
				name = in.readLine();
				
				if (name == null) {
					return;
				}
				
				if (!Server.userNames.contains(name)) {
					Server.userNames.add(name);
					break;
				}
				count ++;
			}
			out.println("NAMEACCEPTED"+name);
			Server.printWriters.add(out);
			
			// read the message from client and send to other clients
			while (true) {
				String message = in.readLine();
				if (message == null) {
					return;
				}
				
				pw.println(name + ": " +message);
				
				for (PrintWriter writer : Server.printWriters) {
					writer.println(name + ": " +message);
				}
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}