const Promise = require('bluebird');

const config = require('../config');
const redisClient = require('../io/redis_client');
const Person = require('../models/person');
const Device = require('../models/device');

function redisKey(token) {
  return `${config.prefix}device:${token}:person`;
}

function touchSession(token) {
  return redisClient.expireAsync(redisKey(token), config.associationExpire);
}

exports.setUpSession = function (token, person) {
  return redisClient.setAsync(redisKey(token), person._id.toString(), 'EX', config.associationExpire);
};

exports.getSessionUser = Promise.coroutine(function* getSessionUser(token) {
  const personId = yield redisClient.getAsync(redisKey(token));

  if (personId === null) {
    return null;
  }

  yield touchSession(token);

  const person = yield Person.findById(personId);
  if (person === null) {
    return null;
  }

  person.device = yield Device.findOne({token});
  person.token = token;

  return person;
});
