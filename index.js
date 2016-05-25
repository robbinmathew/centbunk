/*===============INCLUDE modules ===============*/

/* Express related modules */
var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var session = require('express-session');
var https = require('https');
var http = require('http');
var fs = require('fs');
var proxy = require('express-http-proxy');


/* Non-Express modules for creating views*/
var expHandleBar = require('express-handlebars');

var apiHandler = require('./lib/routes/cb-handler');

/* Module required for authentication*/
var passport = require('passport');
var LocalStrategy = require('passport-local');

/* Local module for authentication*/
var cbAuth = require('./cb-auth.js');
//var cbDb = require('./cb-db.js');
/*===============INCLUDE modules ===============*/



/*===============INIT the app ==================*/
var app = express();

app.use(session({secret: 'asdfasdf123123', saveUninitialized: true, resave: false}));
//TODO: add cookie as secure as below
//app.use(session({secret: 'asdfasdf123123', saveUninitialized: true, resave: false, cookie: {secure: true}}));
app.use(passport.initialize());
app.use(passport.session());

app.use('/api*', proxy('localhost:8081', {
	forwardPath: function(req, res) {
		return require('url').parse(req.originalUrl).path;
	}
}));

app.use(logger('combined')); /*combined is the apache format for logging*/
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(methodOverride('X-HTTP-Method-Override'));


// Session-persisted message middleware
app.use(function(req, res, next){
	var err = req.session.error;
	var success = req.session.success;
	var notice = req.session.notice;

	if (err) {
		res.locals.error = err;
		console.log('error: ' + err);
	}

	if (notice) {
		res.locals.notice = notice;
		console.log('notice: ' + notice);
	}

	if (success) {
		res.locals.success = success;
		console.log('success: ' + success);
	}

	delete req.session.error;
	delete req.session.success;
	delete req.session.notice;
	next();
});





app.use(express.static("resources"));

var handleBar = expHandleBar.create({
	defaultLayout: 'main',
});

app.engine('handlebars', handleBar.engine);
app.set('view engine', 'handlebars');

/*===============INIT the app ==================*/


/*===============Routes setup ==================*/

app.get('/', function(req, res){
	res.render('home', {user: req.user});
});

app.get('/unauth', function(req, res){
	res.render('unauth');
});

app.post('/login', passport.authenticate('local-signin', { 
	successRedirect: '/',
  	failureRedirect: '/unauth'
  	})
);

app.get('/logout', function(req, res){
	var name = req.user.username;
  	console.log("LOGGIN OUT " + req.user.username)
  	req.logout();
  	res.redirect('/');
  	req.session.notice = "You have successfully been logged out " + name + "!";
});

app.use('/apiv2', apiHandler)

/*===============Routes setup ==================*/

/*===============DB connect start==================*/

//cbDb.connect();

/*===============DB connect end==================*/


/*===============Passport setup start ==================*/
passport.use('local-signin', new LocalStrategy(
	//allows us to pass back the request to the callback
  	{passReqToCallback : true},
  	function(req, username, password, done) {
    		cbAuth.localAuth(username, password)
    		.then(function (user) {
			if (user) {
				console.log("LOGGED IN AS: " + user);
				req.session.success = 'You are successfully logged in ' + user+ '!';
				done(null, user);
			} else {
				console.log("COULD NOT LOG IN");
				//inform user could not log them in
				req.session.error = 'Could not log user in. Please try again.';
				done(null, user);
			}
		})
		.fail(function (err){
			console.log(err.body);
		});
	}
));

passport.serializeUser(function(user, done) {
	console.log("SERIALIZING " + user);
  	done(null, user);
});

passport.deserializeUser(function(user, done) {
  	console.log("DESERIALIZING " + user);
  	done(null, user);
});
/*===============Passport setup end ==================*/

/*===============Start the app====================*/
var key = fs.readFileSync(process.env.CB_HTTPS_KEY);
var cert = fs.readFileSync(process.env.CB_HTTPS_CERT);

var https_options = {
	key: key,
	cert: cert
};

// Redirect from http port to https

http.createServer(function (req, res) {
	if(req.headers['host']) {
		var redirectURL = req.headers['host'].replace(process.env.CB_HTTP_PORT, process.env.CB_HTTPS_PORT) + req.url;
		res.writeHead(301, { "Location": "https://" + redirectURL});
		console.log("http request redirect: " + redirectURL);
	}
	res.end();
}).listen(process.env.CB_HTTP_PORT);

server = https.createServer(https_options, app).listen(process.env.CB_HTTPS_PORT);

console.log("listening on: " + process.env.CB_HTTP_PORT);
/*===============Start the app====================*/

