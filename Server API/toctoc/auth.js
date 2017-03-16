const Promise = require('bluebird');
const passport = require('passport');
const OAuth2Strategy = require('passport-oauth2');
const BearerStrategy = require('passport-http-bearer');
const request = require('request');

const config = require('./config');
const Person = require('./models/person');
const {getSessionUser} = require('./actions/device-session');
const createOrUpdateUser = require('./actions/create-or-update-user');

// user serialization and deserialization
passport.serializeUser(function(user, done) {
  done(null, user._id);
});

passport.deserializeUser(function(userID, done) {
  Person.findById(userID, done);
});

// create the strategy
const jlmStrategy = new OAuth2Strategy({
    authorizationURL: config.authorizationURL,
    tokenURL: config.tokenURL,
    clientID: config.clientID,
    clientSecret: config.clientSecret,
    scope: config.scopes.join(config.scopeSeparator),
    callbackURL: config.redirectURL
  },
  function (accessToken, refreshToken, profile, done) {
    createOrUpdateUser(accessToken, refreshToken, profile)
      .then((user) => done(null, user))
      .catch((err) => done(err));
  }
);

jlmStrategy.userProfile = function (accessToken, done) {
  request({
    url: config.profileURL,
    json: true,
    auth: {bearer: accessToken}
  }, function (err, res, body) {
    if (err) {
      return done(err);
    }
    if (res.statusCode === 404) {
      return done(new Error('not found'));
    }
    if (res.statusCode !== 200) {
      return done(new Error('unknown error'));
    }

    return done(null, body);
  });
};

const deviceStrategy = new BearerStrategy(function verifyDevice(token, done) {
  return getSessionUser(token).then((user) => {
    done(null, user);
  }).catch(done);
});

passport.use('jlm-auth', jlmStrategy);
passport.use('device-auth', deviceStrategy);

exports.connect = passport.authenticate('jlm-auth');

exports.oauthCallback = passport.authenticate('jlm-auth', { successReturnToOrRedirect: '/', failureRedirect: '/'});

exports.verify = [
  passport.authenticate('device-auth', {session: false}),
  function(req, res) {
    res.status(200).send(req.user);
  }
];

exports.disconnect = function(req, res) {
  req.logout();
  res.redirect('/');
};
