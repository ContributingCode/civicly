## Civicly ##

Civicly is a crowd sourced civic issue tracker. Although the space of civic tracking issues has been explored before, Civicly takes an 'incentivized social' approach to help the civic authorities manage and address issues.

The crux of the idea is simple, utilize existing social networks as much as possible to aggregate information
about 'trending issues', which involves organic notification of information to users and authorities relevant to the cause.

It aims to provide users to interact with the community to address an important issue/event in a virtual 'petition signup' style mode. The crucial part is, describe the problem in 140chars (utilizing twitter) to its fullest.


Download the app, take photos of civic issues that you would like to see addressed, then spread the word!  You can post these photos to our website with links to Twitter and Facebook, calling for social action.  People then vote on the issues and bring public awareness to the issue.

### [Presentation](http://presvo.com/0499f8c/)  ###

### Technology Stack
* Android App
* Node.js
* Socket.io
* Backend powered by Parse
* API's - Facebook, Twitter, Google

### Architecture ###

![Architecutre](https://presvo.s3.amazonaws.com/small_Architecture_diagram_Contrinutingcode__2_.png)

### The Android App ###

##### Features #####
* Login with Twitter or Facebook 
* News feed -- Trending(in your location), Latest and My feed of #civicproblems reported
* Report a problem -- Report a problem with an image and share it on Twitter and Facebook.               
* Ability to 'Vote up' (Virutally Sign a Petition) to address a particular issue

### Web app ###
Node.js powered web app, displays the #civic problems in real time as they are posted. 

### Deploying on [CloudFoundry](http://www.cloudfoundry.com) ###

Replace the constants in Constants.java and server.js files with App secret and keys
```ruby
git clone git@github.com:<your_name>/teamdotly.git civicly
cd civicly
vmc push --runtime=node08 
```