import java.net.*;
import java.io.*;

public class ChattyChatChatClient implements Runnable {  
	private Socket socket;
    private Thread thread;
    private DataInputStream console;
    private DataOutputStream streamOut;
    private CCCClientThread client;

    public ChattyChatChatClient(String serverName, int serverPort) {  
    	System.out.println("Connecting...");
      
    	try {  
    		socket = new Socket(serverName, serverPort);
    		System.out.println("Connected: " + socket);
    		start();
    	} 
    	catch(UnknownHostException uhe) { //
    		System.out.println("Host unknown: " + uhe.getMessage()); 
    	} 
    	catch(IOException ioe) {  
    		System.out.println("Unknown exception: " + ioe.getMessage()); }
    }
    
    @SuppressWarnings("deprecation")
	public void run() {  
    	while( thread != null ) {  
    		try {  
    			// Get the message into a string
    			String msg = console.readLine();
    			// Send it through the socket (just like your code does)
    			streamOut.writeUTF(msg);
    			streamOut.flush();
    			// Now check if msg.equals("/quit"), and shut down the client process if that's the case
    			if( msg.equals("/quit") ) {
    				return;
    			}
    		} 
    		catch(IOException ioe) {  
    			System.out.println("Sending error: " + ioe.getMessage());
    		}
    	}
    }
   
    public void handle(String message) {  
    		System.out.println(message);
    }
    
    public void stop() {
    	if (thread != null) {
    		thread.stop();  
    		thread = null;
    	}
    	
    	try {  
    		if( console != null )  
    			console.close();
    		if( streamOut != null )  
    			streamOut.close();
    		if( socket != null )  
    			socket.close();
    	} catch(IOException ioe) {  
    		System.out.println("Error closing ..."); 
    	}
      
    	client.close();  
    	client.stop();
    }
    
    public void start() throws IOException { 
    	console = new DataInputStream(System.in);
    	streamOut = new DataOutputStream(socket.getOutputStream());
     
    	if( thread == null ) {
    		client = new CCCClientThread(this, socket);
    		thread = new Thread(this);                   
    		thread.start();
    	}
    }
    
    public static void main(String args[]) {
    	ChattyChatChatClient client;
      
    	if( args.length != 2 )
    		System.out.println("Usage: java ChattyChatChatClient <host port>");
    	else
    		client = new ChattyChatChatClient(args[0], Integer.parseInt(args[1]));
   }
}