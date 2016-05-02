package com.google.devrel.training.present.domain;

public class FileInfo {
	String name;
	String link;

	public FileInfo(String name, String link) {
		this.name = name;
		this.link = link;
	}
	
	public FileInfo (){
		name = "";
		link = "";
	}

	/**
	 * @return the message
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

}