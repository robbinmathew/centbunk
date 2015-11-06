/*===============INCLUDE modules ===============*/

/* Express related modules */
var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var session = require('express-session');

/* Non-Express modules for creating views*/
var expHandleBar = require('express-handlebars');

/* Module required for authentication*/
var passport = require('passport');
var LocalStrategy = require('passport-local');

/* Local module for authentication*/
var cbAuth = require('./cb-auth.js');
/*===============INCLUDE modules ===============*/



/*===============INIT the app ==================*/
var app = express(); 

app.use(logger('combined')); /*combined is the apache format for logging*/
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(methodOverride('X-HTTP-Method-Override'));
app.use(session({secret: 'asdfasdf123123', saveUninitialized: true, resave: false})); //TODO: add cookie as secure as below
//app.use(session({secret: 'asdfasdf123123', saveUninitialized: true, resave: false, cookie: {secure: true}}));
app.use(passport.initialize());
app.use(passport.session());

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

app.get('/signin', function(req, res){
  res.render('signin');
});

app.post('/login', passport.authenticate('local-signin', { 
  successRedirect: '/',
  failureRedirect: '/signin'
  })
);

app.get('/logout', function(req, res){
  var name = req.user.username;
  console.log("LOGGIN OUT " + req.user.username)
  req.logout();
  res.redirect('/');
  req.session.notice = "You have successfully been logged out " + name + "!";
});

/*===============Routes setup ==================*/


/*===============Passport setup ==================*/
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
      }
      if (!user) {
        console.log("COULD NOT LOG IN");
        req.session.error = 'Could not log user in. Please try again.'; //inform user could not log them in
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
/*===============Passport setup ==================*/

/*===============Start the app====================*/
var port = 8000;
app.listen(port);
console.log("listening on: " + port);
/*===============Start the app====================*/

