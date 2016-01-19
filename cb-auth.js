var brypt = require('bcryptjs');
var Q = require('q');

/*Hard-coded users and password*/
var authorized_users = [
	['robin', 'robin123'],
	['thomas', 'thomas123'],
	['robbin', 'Robbin@123'],
	['prabin', 'Prabin@123'],
];

exports.localAuth = function (username, password) {
	var deferred = Q.defer();
	var found=false;

	console.log("username: " + username);
	console.log("password: " + password);

	for(var i=0; i < authorized_users.length; i++) {
		if (username == authorized_users[i][0]) {
			/*Found the username */
			if (password == authorized_users[i][1]) {
				deferred.resolve(username);
				found=true;
			}
		}
	}
	if(!found) {
		console.log("Error: Unknown user");
		deferred.resolve(false);
	}

	return deferred.promise;
}
