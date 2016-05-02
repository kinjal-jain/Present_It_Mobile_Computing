package com.google.devrel.training.present.form;

public class PersonalProfileForm{
	/*
	 * I put in all the fields that I felt the user might need to change
	 * in the future after the profile of the user has been created
	 */
	private String firstName;
	private String lastName;
	private String ufid;
	
	
	/*
	 * This is the empty constructor 
	 */
	PersonalProfileForm(){
		firstName = "";
		lastName = "";
		ufid = "";
	}
	
	/**
	 * This is the constructor to set the values in the form
	 * @param fName
	 * @param lName
	 */
	PersonalProfileForm(String fName, String lName, String ufid){
		this.firstName = fName;
		this.lastName = lName;
		this.ufid = ufid;
	}
	
	/**
	 * This returns the first name
	 * @return the firstName available in the form
	 */
	public String getFirstName(){
		return firstName;
	}
	
	
	/**
	 * This returns the last Name
	 * @return the lastName available in the form
	 */
	public String getLastName(){
		return lastName;
	}
	
	public String getUfid(){
		return ufid;
	}
}


