import java.io.IOException;
import java.net.*;

public class ChattyChatChatServer implements Runnable {
	
	private CCCServerThread clients[] = new CCCServerThread[10]; // max 10 people
	private ServerSocket serverSocket;
	private Thread thread;
	private int clientCount = 0;
	
	public ChattyChatChatServer(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
			start();
		} 
		catch(IOException ioe) {
			System.out.println("Binding error: " + portNumber + ": " + ioe);
		}
	}
	
	public void run() {
		while( thread != null ) {
			try {
				addThread(serverSocket.accept());
			} 
			catch(IOException ioe) {
				System.out.println("Socket acceptance error: " + ioe);
			}
		}
	}
	
	public void start() {
		if (thread == null) {  
			thread = new Thread(this); 
	        thread.start();
	        System.out.println("Server is running");
	      }
	}
	
	private int find(String ID) {  
		for( int i = 0; i < clientCount; i++ )
	         if( clients[i].getID() == ID )
	            return i;
	      return -1;
	   }
	
	public synchronized void handle(String ID, String input) {  
		String[] array = input.split("\\s");
			
		if( input.equals("/quit") ) {  
			for( int i = 0; i < clientCount; i++ )
				clients[i].send(ID + " has logged off.");
	        remove(ID); 
	    } 
		else if( array[0].equals("/nick") && array.length == 2 ) {
			CCCServerThread changeID = clients[find(ID)];
			changeID.setID(array[1]);
		}
		else if( array[0].equals("/dm") && array.length >= 3 ) {
			for( int i = 0; i < clientCount; i++ )
	            if (clients[i].getID().equals(array[1])) {
	            	clients[i].send(ID + " (direct message): ");
	            	
	            	final int index = i; // need this for inner for loop
	            	
	            	for( int j = 2; j < array.length; j++ ) {
		            	clients[index].send(array[j]);
	            	}
	            }
		}
		else {
	    	for( int i = 0; i < clientCount; i++ )
	            clients[i].send(ID + ": " + input);   
	   }
	}
	
	public synchronized void remove(String ID) {  
		int position = find(ID);

		if( position != -1 ) {  
			CCCServerThread kill = clients[position];
			
	        if( position < clientCount - 1 )
	        	for( int i = position + 1; i < clientCount; i++ )
	            	clients[i - 1] = clients[i];
	        
	        clientCount--;
	        
	        try {  
	        	kill.close();
	        } 
	        catch(IOException ioe) {  
	        	System.out.println("Error closing thread: " + ioe);
	        }
		}
	}
	
	private void addThread(Socket socket) {  
		if( clientCount < clients.length ) {  
			System.out.println("Client connected: " + socket);
			clients[clientCount] = new CCCServerThread(this, socket);
			
			try {  
				clients[clientCount].open(); 
	            clients[clientCount].start();  
	            clientCount++;
			} 
			catch(IOException ioe) {  
				System.out.println("Error opening thread: " + ioe); 
			} 
		} 
		else
	         System.out.println("Client cannot connect: maximum number of clients reached.");
	}
		
	public static void main(String args[]) {  
		ChattyChatChatServer server;
		
		if( args.length != 1 )
			System.out.println("Usage: java ChatServer <port number>");
		else
			server = new ChattyChatChatServer(Integer.parseInt(args[0]));
	   }
	

}
