const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const ObjectId = Schema.ObjectId;

const Person = new Schema({
  id: Number,
  email: String,
  first_name: String,
  last_name: String,
  city: String,
  country_code: String,
  access_token: String,
  refresh_token: String
});

module.exports = mongoose.model('Person', Person);
