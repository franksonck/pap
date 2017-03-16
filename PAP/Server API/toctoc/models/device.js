const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const ObjectId = Schema.ObjectId;

const Device = new Schema({
  token: String,
  associations: [{person: ObjectId, date: Date}]
});

module.exports = mongoose.model('Device', Device);