'use strict'

define(function ( require ) {

  var Router = Backbone.Router.extend({
    routes: {
      'city/:cityname': 'search'
    }

  , initialize: function (options) {
      var self = this;
      self.app = options.app;

      console.log(self.app);

      $("#search").submit(function () {
        var cityname = $.trim($("#cityname").val());
        if ( cityname === '' ) {
          alert('cityname not present');
        }
        self.navigate('/city/' + cityname, {trigger: true});
        return false;
      });
    }

  , search: function (cityname) {
      var self = this;
      if ( cityname ) {
        self.app.model.location(decodeURIComponent(cityname));
      }
    }
  });

  return Router;
});