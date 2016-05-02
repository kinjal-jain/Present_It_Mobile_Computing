package com.google.devrel.training.present.spi;

import static com.google.devrel.training.present.service.OfyService.factory;
import static com.google.devrel.training.present.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.devrel.training.present.Constants;
import com.google.devrel.training.present.domain.Course;
import com.google.devrel.training.present.domain.FileInfo;
import com.google.devrel.training.present.domain.HelloClass;
import com.google.devrel.training.present.domain.Person;
import com.google.devrel.training.present.form.CourseForm;
import com.google.devrel.training.present.form.PersonalProfileForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

/**
 * Defines course APIs.
 */
@SuppressWarnings("unused")
@Api(name = "presentIt", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID,Constants.ANDROID_CLIENT_ID },
        audiences = {Constants.ANDROID_AUDIENCE},description = "API for the Present It Backend application.")
public class PresentItApi {
	private String tempCourseFolderId = "";
	private String fileName = "";
	private String fileLink = "";
	private String fileIdToPresent = "";
	private String presentActionToBeTaken = "";

	public static class WrappedString {

		private final String result;
		private final String reason;

		public WrappedString(String result) {
			this.result = result;
			this.reason = "";
		}

		public WrappedString(String result, String reason) {
			this.result = result;
			this.reason = reason;
		}

		public String getResult() {
			return result;
		}

		public String getReason() {
			return reason;
		}
	}

	/**
	 * An api method to save the users profile
	 * 
	 * @param user
	 *            A user object injected by the cloud endpoints
	 * @param form
	 *            A personProfileForm sent from the client form
	 * @return The personProfile just created
	 * @throws UnauthorizedException
	 *             When the user object is null
	 */
	@ApiMethod(name = "savePersonProfile", path = "savePersonProfile", httpMethod = HttpMethod.POST)
	public Person savePersonProfile(final User user, PersonalProfileForm form) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		String mainEmail = user.getEmail();

		Person person = ofy().load().key(Key.create(Person.class, mainEmail)).now();

		if (person == null) {
			person = new Person(mainEmail, form);
		} else {
			person.update(form);
		}

		ofy().save().entity(person).now();
		return person;
	}

	/**
	 * This method is used to get the profile of the user that is currently
	 * logged in.
	 * 
	 * @param user
	 *            A User object injected by the cloud endpoints.
	 * @return The profile of the user that is currently logged in
	 * @throws UnauthorizedException
	 *             if the user object is null
	 */
	@ApiMethod(name = "getPersonProfile", path = "home", httpMethod = HttpMethod.GET)
	public Person getPersonProfile(final User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		String mainEmail = user.getEmail();
		Key<Person> key = Key.create(Person.class, mainEmail);

		Person person = (Person) ofy().load().key(key).now();
		return person;
	}

	/**
	 * 
	 * @param user
	 *            A user for whom the profiles have to be extracted
	 * @return The profile of the user supplied
	 */
	private static Person getPersonProfileFromUser(User user) {
		String mainEmail = user.getEmail();
		Key<Person> key = Key.create(Person.class, mainEmail);

		Person person = (Person) ofy().load().key(key).now();
		if (person == null) {
			person = new Person(mainEmail);
		}
		return person;
	}

	/**
	 * Creates a new course by the user and stores it in datastore
	 * 
	 * @param user
	 *            A user who invokes this method, null when the user is not
	 *            signed in.
	 * @param form
	 *            A CourseForm object representing user's inputs.
	 * @return The course object that has been created
	 * @throws UnauthorizedException
	 *             if the user = null
	 */
	@ApiMethod(name = "createCourse", path = "home", httpMethod = HttpMethod.POST)
	public Course createCourse(final User user, final CourseForm form) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		String mainEmail = user.getEmail();
		Key<Person> personKey = Key.create(Person.class, mainEmail);
		final Key<Course> courseKey = factory().allocateId(personKey, Course.class);

		final long courseId = courseKey.getId();
		Person person = getPersonProfileFromUser(user);
		Course course = new Course(courseId, mainEmail, form, tempCourseFolderId);
		tempCourseFolderId = "";
		ofy().save().entities(course, person).now();

		return course;
	}

	/**
	 * This returns the course when the websafe key is provided
	 * 
	 * @param websafeCourseKey
	 *            This is the websafe key of the course that is queried
	 * @return Returns the course
	 * @throws NotFoundException
	 *             If no course if found in the datastore with the given key
	 */
	@ApiMethod(name = "getCourse", path = "course/{websafeCourseKey}", httpMethod = HttpMethod.GET)
	public Course getCourse(@Named("websafeCourseKey") final String websafeCourseKey) throws NotFoundException {
		Key<Course> courseKey = Key.create(websafeCourseKey);
		Course course = ofy().load().key(courseKey).now();
		if (course == null) {
			throw new NotFoundException("No Course found with key: " + websafeCourseKey);
		}
		return course;
	}

	/**
	 * Just a wrapper for Boolean. We need this wrapped Boolean because
	 * endpoints functions must return an object instance, they can't return a
	 * Type class such as String or Integer or Boolean
	 */
	public static class WrappedBoolean {

		private final boolean result;
		private final String reason;

		public WrappedBoolean(boolean result) {
			this.result = result;
			this.reason = "";
		}

		public WrappedBoolean(boolean result, String reason) {
			this.result = result;
			this.reason = reason;
		}

		public boolean getResult() {
			return result;
		}

		public String getReason() {
			return reason;
		}
	}

	/**
	 * This is the method invoked for the registration into a certain course
	 * 
	 * @param user
	 *            the user who is currently logged in. This is the user who is
	 *            looking to register to the course.
	 * @param websafeCourseKey
	 *            This is the key of the course into which th user wants to
	 *            register
	 * @return Returns the status of the registration
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws ForbiddenException
	 * @throws ConflictException
	 */
	@ApiMethod(name = "registerForCourse", path = "course/{websafeCourseKey}/registration", httpMethod = HttpMethod.POST)

	public WrappedBoolean registerForCourse(final User user, @Named("websafeCourseKey") final String websafeCourseKey)
			throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		final String mainEmail = user.getEmail();

		WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {
			@Override
			public WrappedBoolean run() {
				try {

					// Get the course key
					// Will throw ForbiddenException if the key cannot be
					// created
					Key<Course> courseKey = Key.create(websafeCourseKey);

					// Get the Course entity from the datastore
					Course course = ofy().load().key(courseKey).now();

					// 404 when there is no Course with the given courseId.
					if (course == null) {
						return new WrappedBoolean(false, "No Course found with key: " + websafeCourseKey);
					}

					// Get the user's Profile entity
					Person person = getPersonProfileFromUser(user);

					// Has the user already registered to attend this course?
					if (person.getCourseKeysToAttend().contains(websafeCourseKey)) {
						return new WrappedBoolean(false, "Already registered");
					} else if (course.getSeatsAvailable() <= 0) {
						return new WrappedBoolean(false, "No seats available");
					} else {
						// All looks good, go ahead and book the seat
						person.addToCourseKeysToAttend(websafeCourseKey);
						course.registerForCourse(1);

						// Save the Course and Profile entities
						ofy().save().entities(person, course).now();
						// We are booked!
						return new WrappedBoolean(true, "Registration successful");
					}

				} catch (Exception e) {
					return new WrappedBoolean(false, "Unknown exception");

				}
			}
		});

		if (!result.getResult()) {
			if (result.getReason().contains("No Course found with key")) {
				throw new NotFoundException(result.getReason());
			} else if (result.getReason() == "Already registered") {
				throw new ConflictException("You have already registered");
			} else if (result.getReason() == "No seats available") {
				throw new ConflictException("There are no seats available");
			} else {
				throw new ForbiddenException("Unknown exception");
			}
		}
		return result;

	}

	@ApiMethod(name = "unregisterFromCourse", path = "course/{websafeCourseKey}/registration", httpMethod = HttpMethod.DELETE)
	public WrappedBoolean unregisterFromCourse(final User user,
			@Named("websafeCourseKey") final String websafeCourseKey)
			throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {
			@Override
			public WrappedBoolean run() {
				Key<Course> courseKey = Key.create(websafeCourseKey);
				Course course = ofy().load().key(courseKey).now();
				// 404 when there is no Course with the given courseId.
				if (course == null) {
					return new WrappedBoolean(false, "No Course found with key: " + websafeCourseKey);
				}

				// Un-registering from the Course.
				Person person = getPersonProfileFromUser(user);
				if (person.getCourseKeysToAttend().contains(websafeCourseKey)) {
					person.unregisterFromCourse(websafeCourseKey);
					course.deregisterFromCourse(1);
					ofy().save().entities(person, course).now();
					return new WrappedBoolean(true);
				} else {
					return new WrappedBoolean(false, "You are not registered for this course");
				}
			}
		});
		// if result is false
		if (!result.getResult()) {
			if (result.getReason().contains("No Course found with key")) {
				throw new NotFoundException(result.getReason());
			} else {
				throw new ForbiddenException(result.getReason());
			}
		}
		// NotFoundException is actually thrown here.
		return new WrappedBoolean(result.getResult());
	}

	/**
	 * This returns the Courses the user has chosen to attend
	 * 
	 * @param user
	 *            The current user
	 * @return The list of the courses that the user is attending
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 */
	@ApiMethod(name = "getCoursesToAttend", path = "getCoursesToAttend", httpMethod = HttpMethod.GET)
	public Collection<Course> getCoursesToAttend(final User user) throws UnauthorizedException, NotFoundException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		String mainEmail = user.getEmail();
		Key<Person> personKey = Key.create(Person.class, mainEmail);
		Person person = ofy().load().key(personKey).now();
		if (person == null) {
			throw new NotFoundException("Profile doesn't exist.");
		}
		List<String> keyStringsToAttend = person.getCourseKeysToAttend();
		List<Key<Course>> keysToAttend = new ArrayList<>();
		for (String keyString : keyStringsToAttend) {
			keysToAttend.add(Key.<Course> create(keyString));
		}
		return ofy().load().keys(keysToAttend).values();
	}

	/**
	 * This returns the courses that the current user has created
	 * 
	 * @param user
	 *            The current user
	 * @return THe list of courses that the current user has created
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "getCoursesCreated", path = "getCoursesCreated", httpMethod = HttpMethod.POST)
	public List<Course> getCoursesCreated(final User user) throws UnauthorizedException {
		// If not signed in, throw a 401 error.
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		String mainEmail = user.getEmail();
		Key<Person> personKey = Key.create(Person.class, mainEmail);
		return ofy().load().type(Course.class).ancestor(personKey).list();
	}

	/**
	 * This method returns a list of all the courses
	 * 
	 * @param user
	 * @return
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "getAllCourses", path = "getAllCourses", httpMethod = HttpMethod.POST)
	public List<Course> getAllCourses(final User user) throws UnauthorizedException {
		// If not signed in, throw a 401 error.
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		return ofy().load().type(Course.class).list();
	}

	@ApiMethod(name = "sayHello", path = "sayHello", httpMethod = HttpMethod.GET)
	public HelloClass sayHello(final User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		String ret;
		HelloClass h;
		ret = user.getEmail();
		h = new HelloClass(ret);
		return h;
	}

	/**
	 * This method checks to see if the current user is the owner of the
	 * supplied course
	 * 
	 * @param user
	 * @param websafeCourseKey
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws ForbiddenException
	 * @throws ConflictException
	 */
	@ApiMethod(name = "isOwner", path = "isOwner", httpMethod = HttpMethod.GET)
	public WrappedBoolean isOwner(final User user, @Named("websafeCourseKey") final String websafeCourseKey)
			throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		Key<Course> courseKey = Key.create(websafeCourseKey);

		// Get the Course entity from the datastore
		Course course = ofy().load().key(courseKey).now();

		// 404 when there is no Course with the given courseId.
		if (course == null) {
			return new WrappedBoolean(false, "No Course found with key: " + websafeCourseKey);
		}

		// Get the user's Profile entity
		Person person = getPersonProfileFromUser(user);

		// Has the user already registered to attend this course?
		if (course.getOrganizerEmail().equals(person.getEmail())) {
			return new WrappedBoolean(true, "owner");
		} else {
			return new WrappedBoolean(false, "not owner");
		}
	}

	/**
	 * This method sees if the user is regd for the course id supplied
	 * 
	 * @param user
	 * @param websafeCourseKey
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws ForbiddenException
	 * @throws ConflictException
	 */
	@ApiMethod(name = "isRegdForCourse", path = "isRegdForCourse", httpMethod = HttpMethod.GET)
	public WrappedBoolean isRegdForCourse(final User user, @Named("websafeCourseKey") final String websafeCourseKey)
			throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		Key<Course> courseKey = Key.create(websafeCourseKey);

		// Get the Course entity from the datastore
		Course course = ofy().load().key(courseKey).now();

		// 404 when there is no Course with the given courseId.
		if (course == null) {
			return new WrappedBoolean(false, "No Course found with key: " + websafeCourseKey);
		}

		// Get the user's Profile entity
		Person person = getPersonProfileFromUser(user);

		// Has the user already registered to attend this course?
		if (person.getCourseKeysToAttend().contains(websafeCourseKey)) {
			return new WrappedBoolean(true, "registered");
		} else {
			return new WrappedBoolean(false, "not registered");
		}
	}

	/**
	 * If the root folder id of the parent does not exist this method inserts
	 * this for us
	 * 
	 * @param user
	 * @param parentFolder
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "insertPersonContentRoot", path = "insertPersonContentRoot", httpMethod = HttpMethod.GET)
	public void insertPersonContentRoot(final User user, @Named("parentFolder") final String parentFolder)
			throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		Person person = getPersonProfileFromUser(user);
		person.setContentRoot(parentFolder);
		ofy().save().entity(person).now();
	}

	/**
	 * If the root folder id of the parent does not exist this method inserts
	 * this for us
	 * 
	 * @param user
	 * @param courseFolder
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "insertCourseContentRoot", path = "insertCourseContentRoot", httpMethod = HttpMethod.GET)
	public void insertCourseContentRoot(final User user, @Named("courseFolder") final String courseFolder) throws UnauthorizedException, NotFoundException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}
		tempCourseFolderId = courseFolder;
	}
	
	
	/**
	 * This is the function for the testing endpoints in android
	 * @return
	 */
	@ApiMethod(name = "justHello", path = "justHello", httpMethod = HttpMethod.GET)
	public HelloClass justHello(){
		return new HelloClass("Hello World!");
	}
	
	/**
	 * This method is used to get the profile of the user that is currently
	 * logged in.
	 * 
	 * @param userEmail
	 *            A user's email address
	 * @return The profile of the user that is currently logged in
	 */
	@ApiMethod(name = "getPersonProfileAndroid", path = "getPersonProfileAndroid", httpMethod = HttpMethod.GET)
	public Person getPersonProfileAndroid(@Named("userEmail") final String userEmail) {

		String mainEmail = userEmail;
		Key<Person> key = Key.create(Person.class, mainEmail);

		Person person = (Person) ofy().load().key(key).now();
		return person;
	}
	
	/**
	 * 
	 * @param userEmail
	 * @return
	 */
	@ApiMethod(name = "getCoursesCreatedAndroid", path = "getCoursesCreatedAndroid", httpMethod = HttpMethod.POST)
	public List<Course> getCoursesCreatedAndroid(@Named("userEmail") final String userEmail) {
		// If not signed in, throw a 401 error.
		String mainEmail = userEmail;

		Key<Person> personKey = Key.create(Person.class, mainEmail);
		return ofy().load().type(Course.class).ancestor(personKey).list();
	}
	
	/**
	 * 
	 * @param userEmail
	 * @return
	 */
	@ApiMethod(name = "getAllCoursesAndroid", path = "getAllCoursesAndroid", httpMethod = HttpMethod.POST)
	public List<Course> getAllCoursesAndroid(@Named("userEmail") final String userEmail) {
		// If not signed in, throw a 401 error.
		return ofy().load().type(Course.class).list();
	}
	
	@ApiMethod(name = "justHelloWithTwoNames", path = "justHelloWithTwoNames", httpMethod = HttpMethod.GET)
	public HelloClass justHelloWithTwoNames(@Named("greeting") final String greeting,@Named("name") final String name){
		return new HelloClass(greeting + name);
	}
	
	@ApiMethod(name = "fileUploadedName", path = "fileUploadedName", httpMethod = HttpMethod.POST)
	public void fileUploadedName(@Named("name") final String name){
		fileName = name;
	}
	
	@ApiMethod(name = "fileUploadedLink", path = "fileUploadedLink", httpMethod = HttpMethod.POST)
	public void fileUploadedLink(@Named("link") final String link){
		fileLink = link;
	}
	
	@ApiMethod(name = "fileUploaded", path = "fileUploaded", httpMethod = HttpMethod.POST)
	public void fileUploaded(@Named("websafeCourseKey") final String websafeCourseKey)
		throws NotFoundException{
		Key<Course> courseKey = Key.create(websafeCourseKey);

		// Get the Course entity from the datastore
		Course course = ofy().load().key(courseKey).now();

		// 404 when there is no Course with the given courseId.
		if (course == null) {
			throw new NotFoundException("No Course found with key: " + websafeCourseKey);
		}
		
		course.addToFiles(fileName, fileLink);
		
		ofy().save().entity(course).now();
		
		fileName = "";
		fileLink = "";
	}
	
	/**
	 * This returns the list of all the files available with the current course
	 * @param websafeCourseKey
	 * @return
	 * @throws NotFoundException
	 */
	@ApiMethod(name = "getFilesOfCourse", path = "getFilesOfCourse", httpMethod = HttpMethod.GET)
	public List<FileInfo> getFilesOfCourse(@Named("websafeCourseKey") final String websafeCourseKey)
		throws NotFoundException{
		Key<Course> courseKey = Key.create(websafeCourseKey);

		// Get the Course entity from the datastore
		Course course = ofy().load().key(courseKey).now();

		// 404 when there is no Course with the given courseId.
		if (course == null) {
			throw new NotFoundException("No Course found with key: " + websafeCourseKey);
		}
		
		List<FileInfo> fi = course.getCourseFiles();
		
		return fi;
	}
	
	
	/**
	 * This is the function to be called when the file is starting to be presented
	 * That is when the file is clicked in the android app.
	 * The parameter is the id of the file who is clicked
	 * @param id
	 */
	@ApiMethod(name = "putFileToPresent", path = "putFileToPresent", httpMethod = HttpMethod.POST)
	public void putFileToPresent(@Named("id") final String id){
		fileIdToPresent = id;
	}
	
	/**
	 * This tells which file to present
	 * @return
	 */
	@ApiMethod(name = "getFileToPresent", path = "getFileToPresent", httpMethod = HttpMethod.GET)
	public HelloClass getFileToPresent(){
		String ret = fileIdToPresent;
		fileIdToPresent = "";
		return new HelloClass(ret);
	}
	
	
	/**
	 * This function tells if there is any action to be taken in the file that is being presented currently
	 * would return either of these:
	 *  - next
	 *  - prev
	 *  - stop
	 * @return
	 */
	@ApiMethod(name = "getActionToBeTaken", path = "getActionToBeTaken", httpMethod = HttpMethod.GET)
	public HelloClass getActionToBeTaken(){
		String ret = presentActionToBeTaken;
		presentActionToBeTaken = "";
		return new HelloClass(ret);
	}
	
	/**
	 * This could have three types of actions:
	 * 	- next
	 *  - prev
	 *  - stop
	 *  exactly like this
	 * @param action
	 */
	@ApiMethod(name = "setActionToBeTaken", path = "setActionToBeTaken", httpMethod = HttpMethod.POST)
	public void setActionToBeTaken(@Named("action") final String action){
		presentActionToBeTaken = action;
	}
	
}