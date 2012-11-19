'use strict'

define(function (require) {

  var Civicly = Backbone.Model.extend({
    defaults: {
      location: ''
    }

  , location: function (location) {
      var self = this;
      console.log('===#### =>', location)
      if ( location ) {
        self.set('location', location);
      } else {
        location = self.get('location');
      }
      return location;
    }
  });
  
  return Civicly;
});