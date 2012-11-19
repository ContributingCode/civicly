'use strict'

define(function(require) {
  var Civicly     = require('app/model/civicly');
  var Router      = require('app/router');
  var IssueModel  = require('app/model/issue');
  var Issues      = require('app/model/issues');
  var Sidebar     = require('app/views/sidebar');

  var App = Backbone.View.extend({
    initialize: function () {
      var self = this;
      self.map = self.initMaps();

      self.sidebar = new Sidebar({
        'collection': self.collection
      , 'app'       : self
      , 'el'        : $("#sidebar")
      });

      if (window.navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
          if ( position && position.coords ) {
            geocoder.geocode({
              'latLng': new google.maps.LatLng(position.coords.latitude, position.coords.longitude)
            }, 
            function(results, status) {
              if ( results && results[0] ) {
                $('.mylocation').text('You are in ' + results[0].formatted_address);
              }
            });

            // self.map.panTo(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
          }
        },
        function (error) {
          console.log(error);
        }, {
          enableHighAccuracy: true,
          maximumAge: 60000,
          timeout: 10
        });
      }

      self.model.on('change:location', self.onLocationChange, self);
    }

  , initMaps: function () {
      var opts = {
        zoom: 12,
        center: new google.maps.LatLng(42.92 , -78.82),
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      return new google.maps.Map($('#map_canvas')[0], opts);
    }

  , onLocationChange: function (model) {
      var self = this;
      console.log('location changed', model.location());

      geocoder.geocode({address: model.location()}, function (location) {

         self.collection.fetch({
          latitude: location[0].geometry.location.Ya
        , longitude: location[0].geometry.location.Za
        });  
      });

    }
  });

  var I = new Issues;

  new Router({
    app: new App({
      model: new Civicly
    , collection: I
    })
  });

  /* Start App */
  Backbone.history.start({pushState: true });

  // Create new issue
  // when data comes in
  socket.on('new-issue', function (data) {
    console.log(data);
    I.add(new IssueModel(data));
  });
});