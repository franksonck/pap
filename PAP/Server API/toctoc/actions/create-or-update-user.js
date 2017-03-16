const Promise = require('bluebird');

const Person = require('../models/person');

module.exports = Promise.coroutine(function* createOrUpdateUser(accessToken, refreshToken, profile) {
  // using the id from jlm-auth
  let user = yield Person.findOne({id: profile.id});
  if (user === null) {
    user = new Person({id: profile.id});
  }

  Object.assign(user, {
    accessToken,
    refreshToken,
    email: profile.email,
    first_name: profile.first_name,
    last_name: profile.last_name,
    city: profile.location.city,
    country_code: profile.location.country_code
  });

  yield user.save();

  return user;
});
