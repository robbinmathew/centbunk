var brypt = require('bcryptjs');
var Q = require('q');

/*Hard-coded users and password*/
var authorized_users = [
	['robin', 'robin123', "user"],
	['thomas', 'thomas123', "user"],
	['robbin', 'Robbin@123', "admin"],
	['prabin', 'Prabin@123', "admin"],
];

exports.localAuth = function (username, password) {
	var deferred = Q.defer();
	var found=false;

	console.log("username: " + username);
	console.log("password: " + password);

	var user = findUserByName(username);
	if (user) {
		/*Found the username */
		if (password == user[1]) {
			deferred.resolve(user);
			found=true;
		}
	}
	if(!found) {
		console.log("Error: Unknown user");
		deferred.resolve(false);
	}

	return deferred.promise;
}

var findUserByName = function(username) {
	for(var i=0; i < authorized_users.length; i++) {
		if (username == authorized_users[i][0]) {
			return authorized_users[i];
		}
	}
}

exports.findUserByName = findUserByName;


