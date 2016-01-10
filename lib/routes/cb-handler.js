'use strict';

var debug = require('debug')('cd-handler');
var express = require('express');
var router = express.Router();
var DatabaseModel = require('../dao');


router.get('/parties', function (req, res) {
        new DatabaseModel().getParties(req.query.terms? req.query.terms : 'employee', false, function(parties) {
            res.json(parties);
        });
    }
);

module.exports = router;
