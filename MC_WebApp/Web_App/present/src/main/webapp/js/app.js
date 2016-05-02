'use strict';

/**
 * @ngdoc object
 * @name presentItApp
 * @requires $routeProvider
 * @requires presentItControllers
 * @requires ui.bootstrap
 * 
 * @description Root app, which routes and specifies the partial html and
 *              controller depending on the url requested.
 * 
 */

var app = angular.module('app', [ 'ngRoute' ]).config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		templateUrl : 'partials/landing.html',
		controller : 'LandingCtrl'
	}).when('/profile/course/:websafeKey', {
		templateUrl : 'partials/coursePage.html',
		controller : 'CourseShowController'
	}).when('/profile/course/:websafeKey/upload', {
		templateUrl : 'partials/upload.html',
		controller : 'UploadController'
	}).when('/profile/home', {
		templateUrl : 'partials/home.html',
		controller : 'HomeCtrl'
	}).when('/profile/search/course', {
		templateUrl : 'partials/search.html',
		controller : 'SearchCourseCtrl'
	}).when('/profile/create/course', {
		templateUrl : 'partials/createCourse.html',
		controller : 'CourseCreateCtrl'
	}).when('/profile/edit', {
		templateUrl : 'partials/profile.html',
		controller : 'ProfileCtrl'
	}).otherwise({
		redirectTo : '/'
	});
} ]);

/*
 * The Controllers start from here All that is before this is the app properties
 * and after this is the app controllers
 * 
 * 
 * 
 * 
 */
app.controller('LandingCtrl', function($scope) {
	$scope.msg = "Really!!";
});

app.controller('HomeCtrl', function($scope, $rootScope, $location, $http) {
	
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.goToSearch = function() {
		$location.path("/profile/search/course");
	};
	
	$scope.goToProfile = function() {
		$location.path("/profile/edit");
	};
	
	$scope.goToCourseCreate = function() {
		$location.path("/profile/create/course");
	};
	
	$scope.sayHello = function() {
		gapi.client.presentIt.getCoursesCreated().execute(function(resp) {
			console.log(resp);
		});
	};
	
	$scope.localInit = function() {
		console.log("The init inside the home ctrl");
		gapi.client.presentIt.getCoursesCreated().execute(function(resp) {
			$scope.$apply(function() {
				$scope.coursesCreated = resp.items;
			});
		});
		
		gapi.client.presentIt.getCoursesToAttend().execute(function(resp) {
			$scope.$apply(function() {
				$scope.coursesAttended = resp.items;
			});
		});
	};
	
	$scope.takeToCourse = function(key) {
		$location.path("/profile/course/" + key);
	};
	
});

/**
 * The beginning of the course search controller
 * 
 */
app.controller('SearchCourseCtrl', function($scope, $rootScope, $location, $http) {
	
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.goToSearch = function() {
		$location.path("/profile/search/course");
	};
	
	$scope.goToProfile = function() {
		$location.path("/profile/edit");
	};
	
	$scope.goToCourseCreate = function() {
		$location.path("/profile/create/course");
	};
	
	$scope.sayHello = function() {
		gapi.client.presentIt.getCoursesCreated().execute(function(resp) {
			console.log(resp);
		});
	};
	
	$scope.localInit = function() {
		console.log("The init inside the search ctrl");
		gapi.client.presentIt.getAllCourses().execute(function(resp) {
			$scope.$apply(function() {
				$scope.coursesCreated = resp.items.slice();
			});
		});
	};
	
	$scope.takeToCourse = function(key) {
		$location.path("/profile/course/" + key);
	};
	
});

/**
 * The start of the profile ctrl
 */
app.controller('ProfileCtrl', function($scope, $rootScope, $location, $http) {
	
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.goToSearch = function() {
		$location.path("/profile/search/course");
	};
	
	$scope.goToProfile = function() {
		$location.path("/profile/edit");
	};
	
	$scope.goToCourseCreate = function() {
		$location.path("/profile/create/course");
	};
	
	$scope.sayHello = function() {
		gapi.client.presentIt.getCoursesCreated().execute(function(resp) {
			console.log(resp);
		});
	};
	
	$scope.localInit = function() {
		console.log("The init inside the profile ctrl");
		gapi.client.presentIt.getPersonProfile().execute(function(resp) {
			console.log('The person profile');
			console.log(resp);
			$scope.$apply(function() {
				$scope.userEmail = resp.email;
				$scope.profile = {
					firstName : resp.firstName,
					lastName : resp.lastName,
					ufid : resp.ufid
				};
			});
		});
	};
	
	$scope.updateProfile = function() {
		gapi.client.presentIt.savePersonProfile($scope.profile).execute(function(resp) {
			console.log('The person profile update');
			console.log(resp);
		});
	};
	
});

/**
 * The start of the course create ctrl
 */
app.controller('CourseCreateCtrl', function($scope, $rootScope, $location, $http) {
	
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.goToSearch = function() {
		$location.path("/profile/search/course");
	};
	
	$scope.goToProfile = function() {
		$location.path("/profile/edit");
	};
	
	$scope.goToCourseCreate = function() {
		$location.path("/profile/create/course");
	};
	
	$scope.sayHello = function() {
		gapi.client.presentIt.getCoursesCreated().execute(function(resp) {
			console.log(resp);
		});
	};
	
	$scope.localInit = function() {
		console.log("The init inside the course create ctrl");
		$scope.courseCreate = {
			courseId : "Course-Id",
			courseName : "courseName",
			maxAttendees : "Max Strength",
			description : "The description of the course"
		};
	};
	
	$scope.createCourse = function() {
		var parentF;
		var courseFolderId;
		
		gapi.client.presentIt.getPersonProfile().execute(function(resp) {
			console.log(resp);
			$scope.$apply(function(){
				/*
				 * The beginning of the 000000000 check
				 */
				if (resp.contentRoot === "0000000000") {
					console.log("the check has passed");
					gapi.auth.authorize({
						client_id : '210330844927-r3p153soqj082oi4igbmnuifnq0dj9ov.apps.googleusercontent.com',
						scope : "https://www.googleapis.com/auth/drive",
						immediate : false
					}, function() {
						gapi.client.load('drive', 'v2', function() {
							var request = gapi.client.drive.files.insert({
								"mimeType" : "application/vnd.google-apps.folder",
								"title" : "presentit"
							});
							
							request.execute(function(respD) {
								console.log(respD);
								parentF = respD.id;
								gapi.client.presentIt.insertPersonContentRoot({
									"parentFolder" : respD.id
								}).execute(function(respF) {
									console.log(respF);
								});
								/*
								 * This is the part where we start the upload of the course folder
								 */
								var request2 = gapi.client.drive.files.insert({
									"mimeType" : "application/vnd.google-apps.folder",
									"title" : $scope.courseCreate.courseName,
									"parents" : [ {
										"kind" : "drive#fileLink",
										"id" : parentF
									} ]
								});
								
								request2.execute(function(respC){
									gapi.client.presentIt.insertCourseContentRoot({
										"courseFolder" : respC.id
									}).execute(function(){
										//do nothing. This call has just placed the courseContentRoot temp
										//Maybe here is where you actually create the course instead of seperately
										gapi.client.presentIt.createCourse($scope.courseCreate).execute(function(respCC) {
											console.log(respCC);
										});
										//Now the course has been created
									});
									console.log(respC);
								});
								/*
								 * And this is where it ends
								 */
							});
						});
					});
				} else {
					parentF = resp.contentRoot;
					/*
					 * This is the course folder upload code for the else part
					 */
					gapi.auth.authorize({
						client_id : '210330844927-r3p153soqj082oi4igbmnuifnq0dj9ov.apps.googleusercontent.com',
						scope : "https://www.googleapis.com/auth/drive",
						immediate : false
					}, function() {
						gapi.client.load('drive', 'v2', function() {
							var request = gapi.client.drive.files.insert({
								"mimeType" : "application/vnd.google-apps.folder",
								"title" : $scope.courseCreate.courseName,
								"parents" : [ {
									"kind" : "drive#fileLink",
									"id" : parentF
								} ]
							});
							
							request.execute(function(respD) {
								gapi.client.presentIt.insertCourseContentRoot({
									"courseFolder" : respD.id
								}).execute(function(){
									//do nothing. This call has just placed the courseContentRoot temp
									gapi.client.presentIt.createCourse($scope.courseCreate).execute(function(respCC) {

										console.log(respCC);
									});
									// Now the course has been created
								});
								console.log(respD);
								
							});
						});
					});
					/*
					 * This is where the code for the else part ends
					 */
					
				}
				/*
				 * THe end of the 000000000 check
				 */
			});
		});
		
		/*
		 * Beyond this point we will have a presentIt folder and its assc folder
		 * id
		 */
		
	};
});

/**
 * The start of the course show ctrl
 */
app.controller('CourseShowController', function($scope, $location, $routeParams) {
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.registerForCourse = function() {
		gapi.client.presentIt.registerForCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				if (resp.result) {
					$scope.isRegd = true;
					$scope.isNotRegd = false;
				}
			});
		});
	};
	
	$scope.unregisterFromCourse = function() {
		gapi.client.presentIt.unregisterFromCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				if (resp.result) {
					$scope.isRegd = false;
					$scope.isNotRegd = true;
				}
			});
		});
	};
	
	$scope.uploadDocs = function() {
		$location.path("/profile/course/" + $routeParams.websafeKey + "/upload");
	};
	
	$scope.goToPool = function() {
		
	};
	
	$scope.gotToArchives = function() {
		
	};
	
	$scope.localInit = function() {
		console.log("The course display init");
		console.log($routeParams.websafeKey);
		gapi.client.presentIt.getCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				console.log(resp);
				$scope.courseDescription = resp.description;
				$scope.orgEmail = resp.organizerEmail;
			});
		});
		
		// is owner
		gapi.client.presentIt.isOwner({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				console.log(resp);
				// $scope.isOwn = resp.result;
				if (resp.reason === "not owner") {
					$scope.isOwn = false;
				} else {
					$scope.isOwn = true;
				}
			});
		});
		
		// is regd
		gapi.client.presentIt.isRegdForCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				console.log(resp);
				// $scope.isRegd = resp.boolean;
				// $scope.isNotRegd = !resp.boolean;
				
				if (resp.reason === "not registered") {
					$scope.isRegd = false;
					$scope.isNotRegd = true;
				} else {
					$scope.isRegd = true;
					$scope.isNotRegd = false;
				}
			});
		});
	};
});



/**
 * The start of the course show ctrl
 */
app.controller('UploadController', function($scope, $location, $routeParams) {
	$scope.goHome = function() {
		$location.path("/profile/home");
	};
	
	$scope.registerForCourse = function() {
		gapi.client.presentIt.registerForCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				if (resp.result) {
					$scope.isRegd = true;
					$scope.isNotRegd = false;
				}
			});
		});
	};
	
	$scope.unregisterFromCourse = function() {
		gapi.client.presentIt.unregisterFromCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				if (resp.result) {
					$scope.isRegd = false;
					$scope.isNotRegd = true;
				}
			});
		});
	};
	
	$scope.uploadDocs = function() {
		$location.path("/profile/course/" + $routeParams.websafeKey + "/upload");
	};
	
	$scope.goToPool = function() {
		
	};
	
	$scope.gotToArchives = function() {
		
	};
	
	$scope.uploadFile = function(){
		var folderId;
		var fileId;
		console.log("the upload function");
		//Here the logic to actually upload stuff happens
		gapi.client.presentIt.getCourse({
			websafeCourseKey : $routeParams.websafeKey}).execute(function(resp){
			console.log(resp);
			folderId = resp.contentRoot;
			gapi.auth.authorize({
				client_id : '210330844927-r3p153soqj082oi4igbmnuifnq0dj9ov.apps.googleusercontent.com',
				scope : "https://www.googleapis.com/auth/drive",
				immediate : false
			}, function() {
				gapi.client.load('drive', 'v2', function(){
					const boundary = '-------314159265358979323846';
					const delimiter = "\r\n--" + boundary + "\r\n";
					const close_delim = "\r\n--" + boundary + "--";
					var filePicker = document.getElementById("filePicker");
					var fileData;
					
					if ('files' in filePicker) {
						if (filePicker.files.length == 0) {
				            console.log("No Files selected");
				        }
						else {
				            fileData = filePicker.files[0];
				        }
					}
					console.log("File data");
					console.log(fileData);
					var reader = new FileReader();
					reader.readAsBinaryString(fileData);
					reader.onload = function(e) {
						var contentType = fileData.type || 'application/octet-stream';
						var metadata = {
								'title': fileData.name,
								'mimeType': 'application/vnd.google-apps.presentation',
								"parents" : [ {
									"kind" : "drive#fileLink",
									"id" : folderId
								} ]
						};

						var base64Data = btoa(reader.result);
						var multipartRequestBody =
							delimiter +
							'Content-Type: application/json\r\n\r\n' +
							JSON.stringify(metadata) +
							delimiter +
							'Content-Type: ' + contentType + '\r\n' +
							'Content-Transfer-Encoding: base64\r\n' +
							'\r\n' +
							base64Data +
							close_delim;

						var request = gapi.client.request({
							'path': '/upload/drive/v2/files',
							'method': 'POST',
							'params': {'uploadType': 'multipart'},
							'headers': {
								'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
							},
							'body': multipartRequestBody});
						request.execute(function(respD){
							//Here we call the fileUploaded function
							console.log(respD);
							fileId = respD.id;
							gapi.client.presentIt.fileUploadedName({
								"name" : respD.title
							}).execute();
							gapi.client.presentIt.fileUploadedLink({
								"link" : respD.id
							}).execute();
							gapi.client.presentIt.fileUploaded({
								websafeCourseKey : $routeParams.websafeKey
							}).execute();
						});
					}
				});
			});
		});
	};
	
	$scope.localInit = function() {
		console.log("The course upload init");
		console.log($routeParams.websafeKey);
		
		// is owner
		gapi.client.presentIt.isOwner({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				console.log(resp);
				// $scope.isOwn = resp.result;
				if (resp.reason === "not owner") {
					$scope.isOwn = false;
				} else {
					$scope.isOwn = true;
				}
			});
		});
		
		// is regd
		gapi.client.presentIt.isRegdForCourse({
			websafeCourseKey : $routeParams.websafeKey
		}).execute(function(resp) {
			$scope.$apply(function() {
				console.log(resp);
				// $scope.isRegd = resp.boolean;
				// $scope.isNotRegd = !resp.boolean;
				
				if (resp.reason === "not registered") {
					$scope.isRegd = false;
					$scope.isNotRegd = true;
				} else {
					$scope.isRegd = true;
					$scope.isNotRegd = false;
				}
			});
		});
	};
});

app.controller('RootCtrl', function($scope, $location, $http) {
	var user;
	$scope.show = true;
	$scope.hide = false;
	
	$scope.getSignedInState = function() {
		return user.isSignedIn();
	};
	
	$scope.signIn = function() {
		var auth2 = gapi.auth2.getAuthInstance();
		auth2.signIn().then(function(googleUser) {
			$scope.$apply(function(){
				user = googleUser;
				$scope.show = false;
				$scope.hide = true;
				$location.path("/profile/home");
				var profile = user.getBasicProfile();
				$scope.userName = profile.getName();
				$scope.imgSrc = profile.getImageUrl();
				console.log("the progfile picture URL");
				console.log($scope.imgSrc);
				// auth2.grantOfflineAccess({'redirect_uri':
				// 'postmessage'}).then(function(authResult)
				// {
				// if (authResult['code']) {
				// //$http.post('/someUrl',
				// authResult['code']).then(successCallback,
				// errorCallback);
				// } else {
				// // There was an error.
				// }
				// });
				
				gapi.auth.authorize({
					client_id : '210330844927-r3p153soqj082oi4igbmnuifnq0dj9ov.apps.googleusercontent.com',
					scope : 'https://www.googleapis.com/auth/userinfo.email',
					immediate : false
				}, function() {
				});
			});
			
		}, function(error) {
		});
		
	};
	
	$scope.signOut = function() {
		var auth2 = gapi.auth2.getAuthInstance();
		auth2.signOut().then(function() {
			$scope.$apply(function(){
				$scope.show = true;
				$scope.hide = false;
				$location.path("/");
				$scope.msg = "Guest!";
			});
		}, function(error) {
		});
		
		
	};
	
	$scope.sayHello = function() {
		// gapi.client.helloworldendpoints.sayHelloByName().execute(function(resp)
		// {
		// $scope.msg = resp.message;
		// console.log("Hello");
		// });
	};
	
});