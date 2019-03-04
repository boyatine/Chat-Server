import java.net.*;
import java.io.*;

public class CCCServerThread extends Thread {  
	private ChattyChatChatServer server;
    private Socket socket;
    private String ID;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;
   
    public CCCServerThread(ChattyChatChatServer _server, Socket _socket) {  
    	super();
    	server = _server;
    	socket = _socket;
    	ID = Integer.toString(socket.getPort());
   }
    
    public void send(String msg) {   
    	try {  
    		streamOut.writeUTF(msg);
    		streamOut.flush();
    	} 
    	catch(IOException ioe) {  
    		System.out.println(ID + " ERROR sending: " + ioe.getMessage());
    	}
   }
    
   public String getID() {  
	   return ID;
   }
   
   public void setID(String _ID) {
	   ID = _ID;
   }
   
   public void run() {  
	   System.out.println("Server Thread " + ID + " is running.");
	   
	   while( true ) {  
		   try {  
			   server.handle(ID, streamIn.readUTF());
		   }
		   catch(IOException ioe) {  
			   System.out.println(ID + " client has disconnected: " + ioe.getMessage());
			   server.remove(ID);
			   stop();
		   }
	   }
   }
   
   public void open() throws IOException {  
	   streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
       streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }
   
   public void close() throws IOException {  
	   if (socket != null)    
		   socket.close();
       if (streamIn != null)  
    	   streamIn.close();
       if (streamOut != null) 
    	   streamOut.close();
   }
}