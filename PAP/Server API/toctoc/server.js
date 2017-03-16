const Promise = require('bluebird');
const express = require('express');
const bodyParser = require('body-parser');
const routes = require('./routes');
const mongoose = require("mongoose");
const session = require('express-session');
const morgan = require('morgan');
const passport = require('passport');

const config = require('./config');

const app = express();

mongoose.Promise = Promise;
mongoose.connect('mongodb://127.0.0.1:27017/JLMPaP');
/*
 mongoose.connection.collections['portes'].drop( function(err) {
 console.log('collection dropped');
 });
*/

// set up logging
app.use(morgan('combined'));

// express app will use body-parser to get data from POST
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
app.use(session({secret: config.cookieSecret, name: 'pap.session'}));
app.use(passport.initialize());
app.use(passport.session());


// Set port
const port = config.port;

// Define a prefix for all routes
// Can define something unique like MyRestAPI
// We'll just leave it so all routes are relative to '/'
app.use('/', routes);

// Start server listening
app.listen(port);
console.log('RESTAPI listening on port: ' + port);
