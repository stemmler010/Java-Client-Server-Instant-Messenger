Josh Stemmler Instant Messenger Client/Server Application

Contents

1. Getting Started
2. Restrictions
3. Features


***********************************************************
*	1. Getting Started				  *
*							  *
***********************************************************

"Installation"
1. Unzip the folder to your directory on the server (The server address is hardcoded in, default is rosemary.umw.edu change to your server) 

2. Make sure all of the files are in the same folder

3. Navigate to the folder containing the files in the command line

5. type the command "javac instantMessengerServer.java" into the command line and press enter

6. type the command "javac InstantMessengerClient.java" into the command line and press enter

7. open up a second command line window

8. In one of the windows type "java instantMessengerServer" into the command line and press enter
   At this point you should see that the server is accepting connections

9. In the other window type "java InstantMessengerClient" into the command line and press enter
   At this point you should be greeted by a prompt to log in

10. Log in or register and begin using the client


***********************************************************
*	2. Restrictions				          *
*							  *
***********************************************************


The text files used for messaging/away messages are made on the fly and do not need to be created.

This program is ran from the command line, there is no GUI and as such a new window
will not pop up when someone messages you (like most commercial instant messenger programs)
As such you must type message to message someone, if you would like to change users while
messaging someone else, type just, message again and type the new users username. to go back
to the menu just type exit. An alternative would be to open a new command line window, start the client
and begin talking to the other person there the only problem with that is that, the messages from both 
users will still show up in both windows. 

for example I am talking to bill, I get messages of format bill: messages and then a new message from
jack shows up so now I see bill: messages and jack: messages

While this is much like a chat room it is very different in that each conversation is still independant, so 
bill can't see my conversation with jack and jack can't see my conversation with bill.


***********************************************************
*	3. Features				          *
*							  *
***********************************************************

This application can do the following things
1. Send messages to a user whether they are online or offline
2. Recieve messages that were sent while the user was logged out
3. Check the status of users on a user's friends list (offline/online)
4. Messages are recieved where ever the user is logged on from (if a user is logged in at seperate locations at the same time, both will recieve the messages)
5. Can switch users in the middle of messaging someone else by typing message
6. Will check to see if a user logs off mid conversation

