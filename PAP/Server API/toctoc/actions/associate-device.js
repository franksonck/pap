const Promise = require('bluebird');

const Device = require('../models/device');
const redisClient = require('../io/redis_client');

const {setUpSession} = require('./device-session');

module.exports = Promise.coroutine(function* associateDevice(token, person) {
  // the association is first saved in the database
  const device = yield Device.findOne({ token });
  const association = {person: person._id, date: new Date()};
  if (device === null) {
    yield Device.create({token, associations: [association]});
  }
   else {
    const lastUser = device.associations[device.associations.length - 1];
    if ((!lastUser) || lastUser.person != person._id) {
      device.associations.push(association);
      yield device.save()
    }
  }

  // let's save the session
  yield setUpSession(token, person);
});
