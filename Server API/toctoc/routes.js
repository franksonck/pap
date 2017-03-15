var express = require('express');

// Get the router
var router = express.Router();
var Porte = require ('./models/porte.js');
// Middleware for all this routers requests
router.use(function timeLog(req, res, next) {
  console.log('Request Received: ', dateDisplayed(Date.now()));
  res.setHeader("Access-Control-Allow-Origin", "*");
  next();
});

// Welcome message for a GET at http://localhost:8080/restapi
router.get('/', function(req, res) {
    res.json({ message: 'Welcome to toctoc' });
});

router.route('/portes').get (function (req,res) {
  Porte.find(function(err, portes){
    if (err)
      res.send (err);
    res.json(portes);
  });
});

router.route('/verifier').get (function (req,res) {
    res.send ("fail");
});

router.route('/portes').post (function (req,res) {
  var porte           	= new Porte();
  porte.adresseResume	= req.body.porte.adresseResume;
  porte.complement      = req.body.porte.complement;
  porte.nom_rue        	= req.body.porte.nom_rue;
  porte.nom_ville       = req.body.porte.nom_ville;
  porte.numS        	= req.body.porte.numS;
  porte.numA        	= req.body.porte.numA;
  porte.ouverte       	= req.body.porte.ouverte;
  porte.latitude      	= req.body.porte.latitude;
  porte.longitude		= req.body.porte.longitude;

  porte.save(function(err) {
    if (err)
      res.send (err);
    res.json ({message: 'Nouvelle porte cree avec success'});
  });
});

router.route('/portes/:porte_id').get(function(req,res){
    Portes.findById(req.params.porte_id, function (err, porte){
      if (err)
        res.send(err);
      res.json(porte);
    });
});

router.route('/proches').get(function(req,res){
    var limit       = req.query.limit || 10;
    var maxDistance = req.query.distance || 8;
    maxDistance /= 6371;
    var coords = [];
    coords[0] = req.query.lon;
    coords[1] = req.query.lat;
    console.log(coords);
    console.log(limit);
    console.log(maxDistance);
    Porte.find({ location: {
                 $near: coords,
                 $maxDistance: maxDistance
                      }
                }).limit(limit).exec(function(err, locations){
                  if (err)
                    res.status(500).send(err);
                  res.status(200).json(locations);
                });
});

router.route('/portes/:porte_id').put(function(req,res){
  Porte.findById(req.params.porte_id, function (err, porte){
      if (err)
        res.send(err);
      porte.adresseResume	= req.body.porte.adresseResume;
	  porte.complement      = req.body.porte.complement;
	  porte.nom_rue        	= req.body.porte.nom_rue;
	  porte.nom_ville       = req.body.porte.nom_ville;
	  porte.numS        	= req.body.porte.numS;
	  porte.numA        	= req.body.porte.numA;
	  porte.ouverte       	= req.body.porte.ouverte;
      porte.latitude  = req.body.porte.latitude;
      porte.longitude  = req.body.porte.longitude;
      porte.save(function (err){
        if (err)
          res.send(err);
        res.json ({message: 'Porte mise a jour avec succes'});
      });
  });
});

router.route('/porte/:porte_id').delete(function(req,res){
  Porte.remove({_id:req.params.porte_id}, function(err, porte){
    if (err)
      res.send(err);
    res.json({message: 'porte supprimee avec succes'});
  });
});

module.exports = router;

function dateDisplayed(timestamp) {
    var date = new Date(timestamp);
    return (date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
}
