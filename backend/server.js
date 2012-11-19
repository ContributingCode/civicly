var express = require('express')
  , app     = express()
  , rest    = require('restler')
  , cons    = require('consolidate')
  , socket  = require('socket.io')
  , fs      = require('fs')
  , async   = require('async')
  , http    = require('http')
  , Emitter = require('events').EventEmitter;

var server, io;
var sockets = {};
var headers = {
    'X-Parse-Application-Id': 'YOUR PARSE APP ID'
  , 'X-Parse-REST-API-Key'  : 'YOUR PARSE KEY'
}


server = app.listen(process.env.VCAP_APP_PORT || 3000);
io = socket.listen(server);
io.set('log level', 2);
// Set Socket io transports
io.set('transports', [
  'xhr-polling'
]);
io.set('close timeout', '10');

/* Templates */
app.engine('html', cons.hogan);
app.set('view engine', 'html');
app.set('views', __dirname + '/views');
/* Static */
app.use('/assets' , express.static((__dirname + '/public')));

app.use(express.bodyParser());
app.use(express.methodOverride());

// TO notify we have some post
app.get('/push/:id', function(req, res) {
  console.log("++++++++++");
  rest.get('https://api.parse.com/1/classes/Problem/' + req.params.id, {
    headers: headers
  })
    .on('complete', function (result) {
    async.forEach(Object.keys(sockets), function(socketId, callback){
      sockets[socketId].emit('new-issue', result);
      console.log(result);
      callback();
    });
    res.json(result);
  });
});

app.get('/issues/:q', function(req, res) {
  var loc = req.params.q.split(',');
  console.log(loc)
  var temp=[];
  var url = {
    "location": {
      "$nearSphere": {
        "__type": "GeoPoint",
        "latitude": parseFloat(loc[0]),
        "longitude": parseFloat(loc[1])
      },
      "$maxDistanceInMiles": 30.0
    }
  };

  console.log('https://api.parse.com/1/classes/Problem?order=createdAt&where=' + JSON.stringify(url));
  rest.get('https://api.parse.com/1/classes/Problem?order=createdAt&where='+encodeURIComponent(JSON.stringify(url)), {
    headers: headers
  })
    .on('complete', function (result) {
          console.log("=======");
        console.log(result);
      // fetching tw feed based on loaction
        var ss = "http://search.twitter.com/search.json?q=problems&rpp=5&geocode="+loc[0]+","+loc[1]+",2mi&include_entities=true&result_type=mixed";
        console.log("=======");
        console.log(ss);
        console.log("=======");
        rest.get(ss, {headers: headers}).on('complete', function (data) {
              console.log(data);
              console.log("=======");
           for(var i =0; i < data.results.length;i++){
            var ob1 = new Object();
            var location = new Object();
            location.latitude = loc[0];
            location.longitude = loc[1];
            ob1.content=data.results[i].text;
            ob1.address="100 heath";
            ob1.count =1;
            ob1.location = location;
            temp.push(ob1);
          }
            for(var i=0; i<result.results.length;i++){
              temp.push(result.results[i]);  
            }
            result.results = temp;
            console.log("after"+JSON.stringify(result));   
            res.json(result);
        });
   
  });
});

app.get('*', function(req, res){
  res.render('index');
});


io.sockets.on('connection', function (socket) {
  sockets[socket.id] = socket;

  socket.on('disconnect', function () { 
    delete sockets[socket.id];
  });
});