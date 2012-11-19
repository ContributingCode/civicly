'use strict'

requirejs.config({
  baseUrl: '/assets/js',
  /* Path */
  paths: {
    'jquery'      : 'lib/jquery',
    'underscore'  : 'lib/underscore',
    'backbone'    : 'lib/backbone',
    'hogan'       : 'lib/hogan',
    'moment'      : 'lib/moment',

    'bootstrap'   : 'lib/bootstrap',
  },

  /* Shims */
  shims: {
    'underscore': {
      deps    : ['jquery'],
      exports : '_'
    },
    'backbone': {
      deps    : ['jquery', 'underscore'],
      exports : 'Backbone'
    },
    'hogan': {
      deps    : ['underscore'],
      exports : 'Hogan'
    },
    'bootstrap'    : ['jquery']
  }
});


define(function(require) {
  /* Deps */
  require('jquery');
  require('underscore');
  require('backbone');
  require('hogan');
  require('moment');

  /* Sugar */
  require('lib/sugar');

  /* jQuery Plugins */
  require('bootstrap');

  /* Basic Setup */
  $.ajaxSetup({
    beforeSend: function ( xhr ) {
      xhr.withCredentials = true;
    }
  })

  $(document).on("click", "a:not([data-bypass])", function( e ) {
    // Get the anchor href and protcol
    var href = $(this).attr("href");
    var protocol = this.protocol + "//";
    if (href && href.slice(0, protocol.length) !== protocol &&
        href.indexOf("javascript:") !== 0) {
      e.preventDefault();
      Backbone.history.navigate(href, true);
    }
  });

  /* app */
  var App = require('app/app');
});