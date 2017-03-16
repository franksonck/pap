const express = require('express');

// Get the router
const router = express.Router();
const Porte = require ('./models/porte.js');
const controllers = require('./controllers');
const auth = require('./auth');
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

router.route('/portes').post (function (req,res) {
  var porte           	= new Porte();
  porte.adresseResume	= req.body.porte.adresseResume;
  porte.complement      = req.body.porte.complement;
  porte.nom_rue        	= req.body.porte.nom_rue;
  porte.nom_ville       = req.body.porte.nom_ville;
  porte.numS        	= req.body.porte.numS;
  porte.numA        	= req.body.porte.numA;
  porte.ouverte       	= req.body.porte.ouverte;
  porte.person			=req.body.porte.person;
  porte.location      = [req.body.porte.latitude, req.body.porte.longitude];

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

router.route('/user_porte').get(function(req,res){
    Porte.find({person: req.query.person}).exec(function(err, locations){
                  if (err)
                    res.status(500).send(err);
                  res.status(200).json(locations);
                });
});

router.route('/proches').get(function(req,res){
    var limit       = req.query.limit || 10;
    var maxDistance = req.query.distance || 8;
    maxDistance /= 6371;
    var coords = [];
    coords[0] = req.query.lat;
    coords[1] = req.query.lon;
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
      porte.location      = [req.body.porte.latitude, req.body.porte.longitude];
	  porte.person			=req.body.porte.person;
      porte.save(function (err){
        if (err)
          res.send(err);
        res.json ({message: 'Porte mise a jour avec succes'});
      });
  });
});

router.route('/portes/:porte_id').delete(function(req,res){
  Porte.remove({_id:req.params.porte_id}, function(err, porte){
    if (err)
      res.send(err);
    res.json({message: 'porte supprimee avec succes'});
  });
});

router.get('/connexion/', controllers.associateDevice);
router.get('/connexion/aller', auth.connect);
router.get('/connexion/retour', auth.oauthCallback);


router.get('/deconnexion/', auth.disconnect);

router.get('/verifier', auth.verify);

module.exports = router;

function dateDisplayed(timestamp) {
    var date = new Date(timestamp);
    return (date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
}
