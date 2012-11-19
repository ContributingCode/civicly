'use strict'

define(function (require) {
  var Issue       = require('app/views/issue');

  var Sidebar = Backbone.View.extend({
    initialize: function() {
      var self = this;
      self.app = self.options.app;

      self.collection.on('add', self.add, self);
      self.collection.on('reset', self.onFetch, self);
    }

  , add: function (model) {
      var self = this;
      var modelJSON = model.toJSON();
      var issueEl = new Issue({
        'model' : model
      , 'app'   : self.app
      });
      if ( modelJSON.location ) {
        var marker = new google.maps.Marker({
              position: new google.maps.LatLng(modelJSON.location.latitude, modelJSON.location.longitude)
            , map: self.app.map
            , clickable: true
            , title: modelJSON.content
            });

        google.maps.event.addListener(marker, 'click',function() {
          self.$('li').removeClass('active');
          issueEl.$el.addClass('active');
        }); 
      }

      self.render(issueEl.render().el);
    }

  , onFetch: function () {
      var self = this;
      self.$el.empty();
      self.collection.each(function (model) {
        self.add(model);
      });
      if ( self.collection.length > 0 ) {
        var firstModelJSON = self.collection.at(0).toJSON();
        self.app.map.panTo(new google.maps.LatLng(firstModelJSON.location.latitude, firstModelJSON.location.longitude));
      }
      console.log()
      // fetching tw feed based on loaction
        // var ss = "http://search.twitter.com/search.json?q=problems&rpp=5&geocode="+location[0].geometry.location.Ya+","+location[0].geometry.location.Za+",2mi&include_entities=true&result_type=mixed&callback=?";
        // $.getJSON(ss,
        //   function(data){
        //     console.log(data);
        //     res = data;
        //     var ob1 = new Object();
        //     ob1.content=data.results[0].text;
        //     ob1.address="100 heath";
        //     ob1.count =1;
        //     ob1.location = new Object();
        //     ob1.location.latitude = location[0].geometry.location.Ya;
        //     ob1.location.longitude = location[0].geometry.location.Za;
        //     console.log(ob1);
        //     I.add(new IssueModel(ob1));
            
        // });
    }

  , render: function (issue) {
      var self = this;
      self.$el.prepend(issue);
    }
  });

  return Sidebar;
});