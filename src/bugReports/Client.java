//Author: Michael Coleman
//Class for running a client which connects to the server sending and receiving information to make adjustments to server's data e.g. create/update a bug
package bugReports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Client 
{
	private Socket connection;
	private String message;
	private  Scanner console;
	private  String ipaddress;
	private int portaddress;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean valid = false;
	private boolean loggedIn = false;
	private List <Bug> bgList;
	private List <User> usrList;

	public Client(){
		console = new Scanner(System.in);
		System.out.println("Enter the IP Address of the server");
		ipaddress = console.nextLine();
		
		System.out.println("Enter the TCP Port");
		try {
			portaddress  = console.nextInt();
		} catch (Exception e) {
			System.out.println("Cannot connect to entered TCP Port");
		}
	}
	
	public static void main(String[] args) {
			Client temp = new Client();
			temp.clientapp();
	}

	//Method to communicate Strings to server
	void sendMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			System.out.println("Error sending message");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void clientapp(){
		try 
		{
			connection = new Socket(ipaddress,portaddress);
		
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			System.out.println("Client Side ready to communicate");
		
			do
			{
		    //Read input from server and store in message
			message = (String)in.readObject();
			System.out.println(message);
			//Set message to user input
			message = console.next();
			//Send user input to server
			sendMessage(message);
			
			//If user selects to Register New User
			if(message.equalsIgnoreCase("1"))
			{
				console.nextLine();//flush buffer
				
				message = (String)in.readObject();
				System.out.println(message);
				message = console.nextLine();
				sendMessage(message);
				
				//Validate input to ensure it is unique (employee id)
				while(valid == false){
					message = (String)in.readObject();
					System.out.println(message);
					message = console.nextLine();
					sendMessage(message);
					
					valid= in.readBoolean();
					if(valid == false){
						System.out.println("Employee ID already registered to another user");
					}
					
				}
				valid = false;
				
				//Validate input to ensure it is unique (email)
				while(valid == false){
					message = (String)in.readObject();
					System.out.println(message);
					message = console.nextLine();
					sendMessage(message);
					
					valid= in.readBoolean();
					if(valid == false){
						System.out.println("Email already registered to another user");
					}
				}
				valid = false;
				
				message = (String)in.readObject();
				System.out.println(message);
				message = console.nextLine();
				sendMessage(message);
				
				message = (String)in.readObject();
				System.out.println(message);
			}
			
			//User attempts to login
			else if(message.equalsIgnoreCase("2")){
				message = (String)in.readObject();
				System.out.println(message);
				message = console.next();
				sendMessage(message);
				
				loggedIn = in.readBoolean();
				if(loggedIn == true){
					System.out.println("You have successfully logged in");
				}
				else{
					System.out.println("Incorrect Password");
				}	
			}
			//If login successful - access gained to options 3-7
			if(loggedIn == true){
				while(loggedIn){
					message = (String)in.readObject();
					System.out.println(message);
					message = console.next();
					sendMessage(message);
					
					if(message.equalsIgnoreCase("1"))
					{
						message = (String)in.readObject();
						System.out.println(message);
						message = console.next();
						sendMessage(message);
						
						//Validate input to ensure it is unique (employee id)
						while(valid == false){
							message = (String)in.readObject();
							System.out.println(message);
							message = console.next();
							sendMessage(message);
							
							valid= in.readBoolean();	
							if(valid == false){
								System.out.println("Employee ID already registered to another user");
							}
							
						}
						valid = false;
						
						//Validate input to ensure it is unique (email)
						while(valid == false){
							message = (String)in.readObject();
							System.out.println(message);
							message = console.next();
							sendMessage(message);
							
							valid= in.readBoolean();
							if(valid == false){
								System.out.println("Email already registered to another user");
							}
							
						}
						valid = false;
						
						message = (String)in.readObject();
						System.out.println(message);
						message = console.next();
						sendMessage(message);
						
						message = (String)in.readObject();
						System.out.println(message);
						
					}
					//Log out of server
					if(message.equalsIgnoreCase("2"))
					{
						loggedIn=false;
					}
					
					//Add bug to server
					if(message.equalsIgnoreCase("3"))
					{
						console.nextLine();//clear buffer
						
						message = (String)in.readObject();
						System.out.println(message);
						message = console.nextLine();
						sendMessage(message);
						
						message = (String)in.readObject();
						System.out.println(message);
						message = console.nextLine();
						sendMessage(message);
						
						message = (String)in.readObject();
						System.out.println(message);
						message = console.nextLine();
						sendMessage(message);
						
						message = (String)in.readObject();
						System.out.println(message);
						message = console.nextLine();
						sendMessage(message);
						
						valid= in.readBoolean();
						//Ensure status is set to open,closed or assigned
						while(valid == false){
							System.out.println("Status must be open,assigned or closed");
							
							message = (String)in.readObject();
							System.out.println(message);
							message = console.nextLine();
							sendMessage(message);
							
							valid= in.readBoolean();	
						}
						valid = false;
						System.out.println("Bug successfully created");
					}
					//Assign a bug to a user
					if(message.equalsIgnoreCase("4"))
					{
						message = (String)in.readObject();
						System.out.println(message);
						message = console.next();
						sendMessage(message);
						
						valid= in.readBoolean();
						//If bugID doesn't exist in server
						if(valid==false){
							System.out.println("Bug Id "+message+ " Does not exist in System");
						}
						//If bugID exists in server
						else{
							//Read in List of users
							usrList = (List <User>)in.readObject();
							//Display list of users to console
							for(int i = 0; i<usrList.size();i++){
								System.out.println("id: "+usrList.get(i).getEmployeeID()+") "+usrList.get(i).getName());
							}
							
							message = (String)in.readObject();
							System.out.println(message);
							message = console.next();
							sendMessage(message);
							
							System.out.println("Bug has been assigned successfully\n");
						}
						valid = false;
					}	
					//Show all Bugs on server not assigned to a user
					if(message.equalsIgnoreCase("5"))
					{
						bgList = (List <Bug>)in.readObject();
						//If no assigned bugs e.g. if list returned from server is empty
						if(bgList.size()==0){
							System.out.println("No Unassigned bugs");
						}
						for(int i = 0; i<bgList.size();i++){
							//output list using overwritten toString()
							System.out.println(bgList.get(i).toString());
						}
					}				
					
					//Show all Bugs on server
					if(message.equalsIgnoreCase("6"))
					{
						bgList = (List <Bug>)in.readObject();
						usrList = (List <User>)in.readObject();
						
						//If bug list returned is empty
						if(bgList.size() == 0){
							System.out.println("No bugs in server");
						}
						
						//Output all bugs in returned bug list to console
						for(int i = 0; i<bgList.size();i++){
							System.out.println(bgList.get(i).toString());
						}	
					}
					
					//Update Bug record details
					if(message.equalsIgnoreCase("7"))
					{
						message = (String)in.readObject();
						System.out.println(message);
						message = console.next();
						sendMessage(message);
						
						valid = in.readBoolean();
						//Ensure bugID exists on server
						if(valid==false){
							System.out.println("Bug Id "+message+ " Does not exist in System");
						}
						else{
							message = (String)in.readObject();
							System.out.println(message);
							message = console.next();
							sendMessage(message);
							
							//Update bug record status
							if(message.equalsIgnoreCase("1")){
								message = (String)in.readObject();
								System.out.println(message);
								message = console.next();
								sendMessage(message);	
								
								valid= in.readBoolean();
								
								while(valid == false){
									System.out.println("Status must be open,assigned or closed");
									
									message = (String)in.readObject();
									System.out.println(message);
									message = console.nextLine();
									sendMessage(message);
									
									valid= in.readBoolean();	
								}
								valid = false;
							}
							//Append to problem description
							else if(message.equalsIgnoreCase("2")){
								console.nextLine(); //clear buffer
								
								message = (String)in.readObject();
								System.out.println(message);
								message = console.nextLine();
								sendMessage(message);
							}
							//Update bug record's assigned engineer
							else if(message.equalsIgnoreCase("3")){
								usrList = (List <User>)in.readObject();
								for(int i = 0; i<usrList.size();i++){
									System.out.println(i+1+") "+usrList.get(i).getName());
								}
								
								message = (String)in.readObject();
								System.out.println(message);
								message = console.next();
								sendMessage(message);
								
								System.out.println("Bug has been updated successfully\n");
		
							}
						}
					}
				}
			}
			
			message = (String)in.readObject();
			System.out.println(message);
			message = console.next();
			sendMessage(message);
			
			}while(message.equalsIgnoreCase("Y"));
			
			out.close();
			in.close();
			connection.close();
		} 
		catch (IOException e) {
			System.out.println("Error connecting to server");
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}	
	}
}