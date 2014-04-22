import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author StemmRock
 */
public class instantMessengerServer {
      /**
     * @param args the command line arguments
     */
    boolean registered = true;
    boolean fail = true; //designates if login failed
    static String userList[] = new String[50]; //list of users, obtained from login.txt
    static int status[] = new int [50]; //status of users logged in;
    
    public static void main(String[] args) {
		instantMessengerServer server = new instantMessengerServer();

    }

    /**
     * Constructor for the instantMessengerServer class.  Opens a ServerSocket to 
     * accept incoming socket connections.  Starts a new thread for each 
     * connection which reads the data and sends an ok when done.
     */
    public instantMessengerServer() {
		System.out.println("MyServer getting ready to accept connections...");
		// Open a ServerSocket to accept Socket connections.
		ServerSocket incomingConnections;
                for(int x=0;x<status.length;x++){
                            status[x]=0;
                        }
		try {
			incomingConnections = new ServerSocket(2431);
			while (true) {
				// Accept connections.
				Socket incomingSocket = new Socket();
				System.out.println("Accepting connections...");
				try {
					incomingSocket = incomingConnections.accept();
					System.out.println("     Connection made...");
					CommThreadListener listener = new CommThreadListener(incomingSocket);
					listener.start();
				}
				catch (Exception e) {
					// Do nothing.  Just let me know there was a failure.
					System.out.println("Failed to make connection.");
				}
			}
		}
		catch (Exception e) {
			// We can't get connections because we couldn't set up the 
			// ServerSocket.  Terminate the program.
			System.out.println("Failed to set up ServerSocket.  Exiting.");
			System.exit(1);
		}
    }


    /**
     * Listens to the Socket that the client and the server use to communicate.
     */
    class CommThreadListener extends Thread {
		Socket s;

		public CommThreadListener(Socket s) {
			this.s = s;
			System.out.println("     Reading from connection...");
		}

		public void run() {
                    
			System.out.println("Running receive thread...");
			// Declare file and reader/writer variables here so we have access to them if we hit an exception.			
                        boolean login = false; //used to show that a user has logged onto the server
                        boolean messaging = false; //denotes if the user is sending messages to another user
			BufferedReader in = null; //reads output from the client
			PrintWriter writer = null; //sends output from server to the client
                        String username = ""; //string value for username obtained from client, used for logging in and other things
                        String password = ""; // string value signifing a password associated to a username, used for log in
                        String message = ""; //message to be sent across the server to another user
                        String mUser = ""; //string value for the username a user is sending messages to.
                        String sUser = ""; //username whose status is being checked
                        int onOrOff = 0; //designator for whether or not a user is online.
                        
                       
                        
			try {
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				writer = new PrintWriter(s.getOutputStream(), true);
                                
				
						StringBuffer path = new StringBuffer("");
				boolean done = false;
				String str;
                               
                    while ((str = in.readLine()) != null) {
                                str = str.trim();
                               
                               
                                
                                
//==============================================================================
        //initialize connection and login
                                
                                if(str.equals("Hello")){
                                   userList();
                                    writer.println("What is your username?");
                                    
                                }
                                else if(login == false && username.equals("")) { //makes next message sent from client the username if there is no
                                    username = str;                             //current value for the username
				    
				 if (str.toLowerCase().equals("register")){
					registered = false;
					writer.println("username?");
					str = in.readLine();
					username = str;
					if(username.equals("")){
						writer.println("username invalid");
					}
					else{
					writer.println("password?");
					str = in.readLine();
					password = str;
					register(username, password);
					userList();
					writer.println("continue");
					}
				    }
				   else{
                                    writer.println("What is your password?");
				    }
                                }
                                else if(login == false && !(username.equals(""))){ //makes next message sent from client the password
                                    if(registered == true){
				    password = str;                                   
                                    }
                                    if(login(username,password)){   //checks to see if valid credentials, if so, logs in
                                        writer.println("logged in");
                                        changeStatus(username,"login");
                                        login = true;
					registered = true;
                                    }
                                    else{
                                        writer.println("fail");
                                        username = "";
                                        password = "";
                                        login = false;
                                    }
                                    
                                }
                                //end login
                                

//==============================================================================
if(login == true){ //only does these operations if logged in.
                               
                               if(str.equals("message")){ //client signals that it wishes to send a message
                                   messaging = true;
                                   writer.println("who"); //server asks who
                               }
                               //end message
//==============================================================================                               
                               
                               if(str.equals("person")){ // client sends a username of who they wish to message
                                 
                                   mUser = in.readLine();
                                   mUser = mUser.trim();
                                   if(!userExists(mUser)){ //checks if the user exists, if not tells client.                                    
                                       writer.println("fail2");
                                   }
                                   else{
                                   onOrOff = findStatus(mUser); //if the users exists, the server then checks if they are online.
                                   if(onOrOff > 0){   //online
                                  writer.println("begin"); //tells client to begin sending messages
                                
                                   }
                                   else{ //offline
                                       writer.println("mesOff"); //tells client user is offline
                                   }
                                   }
                               }
                               
                               //end message initiation
//==============================================================================
                               
                               if(str.equals("sendThis")){ //client sends message to server, to be sent to other user.
                                   try{
                                       
                                   BufferedWriter mwriter = new BufferedWriter(new FileWriter(mUser + ".txt")); //messages are written to a .txt file
                                   message = in.readLine();                                                     //then read and deleted by the other user
                                    onOrOff = findStatus(mUser);    //checks if user is online so as to write to the online .txt file
                                   if(onOrOff > 0){//online                                   
                                        if(message.toLowerCase().equals("exit")){ //if user typed exit by itself, messaging ends
                                       
                                       mwriter.close();
                                       writer.println("logged in");
                                   }
                                   else{
                                   mwriter.write("\n" + username + ": "+ message); //message format, username: message text                                  
                                   mwriter.close();
                                   writer.println("idle"); //puts the client into a pseudo idle state to wait for messages
                                   }
                                   }
                                   }
                                   catch(IOException e){}
                                   if(onOrOff == 0){ //if offline, needs to know so as to write to the "away" text file, also in this case user was online.
                                       try{
                                        BufferedWriter mwriter2 = new BufferedWriter(new FileWriter(mUser + "Away.txt",true)); //chooses to write to the
                                                                                                                               //existing away.txt file             
                                        if(message.toLowerCase().equals("exit")){ //if exit, stops/cancels message
                                       
                                       mwriter2.close();
                                       writer.println("logged in");
                                   }
                                   else{
                                   mwriter2.write("\n" + username + ": "+ message); //message format
                                   
                                   
                                   mwriter2.close();
                                   writer.println("away"); //since user was online at start of conversation, server notifies client that that is no longer the case
                                   }
                                   }
                                   
                                   catch(IOException e){}
                               }
                                   }
                                                                                                                                                                            
                                   
                                   
                               
                                 //end send message to online user
//==============================================================================                               
                               
                               if(str.equals("sendThis2")){ //send a message to an offline user
                                   try{
                                   
                                   BufferedWriter mwriter2 = new BufferedWriter(new FileWriter(mUser + "Away.txt",true)); //writes to away file
                                   message = in.readLine();
                                   if(message.toLowerCase().equals("exit")){ //cancels message
                                       
                                       mwriter2.close();
                                       writer.println("logged in");
                                   }
                                   else{
                                   mwriter2.write("\n" + username + ": "+ message); //message format
                                   
                                   
                                   mwriter2.close();
                                   writer.println("logged in"); //brings client back to main menu
                                   }
                                   }                                                                                                                                          
                                   catch(IOException e){}
                               }
                         
//============================================================================== 
                               
                               
                               if(str.equals("delete")){ //deletes messages once they are viewed
                            BufferedWriter br = new BufferedWriter(new FileWriter(username + ".txt"));
                                br.write("");
                                br.close();
                            }
                               
                               if(str.equals("delete2")){ //deletes away messages once user logs in
                                   BufferedWriter br = new BufferedWriter(new FileWriter(username + "Away.txt"));
                                   br.write("");
                                   br.close();
                               }
                               
//==============================================================================                               
                               
                               
                               if(str.equals("exit") && messaging == true){ //ends messaging
                                   messaging = false;
                                   writer.println("logged in");
                               }
                               
//==============================================================================                               
                               
                               if(str.equals("waiting")){ //client states it is waiting for messages
                                   writer.println("idle"); //server tells client to stay idle
                               }
                               
//==============================================================================                               
                               if(str.equals("status")){ //checks status of users on friends list (could work for all users if need be)
                                   sUser = in.readLine();
                                   onOrOff = findStatus(sUser);
                                   if(onOrOff == 0){
                                       writer.println("offline");
                                   }
                                   else if(onOrOff > 0){
                                       writer.println("online");
                                   }
                                  
                                   
                               }
                               
//=============================================================================
                               
                               if(str.equals("done")){ //client is finished a task (messaging, or status);
                                   writer.println("logged in");
                               }
                               if(str.equals("done2")){ //client is finished messaging one person and wishes to message a different person
                                   writer.println("who");
                               }
                               
                              
}
                    
                    }
                                
					
                                changeStatus(username,"logout"); //updates users status when they logout
				System.out.println("Closing socket!!!!!!!!!!");
				// Close the socket.
				s.close();
			}
			catch (Exception e) { 
				// Send a failure and close the socket.  
				System.err.println(e);
				try {
					s.close();
				}
				catch (Exception exception) {
					// There's nothing more we can do here.  Just let me know there was an error.
					System.out.println("Couldn't recover from error.");
				}
			}
			System.out.println("Thread exiting.");
                        
                }
    }
	
//==============================================================================    
    
    
    /**
     * This function is utilized when a user tries to login to the server.
     * It returns a boolean value expressing if login was successful.
     * 
     * @param u This is a string value representing the username.
     * @param p This is a string value representing the password.
     * @return Returns a boolean value of true if login was success, else false;
     * @throws IOException Throws an exception if the file is not found.
     */
    public static boolean login(String u,String p)throws IOException{
        try{
            FileInputStream login = new FileInputStream("login.txt");
        String user;
        BufferedReader br = new BufferedReader(new InputStreamReader(login));
            while(br.readLine()!=null){
             do{   
                user = br.readLine();
                //System.out.println(user);
                
                if(user == null){
                    System.out.print("here");
                    return false;
                }
                if(user.trim().charAt(0) != '#'){
                    String uAndp[] = user.split("\\s+");
                    
                    if(u.equals(uAndp[0])&&p.equals(uAndp[1].trim())){
                       
                        return true;
                    }
                }
            }while(true);
             
        }
            br.close();
        }
        catch(FileNotFoundException e){
        
        }
        
            return false;
}
//==============================================================================
public static void  register(String username, String password){
	try{
	File f = new File("login.txt");
	if(f.exists() && !f.isDirectory()){
         BufferedWriter mwriter2 = new BufferedWriter(new FileWriter("login.txt",true)); //chooses to write to the
//existing login.txt file
         mwriter2.write(username + " " + password + "\n"); //message format         
         mwriter2.close();
         }
	else{
	 BufferedWriter mwriter2 = new BufferedWriter(new FileWriter("login.txt"));
	 mwriter2.write("username password\n");
	 mwriter2.write(username + " " + password + "\n");
	 mwriter2.close();
	}
	}
					
         catch(IOException e){}

	try{
	BufferedWriter mwriter2 = new BufferedWriter(new FileWriter(username+"Friends.txt"));
	mwriter2.write("\n");
	mwriter2.write(username + "\n");
	mwriter2.close();
	}
	catch(IOException e){}
	}

    	    
//==============================================================================    
    
    
/**
 * This function populates an array with the usernames of all of the users
 * that are "registered" to the system. It takes all of the usernames stored
 * in the login.txt file and populates the array.
 */    
public static void userList(){
   
    String username;
    int x = 0;
    int y = 0;
    try{
    FileInputStream login2 = new FileInputStream("login.txt");
    BufferedReader br2 = new BufferedReader(new InputStreamReader(login2));
            while(br2.readLine()!=null){
             do{   
                username = br2.readLine();
                //System.out.println(user);
                
                if(username == null){
                    break;
                }
                if(username.trim().charAt(0) != '#'){
                    String uAndp[] = username.split(" ");
                    userList[x] = uAndp[0];
                    x++;
                        
                    }
                
            }while(true);
             
        }
           
            br2.close();
}
    catch(IOException e){System.out.println("userList not populated");}
}

//==============================================================================

/**
 * This function changes a users status on the server. Status is denoted by an 
 * integer value If a user is logged in or logged in from multiple locations 
 * their status will be greater than 0. If they are logged in from multiple 
 * locations and one instance logs out, then the status is lowered by 1. If 
 * status is 0, user is considered logged out.
 * @param user Username of user who's status is changing.
 * @param operation String value representing whether the user is logging in or out.
 */

public static void changeStatus(String user,String operation){
    for(int x=0;x<userList.length;x++){
	System.out.println(userList[x]);
        if(user.equals(userList[x]) && operation.equals("login")){
            status[x] = status[x]+1;
            
        }
        else if(user.equals(userList[x]) && operation.equals("logout")){
            
            status[x] = status[x] - 1;
            
           
        }
    }
    
   
}

//==============================================================================

/**
 * This function returns the integer value of a users status on the server.
 * @param sUser String value representing the username of the user who's status is being checked.
 * @return Integer value for status if the user is found else, print error and return -1.
 */
public static int findStatus(String sUser){
    
    for (int x=0;x<userList.length;x++){
	System.out.println(sUser);
	System.out.println(userList[x]);
	System.out.println(status[x]);
        if(userList[x] == null){
            System.out.println("user not found");
            return -2;
        }
        if(sUser.equals(userList[x])){
            return status[x];
        }
        
    }
    return 2;
}

//==============================================================================

/**
 * This functions returns a boolean value representing if a user exists or not.
 * If the user exists it returns true, else it returns false.
 * @param user String value representing username being checked
 * @return Boolean true if user exists, false if not.
 */
public static boolean userExists(String user){
    for(int x = 0;x<userList.length;x++){
        if(userList[x] == null){
            return false;
        }
        if(user.equals(userList[x])){
            return true;
        }
        
    }
    System.out.println("failed");
    return false;
}
}

        
    

