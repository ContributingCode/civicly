var requirejs = require('requirejs'),
    fs        = require('fs');

var config = {
  baseUrl: "public/js",
  // dir: "js",
  //Comment out the optimize line if you want
  //the code minified by UglifyJS.
  // optimize: "none",

  paths: {
    'jquery'      : 'lib/jquery',
    'underscore'  : 'lib/underscore',
    'backbone'    : 'lib/backbone',
    'hogan'       : 'lib/hogan',
    'moment'      : 'lib/moment',

    'bootstrap'   : 'lib/bootstrap'
  },

  name: "main",
  out: "public/js/main-built.js"
}

requirejs.optimize(config, function ( buildResponse ) {
    // var contents = fs.readFileSync(config.out, 'utf8');
    console.log(buildResponse)
});