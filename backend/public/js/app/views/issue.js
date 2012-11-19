'use strict'

define(function (require) {

  var Issuetpl = require('text!app/template/issue.tpl');

  Issuetpl = Hogan.compile(Issuetpl);

  var Issue = Backbone.View.extend({

    events: {
      'click': 'onClick'
    , 'click .img': 'onClickImg'
    }

  , tagName: 'li'
  
  , initialize: function() {
      var self = this;
      self.app = self.options.app;
    }

  , render: function () {
      var self = this
        , modelJSON = self.model.toJSON();

      modelJSON.createdAt = moment(modelJSON.createdAt).fromNow()

      self.$el.html(Issuetpl.render(modelJSON));

      if ( modelJSON.location && modelJSON.location.latitude && modelJSON.location.longitude ) {
        geocoder.geocode({'latLng': new google.maps.LatLng(modelJSON.location.latitude,modelJSON.location.longitude)}, 
        function(results, status) {
          if ( results && results[0] ) {
            self.$('.location').text(results[0].formatted_address);
          }
        });
      }
      return this;
    }

  , onClick: function (e) {
      var self = this
        , modelJSON = self.model.toJSON();
      self.app.map.panTo(new google.maps.LatLng(modelJSON.location.latitude, modelJSON.location.longitude));
    }

  , onClickImg: function (e) {
      e.preventDefault();
      e.stopPropagation();
      var self = this;
      $("#modal").modal('show');
      $("#modal").find(".content").html('<div style="text-align: center"><img src="'+self.model.get('image').url+'" /></div>');
    }
  });

  return Issue;
});