package com.google.devrel.training.present.domain;


import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.common.collect.ImmutableList;
import com.google.devrel.training.present.form.CourseForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Course {
	@Id
	private long key;
	private String courseId;
	private String courseName;
	private String description;
	private String contentRoot = "0000000000";
	private List<FileInfo> fileList = new ArrayList<>(0);

	@Index
	private int maxAttendees;

	@Parent
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private Key<Person> personKey;

//	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private String organizerEmail;

	private int seatsAvailable;

	@SuppressWarnings("unused")
	private Course() {
	}

	public Course(final long id, final String organizerEmail, final CourseForm form) {
		this.key = id;
		this.organizerEmail = organizerEmail;
		this.courseId = form.getCourseId();
		this.courseName = form.getCourseName();
		this.maxAttendees = form.getMaxAttendees();
		this.seatsAvailable = form.getMaxAttendees();
		this.description = form.getDescription();
		this.personKey = Key.create(Person.class, organizerEmail);
	}

	public Course(final long id, final String organizerEmail, final CourseForm form, final String courseRoot) {
		this.key = id;
		this.organizerEmail = organizerEmail;
		this.courseId = form.getCourseId();
		this.courseName = form.getCourseName();
		this.maxAttendees = form.getMaxAttendees();
		this.seatsAvailable = form.getMaxAttendees();
		this.description = form.getDescription();
		this.personKey = Key.create(Person.class, organizerEmail);
		this.contentRoot = courseRoot;
	}
	
	public long getId() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getMaxAttendees() {
		return maxAttendees;
	}

	public int getSeatsAvailable() {
		return seatsAvailable;
	}

//	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getOrganizerEmail() {
		return organizerEmail;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Key<Person> getPersonKey() {
		return personKey;
	}

	// Get a String version of the key
	public String getWebsafeKey() {
		return Key.create(personKey, Course.class, key).getString();
	}
	
	public String getContentRoot() {
		return contentRoot;
	}

	public void setContentRoot(String contentRoot) {
		this.contentRoot = contentRoot;
	}

	public void registerForCourse(final int number) {
		if (seatsAvailable < number) {
			throw new IllegalArgumentException("There are not enough seats available.");
		}
		seatsAvailable = seatsAvailable - number;
	}

	public void deregisterFromCourse(final int number) {
		if (seatsAvailable + number > maxAttendees) {
			throw new IllegalArgumentException("The number of seats will exceed the capacity.");
		}
		seatsAvailable = seatsAvailable + number;
	}

	/**
	 * May need to modify this function to take into account only the changed
	 * and no null parameters and not modify everything always.
	 * 
	 * @param form
	 */
	public void updateWithCourseForm(CourseForm form) {
		this.courseId = form.getCourseId();
		this.courseName = form.getCourseName();
		this.maxAttendees = form.getMaxAttendees();
		this.seatsAvailable = form.getMaxAttendees();
	}
	
	
	public void addToFiles(String name,String link) {
		FileInfo fi = new FileInfo(name, link);
		fileList.add(fi);
	}
	
	public List<FileInfo> getCourseFiles() {
		return ImmutableList.copyOf(fileList);
	}
}
