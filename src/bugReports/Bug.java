//Author: Michael Coleman
//Class for creating a Bug
package bugReports;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Bug implements Serializable {
	//Bug can only be one of 3 cases
	public enum Status{OPEN,ASSIGNED,CLOSED};
	
	private String applicationName;
	private Date dateTimeStamp;
	private String platform; //(e.g. Window, Unix or Mac)
	private String problemDescription;
	private Status status; //(Can be Open, Assigned or Closed)
	private Integer assignedTo;
	private int bugID;
	
	//Default Constructor
	public Bug(String applicationName, Date dateTimeStamp, String platform, String problemDescription, Status status) {
		this.applicationName = applicationName;
		this.dateTimeStamp=dateTimeStamp;
		this.platform = platform;
		this.problemDescription = problemDescription;
		this.status = status;
		this.assignedTo = 0;
	}
	//Constructor for loading from file
	public Bug(String applicationName, Date dateTimeStamp, String platform, String problemDescription,Status status,int bugId, Integer assignedTo) {
		this.applicationName = applicationName;		
		this.dateTimeStamp = dateTimeStamp;	
		this.platform = platform;
		this.problemDescription = problemDescription;
		this.status = status;
		this.assignedTo = assignedTo;
		this.bugID = bugId;
	}

	//Methods
	@Override
	public String toString() {
		return "Bug ID: "+bugID+"\nApplication Name: "+ applicationName+"\nTimeStamp: "+ dateTimeStamp.toString() +"\nPlatform: "+ platform +"\nProblem Description: "+ problemDescription +"\nStatus: "
				+ status+"\nAssigned to employee with ID(0 = unassigned): "+ assignedTo+"\n";
	}

	//Getters and Setters
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Integer assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	public int getBugID() {
		return bugID;
	}

	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
}