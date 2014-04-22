import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author StemmRock
 */
public class InstantMessengerClient implements Runnable{
static String answer = ""; //users answer
Scanner input = new Scanner(System.in); //scanner to get user input.
static boolean done = false;
static String friendsList[] = new String[50];
static boolean menu = true;

	public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in); //scanner to get user input.
        boolean loggedin = false; //used to show user is logged in
        String username = ""; //string for the user's username
        Socket mySocket = null; //Socket
        PrintWriter outBound = null; //writes messages to server
        BufferedReader in = null; //reads messages from server
        boolean messaging = false; //denotes whether messaging or not
        boolean idle = false; //denotes whether idle or not
        int hacky = 0; //used as a counter for finding status of users
        boolean stats = false; //denotes if status is being invoked or not
        String mUser = ""; //string value representing username of user be messaged.
        
        
 
        try {
            mySocket = new Socket("rosemary.umw.edu", 2431);
            outBound = new PrintWriter(mySocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        } 
		catch (UnknownHostException e) {
            System.err.println("I couldn't find the host rosemary.umw.edu.");
            System.exit(1);
        } 
		catch (IOException e) {
            System.err.println("I/O exception with rosemary.umw.edu.");
            System.exit(1);
        }
 
        //======================================================================
        
        String fromServer;//string value representing messages from the server
 
        outBound.println("Hello"); //initialize connection with server.
		        
//		System.out.println("Sent msg... waitng for response...");
		while ((fromServer = in.readLine()) != null) {
                    
                    //end initialize
//==============================================================================
                   //this area recieves messages sent while user is idle during messaging
                   //basically client is in idle during this time.
                    
                    if(loggedin == true && idle == true && fromServer.equals("idle")){ 
                        
                        Thread mess = new Thread(new InstantMessengerClient()); //new thread to get input while system searches for messages
                        mess.start();
                       done = true;
                       menu = true;
                        while(answer.equals("")){
                        if(!(isEmpty(username).equals("empty"))){ //looking for text in message file, if found, displays and deletes
                            System.out.println();
                            getMessages(username);
                            System.out.println();
                            outBound.println("delete");
                            System.out.print("");
                            try{
                            Thread.sleep(50);
                            }
                            catch(InterruptedException ie){}
                        }                
                      
                        }
                       
                }
                    
                    //end idle recieve messages
//==============================================================================
                    
      //This is the client side login              
                    if(fromServer.equals("username?")){
			System.out.println();
			System.out.println("What would you like your username to be?");
			System.out.println();
			answer = input.nextLine();
			username = answer;
			outBound.println(answer);
 			}
                    if(fromServer.equals("What is your username?")){ // server asks for username
                        System.out.println();
                        System.out.println("Please type your username or type register to register an new user");
                        System.out.println();
                       // System.out.println(fromServer);
                       // System.out.println();
                        answer = input.nextLine();
                        username = answer;
                        outBound.println(answer);   //client asks user to input username and sends it to the server
                    }
		    if(fromServer.equals("password?")){
			System.out.println();
			System.out.println("What would you like your password to be?");
			System.out.println();
			answer = input.nextLine();
			outBound.println(answer);
			}
                    if(fromServer.equals("What is your password?")){ //server asks for password
                        System.out.println();
                        System.out.println(fromServer);
                        System.out.println();
                        answer = input.nextLine();
			outBound.println(answer);             //client asks user to input password and sends it to server
        }
		    if(fromServer.equals("continue")){
			outBound.println("ok");
			}
                    if(fromServer.equals("logged in")){  //if login is successful, resets values, populates friends list and shows loggedin
                        loggedin = true;
                        messaging = false;
                        friendsList(username);
                        done = false;
                        idle = false;
                        
                    }
                           //end login
//==============================================================================
          
                    //This is the menu
                    
                    if(loggedin == true && messaging == false && stats == false && (!(answer.equals("try again1"))) && menu == true){
                        System.out.println();
                        System.out.println("Welcome " + username + "!");
                        
                        if(!(isEmpty2(username).equals("empty"))){ //retrieve messages sent while logged off
                            System.out.println("Here are the messages you recieved while you were away:");
                            System.out.println();                     
                            getMessages2(username); //gets away messages
                            outBound.println("delete2");                                                       
                        }                                                                   
                        else{
                            System.out.println();
                            
                        }    
                        //menu
                        System.out.println();
                        System.out.println("Please select from the following options:");
                        System.out.println("Status - Check the status (online/offline) of your friends");                        
                        System.out.println("Message - Message a user");
                        System.out.println("Logout - Logout and exit");
                        System.out.println();
                        System.out.println();
                        Thread t = new Thread(new InstantMessengerClient()); //start new thread to get input while main thread searches for messages
                        t.start();
                        
    //continues to check if the message file has text until a valid option is chosen, if so, displays the text and deletes it from the file
                        while(!(answer.equals("status")||answer.equals("message")||answer.equals("logout")||answer.equals("try again1"))){
                            if(!(isEmpty(username).equals("empty"))){
                           
                            getMessages(username);
                            outBound.println("delete");
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(InstantMessengerClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                            }   
                        }
                    }
                    
                    //end menu
//==============================================================================                    
                    
    //This block sends user input as a message when the client is in idle status
                    if(!(fromServer.equals("away")) && loggedin == true && !(answer.equals("status")||answer.equals("message")||answer.equals("logout")||answer.equals("try again1")) && idle == true){                
                        outBound.println("sendThis");
                        outBound.println(answer);
                        answer = "";
                        done = false;
                    }
                    
                    //end send message from idle
//==============================================================================                    
                    //This are checks a users status
                    
                    if(loggedin == true && answer.equals("status") && stats == false){ //tells user to prepare to accept usernames for status check
                        outBound.println("status");
                        outBound.println(friendsList[hacky]); //sends a username from the users friends list
                        hacky++;
                        stats = true;
                        
                        
                    }
                    
                    if(fromServer.equals("online")){ //server tells client user is online, client displays as such
                        System.out.println(friendsList[hacky-1] + ": Online");
                        if(friendsList[hacky]!=null){
                            outBound.println("status");
                            outBound.println(friendsList[hacky]);
                            hacky++;
                            
                        }
                        else{ // if the friends list empty then stop checking status and reset values.
                            hacky = 0;
                            stats = false;
                            answer = "";
                            if(messaging == false){
                            outBound.println("done");
                            menu = true;
                            }
                            else{
                            outBound.println("waiting");
                                menu = false;
                            }
                        }
                    }
                    
                    if(fromServer.equals("offline")){ //server tells client user is offline and client outputs this
                        System.out.println(friendsList[hacky-1] + ": Offline");
                            if(friendsList[hacky]!=null){
                            outBound.println("status");
                            outBound.println(friendsList[hacky]);
                            hacky++;
                            
                        }
                        else{ //once all friends have been checked, stops checking status.
                            hacky = 0;
                            stats = false;
                            answer = "";
                            
                             if(messaging == false){
                            outBound.println("done");
                            menu = true;
                            }
                            else{
                            outBound.println("waiting");
                                menu = false;
                            }
                        }
                        
                    }
                   
                    //end status
//==============================================================================                    
                    //This is the area in which the client tells the server it wants to message a user
                    
                    //this part is for when deciding to message a user from the menu
                    if(loggedin == true && answer.equals("message") && messaging == false){
                        System.out.println("Type the word exit by itself to stop messaging.");
                       messaging = true;
                       done = true;
                        outBound.println(answer);
                    }
                    
                    //this is used when changing user to message while messaging another user
                    if(loggedin == true && answer.equals("message") && messaging == true && fromServer.equals("idle")){
                        outBound.println(answer);
                        idle = false;
                    }
                    
                    //end messaging
//==============================================================================                    
                    //this block designates the user to be messaged
                    
                    if(fromServer.equals("who")){ //server request recipient username from client
                        System.out.println();
                        System.out.println("Who do you wish to message"); //client requests input from user
                        System.out.println();
                        answer = input.nextLine();
                        mUser = answer;
                        outBound.println("person"); //client sends username to server.
                        outBound.println(answer);
                    }
                    
                    //end who
//==============================================================================                    
                    //This block tells the client the server is ready to recieve messages
                    
                    if(fromServer.equals("begin")){ //server tells client ready
                        System.out.println();
                        System.out.println("Send message to " + answer); //client tells user to begin messaging                  
                        idle = true;                       //system get put into idle status so that it recieves messages while messaging
                        answer = input.nextLine();
                        outBound.println("sendThis");                     
                        outBound.println(answer);
                        answer = "";
                       
                    }
                    
                    //end begin messaging
//==============================================================================                    
                    //this is the client side logout
                    
                    if(loggedin == true && answer.equals("logout")){                       
                        	break; //breaks loop and closes resources and socket.
                    }
                    
                    //end logout
//==============================================================================                    
                    //login fail
                    
                    if(fromServer.equals("fail")){ //server tells client login failed
                        System.out.println();
                        System.out.println("Incorrect username or password, please try again."); //client tells user login failed
                        System.out.println();
                        outBound.println("Hello"); //client starts over
                        }
                    
                    //end login fail
//==============================================================================                    
                    //server tells client that the user to be messaged is offline
                    
                    if(fromServer.equals("mesOff")){ //server tells client
                        System.out.println();
                        System.out.println(answer + " is offline, leave a message for this user."); //client tells user  
                        System.out.println();
                        answer = input.nextLine();
                        outBound.println("sendThis2"); //client tells server to place in away text file                     
                        outBound.println(answer);
                        answer = "";
                        menu = true;
                        messaging = false;
                    }
                    // end offline message
//==============================================================================
                    //menu error
                   //informs user of invalid choice 
                   if(answer.equals("try again1")){
                       System.out.println("Invalid choice, Try Again!"); //tells user to make a valid selection
                       System.out.println();
                       answer = "";
                       Thread st = new Thread(new InstantMessengerClient()); //new thread to recieve input while parent searches for messages.
                        st.start();
                        while(!(answer.equals("status")||answer.equals("message")||answer.equals("logout")||answer.equals("try again1"))){
                            if(!(isEmpty(username).equals("empty"))){
                            System.out.println("Incoming messages:");
                           
                            getMessages(username);
                            outBound.println("delete");
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(InstantMessengerClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                            }   
                        }
                        menu = false;
                        outBound.println("done");
                   }
                   
                   //end menu error
//==============================================================================
                   //user does not exist
                   if(fromServer.equals("fail2")){ //server tells client user that was trying to be messaged is not registered
                       System.out.println();
                       System.out.println("User does not exist, please try again."); // client tells user
                       System.out.println();
                       outBound.println("done2");
                   }
                   
                   //end doesn't exist
//==============================================================================                   
                   //messaged user logged off while talking
                   
                   if(fromServer.equals("away")){ //server tells client other user's status has changed
                       System.out.println();
                       System.out.println("The user has logged off while talking with you."); //client tells user message will be seen on return
                       System.out.println("They will recieve your messages when they log in again.");
                       System.out.println();
                       messaging = false;
                       menu = true; //resets values and ends messaging
                       answer = "";
                       outBound.println("done");
                       
                   }
                    
                    
                }
                if(answer.equals("logout")){ //prints bye instead of closing socket
                    System.out.println("Bye!"); 
                        outBound.close();
                        in.close();
                        mySocket.close();
                }
                else{
		System.out.println("closing socket!!!!!"); //socket and resources are closed
                    outBound.close();
                    in.close();
                    mySocket.close();
                }
    }    
        
        //======================================================================
        
        /**
         * Reads text from user's messages file and prints the text to the screen.
         * @param u username of current user logged into the system.
         * @return A String stating whether there are no more messages or the file is empty.
         * @throws IOException Exception is thrown if the file is not found.
         */
        public static String getMessages(String u) throws IOException{
            try{
        String message;
        BufferedReader br = new BufferedReader(new FileReader(u + ".txt"));
        
            while(br.readLine()!=null){
             do{   
                message = br.readLine();                                 
                if(message == null){
                    return "No more messages";
                }
                System.out.println(message);                            
            }while(true);
             
        }
            br.close();
        }
        catch(FileNotFoundException e){
        
        }
            return "empty";
        }
        
//==============================================================================
        
        /**
         * This function checks if the currently logged on user's message file is empty.
         * @param u Username of currently logged on user.
         * @return Returns a string stating whether or not it is empty.
         * @throws IOException Throws exception if file is not found.
         */
        
        public static String isEmpty(String u) throws IOException{
            try{
            FileInputStream messagesE = new FileInputStream(u + ".txt");
            String message;
            BufferedReader brE = new BufferedReader(new InputStreamReader(messagesE));
            
            if(brE.readLine()!=null){
             return "not empty";            
        }
            brE.close();
        }
          
        catch(FileNotFoundException e){
        
        }
            
            return "empty";
        }
                
//==============================================================================
        
        /**
         * This file populates an array with the usernames of users in a the currently logged
         * in user's friends list.
         * @param user Currently logged in user.
         */
        
public static void friendsList(String user){
    String username;
    int x = 0;
  
    try{
    FileInputStream list = new FileInputStream(user + "Friends.txt");
    BufferedReader br2 = new BufferedReader(new InputStreamReader(list));
            while(br2.readLine()!=null){
                
             do{   
                 username = br2.readLine();
                 if(username == null){
                    break;
                 }
                friendsList[x] = username;
                x++;           
            }while(true);
             
        }
           
            br2.close();
}
    catch(IOException e){System.out.println("friendsList not populated");}
}

//==============================================================================

        /**
         * Reads text from user's away messages file and prints the text to the screen.
         * @param u username of current user logged into the system.
         * @return A String stating whether there are no more messages or the file is empty.
         * @throws IOException Exception is thrown if the file is not found.
         */
 public static String getMessages2(String u) throws IOException{
            try{
        String message;
        BufferedReader br = new BufferedReader(new FileReader(u + "Away.txt"));
        
            while(br.readLine()!=null){
             do{   
                message = br.readLine();
                  
                
                if(message == null){
                    return "No more messages";
                }
                System.out.println(message);                              
            }while(true);             
        }
            br.close();
        }
        catch(FileNotFoundException e){
        
        }
            return "empty";
        }
 
//============================================================================== 
 
  /**
         * This function checks if the currently logged on user's away message file is empty.
         * @param u Username of currently logged on user.
         * @return Returns a string stating whether or not it is empty.
         * @throws IOException Throws exception if file is not found.
         */
  public static String isEmpty2(String u) throws IOException{
            try{
            FileInputStream messagesE = new FileInputStream(u + "Away.txt");
        String message;
        BufferedReader brE = new BufferedReader(new InputStreamReader(messagesE));
            if(brE.readLine()!=null){
             return "not empty";
             
        }
            brE.close();
        }
          
        catch(FileNotFoundException e){
        
        }
            
            return "empty";
        }
                
//==============================================================================  
  
  /**
   * This is the run function for the new threads that are started just
   * to receive input from the user. The threads are started only so that they
   * can wait for user input and update the answer variable with that input. This
   * probably isn't the most efficient way of going about this.
   */
                    @Override
        public void run(){
            
                answer = input.nextLine();
                if(done == false){
                answer = answer.toLowerCase();
                if(!(answer.equals("status")||answer.equals("message")||answer.equals("logout"))){
                    answer = "try again1";
                    System.out.println("failed");
                }
                }               
                
                try{
                    Thread.sleep(100);
                }
                catch(InterruptedException ie){ System.out.println("INTERRUPTED!!!!");}
             
        
    }
}
