//Author: Michael Coleman
//Class for creating a user
package bugReports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class User implements Serializable{
	//Variables
	private String name;
	private Integer employeeID;
	private String email;
	private String department;
	private List<Long> assignedBugs;//long used as JSON_Simple Jar returns long
	
	//Default Constructor
	public User(String name,Integer empId,String email,String dept) {
		this.name = name;
		this.employeeID = empId;
		this.email = email;
		this.department = dept;	
		this.assignedBugs = new ArrayList<Long>();
	}
	
	//Constructor for loading from files
	public User(String name,Integer empId,String email,String dept,List<Long> assignedBugs) {
		this.name = name;
		this.employeeID = empId;
		this.email = email;
		this.department = dept;
		this.assignedBugs= assignedBugs;
	}
	
	//methods
	@Override
	public String toString() {
		return "Name: "+ name + "\nID: "+ employeeID + "\nEmail: "+ email + "\nDept: "+ department+"\nAssignedBugs: "+assignedBugs;
	}
	
	//Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(Integer employeeID) {
		this.employeeID = employeeID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<Long> getAssignedBugIds() {
		return this.assignedBugs;
	}

	public void addBugs(Long id) {
		this.assignedBugs.add(id);
	}
}
