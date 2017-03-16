const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const ObjectId = Schema.ObjectId;

const Porte = new Schema({
  adresseResume: String,
  complement: String,
  nom_rue: String,
  nom_ville: String,
  numS: String,
  numA: String,
  ouverte: Boolean,
  latitude: Number,
  longitude: Number,
  person: ObjectId,
  device: ObjectId
});

module.exports = mongoose.model('Porte', Porte);
