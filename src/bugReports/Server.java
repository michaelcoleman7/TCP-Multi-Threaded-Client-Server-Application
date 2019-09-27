//Author: Michael Coleman
//Class for running the server which allows clients to connect and receive sever information
package bugReports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bugReports.Bug.Status;

public class Server {
	//List of all users
	private static List<User> userlist = new ArrayList<User>();	
	//List of all bug
	private static List<Bug> buglist = new ArrayList<Bug>();
	private static FileManager fm = new FileManager();
	private static String userUrl = "./data/users.txt";
	private static String bugUrl = "./data/bugs.txt";
	
	//Default Constructor
	public Server (){
		init();
	}

	private void init() {
		//Set up users
		setupUsers();
		//Set up list
		setupBugs();
		//Launches the server 
		launchServer();
	}
	
	public static void saveListsToFile() {
		fm.saveUserstoFile(userUrl, Server.getUserlist());
		fm.saveBugsToFile(bugUrl, Server.getBuglist());
	}
	
	private void launchServer(){
		ServerSocket listener;
		int clientid=0;
		try {
			 listener = new ServerSocket(10000,10);
			 while(true)
			 {
				System.out.println("Main thread listening for incoming new connections");
				Socket newconnection = listener.accept();
				
				System.out.println("New connection received and spanning a thread");
				Connecthandler t = new Connecthandler(newconnection, clientid);
				clientid++;
				t.start();
			 }
		} 
		catch (IOException e){
			System.out.println("Socket not opened");
		}
	}

	@SuppressWarnings("static-access")
	private void setupUsers() {
		//Read in users from file
		this.userlist = fm.getUserList(userUrl); 
	}

	@SuppressWarnings("static-access")
	private void setupBugs() {
		//Read in bugs from file
		this.buglist = fm.getBugList(bugUrl);	
	}
	
	public static List<User> getUserlist() {
		return userlist;
	}

	public static List<Bug> getBuglist() {
		return buglist;
	}

	//Main method
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Server server = new Server();
	}
}

class Connecthandler extends Thread{
	Socket individualconnection;
	int socketid;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	int empID,bugID;
	String name,email,dept,appName,platfrom,problemDesc;
	Status status;
	Date dateTimeStamp;
	boolean loggedIn = false;
	String bugUrl = "./data/bugs.txt";
	String userUrl = "./data/users.txt";
	
	public Connecthandler(Socket s, int i){
		individualconnection = s;
		socketid = i;
	}
	
	//Method to communicate strings with client
	void sendMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
			closeConnections();	
		}
	}
	
	//Method to communicate a list of bugs to the client
	void sendBugList(List <Bug> lst){
		try{
			out.reset();
			out.writeObject(lst);
			out.flush();
		}
		catch(IOException ioException){
			System.out.println("Error sending list");
			closeConnections();	
		}
	}
	
	//Method to communicate a list of users to the client
	void sendUserList(List <User> lst)
	{
		try{
			out.writeObject(lst);
			out.flush();
		}
		catch(IOException ioException){
			System.out.println("Error sending list");
			closeConnections();	
		}
	}
	
	public void run(){	
		try {
			out = new ObjectOutputStream(individualconnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(individualconnection.getInputStream());
			System.out.println("Connection"+ socketid+" from IP address "+individualconnection.getInetAddress());
		
			do{
				//Send a string to the the client
				sendMessage("Option 1: Register new user \nOption 2: Login");
				//Set message = to the response given by the client
				message = (String)in.readObject();
				
				//User want to register a user
				if(message.equalsIgnoreCase("1")){
					register();
				}
				//User wants to login to access other server methods by entering the server password
				else if(message.equalsIgnoreCase("2")){
					loggedIn = login();
					
					//If login successful
					while(loggedIn){
						if(loggedIn == true){
							//Send menu of options to user
							sendMessage("Option 1: Register new user \nOption 2: Log Out\nOption 3: Add Bug \nOption 4: Assign a Bug \nOption 5: View Non-Assigned Bugs \nOption 6: View All Bugs \nOption 7: Update a Bug");
							message = (String)in.readObject();
							
							//Register a user
							if(message.equalsIgnoreCase("1")){
								register();
							}
							
							//Log out of Server
							if(message.equalsIgnoreCase("2")){
								loggedIn = false;
							}
							
							//Add a bug
							if(message.equalsIgnoreCase("3")){
								addBug();
							}
							//Assign a bug to a user
							if(message.equalsIgnoreCase("4")){
								assignBug();
							}
							//Show all unassigned bugs
							if(message.equalsIgnoreCase("5")){
								showUnassignedBugs();
							}
							//Show all bugs on server
							if(message.equalsIgnoreCase("6")){
								showAllBugs();
							}
							//Update bug record
							if(message.equalsIgnoreCase("7")){
								updateBugRecord();
							}
						}
					}
				}
				sendMessage("Press Y to continue and N to Exit\n");
				message = (String)in.readObject();
			}while(message.equalsIgnoreCase("Y"));
		}
		catch (IOException e) {
			e.printStackTrace();
			closeConnections();	
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			closeConnections();	
		} 
		finally{
			closeConnections();	
		}	
	}
	
	//Method to close connections
	private void closeConnections(){
		try {
			out.close();
			in.close();
			individualconnection.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	//Register a user with server
	private void register() {
		try{
		sendMessage("\nPlease enter Name");
		name = (String)in.readObject();
		
		boolean valid=true;
		
		//Ensure employee id is unique
		do{	
			valid=true;
			sendMessage("Please enter Employee ID");
			String stringEmpID = (String)in.readObject();
			empID = Integer.parseInt(stringEmpID);
			List <User> usrList = Server.getUserlist();
			
			for (int i = 0; i < usrList.size(); i++){
				if(empID == usrList.get(i).getEmployeeID()){
					//Send boolean(false) to client
					out.writeBoolean(false);
					valid=false;
				}
			}
				
		}while(valid == false);
		//Send boolean(true) to client
		out.writeBoolean(true);
		
		//Ensure email is unique
		do{	
			valid=true;
			sendMessage("\nPlease enter E-mail");
			email = (String)in.readObject();
			List <User> usrList = Server.getUserlist();
			
			for (int i = 0; i < usrList.size(); i++){
				if(email.equals(usrList.get(i).getEmail())){
					out.writeBoolean(false);
					valid=false;
				}
			}
				
		}while(valid == false);
		
		out.writeBoolean(true);
		
		sendMessage("\nPlease enter Department");
		dept = (String)in.readObject();
		
		//Create new User
		User newUser = new User(name,empID,email,dept);
		//Add User to User List
		Server.getUserlist().add(newUser);
		//Save user to file system
		Server.saveListsToFile();
		
		sendMessage("\nUser Successfully Created");
		}catch(Exception e){
			System.out.println("Error has occured");
			closeConnections();
		}
	}
	
	//Login method to access more server methods
	private boolean login(){
		//System password
		String password = "password";
		
		try{
			sendMessage("\nEnter Server Password to access other features of server");
			String pw = (String)in.readObject();
			//Compare client sent password with system password
			if(pw.equals(password)){
				out.writeBoolean(true);
				return true;
			}
			else{
				out.writeBoolean(false);
			}
		}catch(Exception e){
			System.out.println("Error with login");
		}
		return false;
	}

	//Add a bug to the server
	private void addBug() {
		try{
			sendMessage("\nPlease enter Application Name");
			appName = (String)in.readObject();
			
			sendMessage("\nPlease enter platfrom(e.g. Windows, Unix or Mac)");
			platfrom = (String)in.readObject();
			
			sendMessage("\nPlease enter Problem Description");
			problemDesc = (String)in.readObject();
			
			String statusString ="";
			boolean found = false;
			
			//Ensure status is open, closed or assigned
			while(found == false){
				sendMessage("\nPlease enter Status(Must be Open, Assigned or Closed)");
				//Get input as a string
		    	statusString =(String)in.readObject();
		    	if(statusString.equalsIgnoreCase("open") ||statusString.equalsIgnoreCase("assigned") || statusString.equalsIgnoreCase("closed")){
		    		out.writeBoolean(true);
		    		found = true;
		    	}
		    	else{
		    		out.writeBoolean(false);
		    	}
	    	}
	    	//Convert to Upper case as part of Enum
			statusString = statusString.toUpperCase();
	    	//Convert to Enum Status
	    	status = Status.valueOf(statusString);
	
			//Create a new date based on current time
			dateTimeStamp = new Date();
			
			//Set bugID to random number between 1 and 1000 - Obviously would have add code to cap server to hold only 1000 entries for real world server but I capped at 1000 for ease of remembering BugID's
			bugID = (int) (Math.random() * (1000 - 1)) + 1;
			
			List <Bug> bgList = Server.getBuglist();
	
			for (int i = 0; i < bgList.size(); i++){
				//Ensure that bugID is unique by looping until it is not equal to another bugID in system
				while(bgList.get(i).getBugID() == bugID){
					bugID = (int) (Math.random() * (1000 - 1)) + 1;
				}
			}
			
			//Create new Bug
			Bug newBug = new Bug(appName,dateTimeStamp,platfrom,problemDesc,status);
			newBug.setBugID(bugID);
			//Add Bug to Bug List
			Server.getBuglist().add(newBug);
			//Save Bug to file system
			Server.saveListsToFile();
		}catch(Exception e){
			System.out.println("Error Adding Bug");
		}	
	}
	
	//Assign a bug to a user
	private void assignBug() {
		try{
			sendMessage("\nPlease enter Bug ID you wish to assign");
			String bgID = (String)in.readObject();
			
			bugID = Integer.parseInt(bgID);
			
			List <Bug> bgList = Server.getBuglist();
			boolean found = false;
			//Ensure that bugID exists in the Server
			for(int i = 0; i < bgList.size();i++){
				if(bugID == bgList.get(i).getBugID()){
					found = true;
				}
			}
			//If BugID exists in server
			if(found){
				out.writeBoolean(true);
				
				List <User> usrList = Server.getUserlist();
				//Send User list to client
				sendUserList(usrList);
				
				sendMessage("\nPlease enter employee id of the user you wish to assign the Bug "+bugID);
				String userid = (String)in.readObject();
				int userNum = Integer.parseInt(userid);
				
				//Update servers buglist with new bug assigned to user
				for(int i = 0; i < Server.getBuglist().size();i++){
					if(Server.getBuglist().get(i).getBugID() == bugID){
						//Set assignedTo variable  = to the User's Employee ID						
						Server.getBuglist().get(i).setAssignedTo(userNum);
						
						//Set bugs status to ASSIGNED as it is now assigned to a user
						status = Status.valueOf("ASSIGNED");
						Server.getBuglist().get(i).setStatus(status);
					}
				}
				//Save bugs to file
				Server.saveListsToFile();
			}
			//If bugID not found in server
			else{
				out.writeBoolean(false);
			}
		}
		catch(Exception e){
			System.out.println("Error assigning bug");
		}	
	}
	
	//Method to show bugs which are unassigned
	private void showUnassignedBugs(){
		List <Bug> bgList = Server.getBuglist();
		List <Bug> unassignedList = new ArrayList<Bug>();
		
		for (int i = 0; i < bgList.size(); i++){
			//If assignedTo value = 0 then it is unassigned
			if(bgList.get(i).getAssignedTo() == 0){
				//Add unassigned to their own list
				unassignedList.add(bgList.get(i));
			}
		}
		//Send unassigned list to client to view
		sendBugList(unassignedList);
	}
	
	//Method to show all bug records on server
	private void showAllBugs(){
		List <Bug> bgList = Server.getBuglist();
		List <User> usrList = Server.getUserlist();
		//Send bug List to client
		sendBugList(bgList);
		//Send user list to client
		sendUserList(usrList);
	}
	
	//Method to update a bug records details - can update status,append to problem description or change assigned engineer
	private void updateBugRecord() {
		try{
			sendMessage("Enter which bug you would like to update(use BugID)\n");
			String bgID = (String)in.readObject();
			bugID = Integer.parseInt(bgID);
			
			List <Bug> bgList = Server.getBuglist();
			//Ensure BugID exists in server
			boolean found = false;
			for(int i = 0; i < bgList.size();i++){
				if(bugID == bgList.get(i).getBugID()){
					found = true;
				}
			}
			//If bugID exists in server
			if(found){
				out.writeBoolean(true);
				
				sendMessage("Select which detail you would like to update\n1) Status\n2) Append to Problem description\n3) Change assigned engineer\n");
				String detail = (String)in.readObject();
				int detailNum = Integer.parseInt(detail);
				
				//update status
				if(detailNum == 1){
					String statusString ="";
					boolean foundStatus = false;
					
					//Ensure status is open,closed or assigned
					while(foundStatus == false){
						sendMessage("\nPlease enter Status(Must be Open, Assigned or Closed)");
						//get as a string
				    	statusString =(String)in.readObject();
				    	if(statusString.equalsIgnoreCase("open") ||statusString.equalsIgnoreCase("assigned") || statusString.equalsIgnoreCase("closed")){
				    		out.writeBoolean(true);
				    		foundStatus = true;
				    	}
				    	else{
				    		out.writeBoolean(false);
				    	}
			    	}
			    	//Convert to Upper case as part of Enum
					statusString = statusString.toUpperCase();
			    	//Convert to Enum Status
			    	status = Status.valueOf(statusString);
			    	
					for (int i = 0; i < Server.getBuglist().size(); i++){
						if(Server.getBuglist().get(i).getBugID()== bugID){
							Server.getBuglist().get(i).setStatus(status);
						}
					}
					//Save bugs to file
					Server.saveListsToFile();
				}
				if(detailNum == 2){
					sendMessage("Enter What you would like to append to the problem description\n");
					String problemDescExtra = (String)in.readObject();
					
					for (int i = 0; i < Server.getBuglist().size(); i++){
						if(Server.getBuglist().get(i).getBugID()== bugID){
							//Append pd (user input) to the problem description
							Server.getBuglist().get(i).setProblemDescription(Server.getBuglist().get(i).getProblemDescription()+" "+problemDescExtra);
						}
					}
					//Save bugs to file
					Server.saveListsToFile();
				}
				
				//Change the assigned engineer
				if(detailNum == 3){	
					List <User> usrList = Server.getUserlist();
					sendUserList(usrList);
					
					sendMessage("Enter the engineer ID you wish to assign to this bug\n");
					String engineer = (String)in.readObject();
					int engineerNum = Integer.parseInt(engineer);
					
					//Update servers buglist with new bug assigned to user
					for(int i = 0; i < Server.getBuglist().size();i++){
						if(Server.getBuglist().get(i).getBugID() == bugID){
							//Set the bugs assigned engineer
							Server.getBuglist().get(i).setAssignedTo(engineerNum);
						}
					}
					//Save bugs to file
					Server.saveListsToFile();
				}
			}
			//If BugID not found on server
			else{
				out.writeBoolean(false);
			}
		}
		catch(Exception e){
			System.out.println("Error updating bug");
		}
	}	
}