const {ensureLoggedIn} = require('connect-ensure-login');

const config = require('./config');
const associateDevice = require('./actions/associate-device');

exports.associateDevice = [
  ensureLoggedIn({redirectTo: '/connexion/aller'}),
  function(req, res) {
    const deviceToken = req.query.device;
    if (!deviceToken) {
      return res.status(400).send('Bad request');
    }

    associateDevice(deviceToken, req.user).then(
      () => res.redirect(config.appReopenSuccess),
      () => res.redirect(config.appReopenError)
    );
  }
];
