'use strict'

define(function (require) {
  var Issue = require('app/model/issue');
  var baseUrl = '/issues';
  var Issues = Backbone.Collection.extend({
    model: Issue
  , fetch: function (url) {
      this.url = baseUrl + '/' + url.latitude + ',' + url.longitude;
      Backbone.Collection.prototype.fetch.apply(this, arguments);
    }
  , parse: function (result) {
      return result.results;
    }
  });

  return Issues;
});