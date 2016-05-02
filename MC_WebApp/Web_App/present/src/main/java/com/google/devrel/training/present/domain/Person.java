package com.google.devrel.training.present.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.devrel.training.present.form.PersonalProfileForm;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Person {
	// private PersonalProfile profile = new PersonalProfile();
	@Id
	private String mainEmail;
	private String firstName = "";
	private String lastName = "";
	private String ufid = "";
	private String contentRoot = "0000000000";

	/**
	 * @return the contentRoot
	 */
	public String getContentRoot() {
		return contentRoot;
	}

	/**
	 * @param contentRoot
	 *            the contentRoot to set
	 */
	public void setContentRoot(String contentRoot) {
		this.contentRoot = contentRoot;
	}

	/**
	 * Keys of the courses that this user registers to attend.
	 */
	private List<String> courseKeysToAttend = new ArrayList<>(0);

	/**
	 * Public Constructor
	 * 
	 * @param mainEmail
	 * @param form
	 */
	public Person(String mainEmail, PersonalProfileForm form) {
		this.mainEmail = mainEmail;
		this.firstName = form.getFirstName();
		this.lastName = form.getLastName();
		this.ufid = form.getUfid();
	}

	public Person(String mainEmail) {
		this.mainEmail = mainEmail;
		this.firstName = "";
		this.lastName = "";
		this.ufid = "";
	}

	/**
	 * Just making the default constructor private.
	 */
	@SuppressWarnings("unused")
	private Person() {
	}

	public String getEmail() {
		return mainEmail;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUfid() {
		return ufid;
	}

	/**
	 * This updated the personal profile with the values given in the form
	 * 
	 * @param form
	 */
	public void update(PersonalProfileForm form) {
		this.firstName = form.getFirstName();
		this.lastName = form.getLastName();
		this.ufid = form.getUfid();
	}

	/**
	 * From here the functions need to be modified based on the course class
	 */
	/**
	 * Getter for courseIdsToAttend.
	 * 
	 * @return an immutable copy of courseIdsToAttend.
	 */
	public List<String> getCourseKeysToAttend() {
		return ImmutableList.copyOf(courseKeysToAttend);
	}

	/**
	 * Adds a CoursId to courseIdsToAttend.
	 *
	 * The method initCourseIdsToAttend is not thread-safe, but we need a
	 * transaction for calling this method after all, so it is not a practical
	 * issue.
	 *
	 * @param courseKey
	 *            a websafe String representation of the Course Key.
	 */
	public void addToCourseKeysToAttend(String courseKey) {
		courseKeysToAttend.add(courseKey);
	}

	/**
	 * Remove the courseId from courseIdsToAttend.
	 *
	 * @param courseKey
	 *            a websafe String representation of the Course Key.
	 */
	public void unregisterFromCourse(String courseKey) {
		if (courseKeysToAttend.contains(courseKey)) {
			courseKeysToAttend.remove(courseKey);
		} else {
			throw new IllegalArgumentException("Invalid courseKey: " + courseKey);
		}
	}

}
