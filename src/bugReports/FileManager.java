//Author: Michael Coleman
//Class for Managing the file i.e reading information back into server from files
//adapted from https://www.mkyong.com/java/json-simple-example-read-and-write-json/
package bugReports;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import bugReports.Bug.Status;

public class FileManager {
	
	@SuppressWarnings("unchecked")
	public List<User> getUserList(String url) {
		JSONParser parser = new JSONParser();
		List<User> userList = new ArrayList<User>();
		
        try {
        	//Create an object from JSON parsed from file
            Object obj = parser.parse(new FileReader(url));

            //Cast the object to a JSONObject 
            JSONObject jsonObject = (JSONObject) obj;
            
            //Create a JSONArray from the users attribute in the JSONObject
            JSONArray userarray = (JSONArray) jsonObject.get("users");            
            
            for (int i = 0; i < userarray.size(); i++) {
            	//Assign variables from the JSONArray by casting to a JSONObject(to use its get method to find value)
            	JSONObject tempJSONObject = ((JSONObject) userarray.get(i));
                String name = tempJSONObject.get("name").toString();
                int empId = ((Long) tempJSONObject.get("empId")).intValue();//cast to long and call .intValue() to get value as an integer
                String email =tempJSONObject.get("email").toString();
                String dept =tempJSONObject.get("dept").toString();
                List<Long> assignedBugIds = (List<Long>) tempJSONObject.get("assignedBugIds"); //cast to Long                              
 
                //Create a temporary user
                User temp = new User(name, empId, email, dept, assignedBugIds);
                //Add temporary user to list
                userList.add(temp);
            } 
        } catch(FileNotFoundException e){
        	try {
				new FileOutputStream("./data/users.txt", true).close();
				
				try (FileWriter file = new FileWriter(url)) {
					file.write("{\"users\":[]}");
					file.flush();
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }catch (Exception e) {
			System.out.println("Unable to parse users");
		}
		return userList;
	}
	
	@SuppressWarnings("unchecked")
	public void saveUserstoFile(String url,List<User> userArray) {
		//Writes to file
		try (FileWriter file = new FileWriter(url)) {
			file.write("{\"users\":[");
			for (int i = 0; i < userArray.size(); i++) {
				if(i>0)
					file.write(",");
				//Create a JSON Object and add all values to be wrote as JSON
				JSONObject tempUser = new JSONObject();
				tempUser.put("name", userArray.get(i).getName());
				tempUser.put("empId", new Integer(userArray.get(i).getEmployeeID()));
				tempUser.put("email", userArray.get(i).getEmail());
				tempUser.put("dept", userArray.get(i).getDepartment());
				//Create a nested JSON array to store assignedBug Array
				List<Long> tempBugList = userArray.get(i).getAssignedBugIds();
				JSONArray buglist = new JSONArray();
	
				//Loop assigned bug and add to array
				for (int j = 0; j < tempBugList.size(); j++) {
					buglist.add(tempBugList.get(i));
				}
				
				//Save array to JSON
		        tempUser.put("assignedBugIds", buglist);

		        //Write JSON to file
		        file.write(tempUser.toString());
		        file.flush();
  	       
			}
			
			file.write("]}");
			
		} catch (IOException e) {
            System.out.println("Unable to print to file");
        }
	}
	
	public List<Bug> getBugList(String url) {
		JSONParser parser = new JSONParser();
		List<Bug> bugList = new ArrayList<Bug>();
		
        try {
        	//Create an object from JSON parsed from file
            Object obj = parser.parse(new FileReader(url));

            //Cast the object to a JSONObject 
            JSONObject jsonObject = (JSONObject) obj;
            
            //Create a JSONArray from the bug attribute in the JSONObject
            JSONArray bugarray = (JSONArray) jsonObject.get("bugs");  

            for (int i = 0; i < bugarray.size(); i++) {
            	//Assign variables from the JSONArray by casting to a JSONObject(to use its get method to find value)
            	JSONObject tempJSONObject = ((JSONObject) bugarray.get(i));
            	String applicationName = tempJSONObject.get("applicationName").toString();
            	String dateTimeStampString = (String) tempJSONObject.get("dateTimeStamp");
            	//Convert from String to date // adapted from https://stackoverflow.com/questions/20035030/read-and-write-date-calendar-to-a-txt-file-in-java
            	DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
            	Date dateTimeStamp = df.parse(dateTimeStampString);
            	String platform = tempJSONObject.get("platform").toString();
            	String problemDescription = tempJSONObject.get("problemDescription").toString();
            	//Get as a string
            	String statusString = tempJSONObject.get("status").toString();
            	//Convert to Enum Status
            	Status status = Status.valueOf(statusString);
            	//Get as a long
            	Long assignedToLong = ((Long) tempJSONObject.get("assignedTo"));
            	//Convert to integer
            	Integer assignedTo = assignedToLong.intValue();
            	Long bugIDLong = ((Long) tempJSONObject.get("bugID"));
            	//Convert to integer
            	int bugID = bugIDLong.intValue();
            	
                //Create a temporary bug
                Bug temp = new Bug(applicationName, dateTimeStamp, platform, problemDescription, status, bugID, assignedTo);
                //Add temporary bug to list
                bugList.add(temp);
            } 
        }catch(FileNotFoundException e){
        	try {
				new FileOutputStream("./data/bugs.txt", true).close();
				
				try (FileWriter file = new FileWriter(url)) {
					file.write("{\"bugs\":[]}");
					file.flush();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        catch (Exception e) {
			System.out.println("Unable to parse bugs");
		}
		return bugList;
	}
	
	@SuppressWarnings({ "unchecked"})
	public void saveBugsToFile(String url, List<Bug> bugsArray) {
		//Writes to file
		try (FileWriter file = new FileWriter(url)) {
			file.write("{\"bugs\":[");
			for (int i = 0; i < bugsArray.size(); i++) {
				if(i>0)
					file.write(",");
				//Create a JSON Object and add all values to be wrote as JSON
				JSONObject tempBug = new JSONObject();
				tempBug.put("applicationName", bugsArray.get(i).getApplicationName());
				tempBug.put("dateTimeStamp", bugsArray.get(i).getDateTimeStamp().toString());
				tempBug.put("platform", bugsArray.get(i).getPlatform());
				tempBug.put("problemDescription", bugsArray.get(i).getProblemDescription());
				tempBug.put("status", bugsArray.get(i).getStatus().toString());
				tempBug.put("assignedTo", new Integer(bugsArray.get(i).getAssignedTo()));
				tempBug.put("bugID", bugsArray.get(i).getBugID());

		        //Write JSON to file
		        file.write(tempBug.toString());
		        file.flush();
			}	
			file.write("]}");	
		} catch (IOException e) {
            System.out.println("Unable to print to file");
        }
	}
}