var mysql = require('mysql');

var dbConn = mysql.createConnection({
	connectionLimit : 100, //important
	host     : process.env.CB_MYSQL_SERVER,
	user     : process.env.CB_MYSQL_USER,
	password : process.env.CB_MYSQL_PASSWORD,
	port     : process.env.CB_MYSQL_PORT,
	database : process.env.CB_MYSQL_DB_NAME
});



function queryUsers() {
	dbConn.query('SELECT * FROM users', function(err, rows) {
		// connected! (unless `err` is set)
		if (err) throw err;

		console.log('The solution is: ', rows[0]);
	});
}

exports.connect = function () {
        console.log('db connection started.. ' + dbConn);
	queryUsers();
}
