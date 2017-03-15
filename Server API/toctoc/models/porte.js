var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var porteSchema   = new Schema({
    adresseResume: String,
	complement: String,
	nom_rue: String,
	nom_ville: String,
    numS: String,
	numA: String,
    ouverte: Boolean,
	user_id: String,
	location: {
      type: [Number],
      index: '2d'}
	});
module.exports = mongoose.model('Porte', porteSchema);
