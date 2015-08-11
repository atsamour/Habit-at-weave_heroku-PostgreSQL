This is a sample Java web project for [Heroku](https//www.heroku.com) PaaS cloud. You can also run it locally with only JRE and Maven installed, as it doesn't requre a seperate application container. It evaluates Heroku's [open source](http://github.com/jsimone/webapp-runner) with embedded Tomcat.


## To run locally

First you have to git pull this repo to your PC, then build the application running this command from the local project folder:

    :::term
    $ mvn package

And then you just run the application by using the java command:

    :::term
    $ java -jar target/dependency/webapp-runner.jar target/*.war

If everything worked correctly, your application should be accessible at `localhost:8080`.

## To run on Heroku cloud

First create a Heroku account, install Heroku Toolbelt as described [here](https://devcenter.heroku.com/articles/getting-started-with-java#set-up), then deploy the app on Heroku as described [here](https://devcenter.heroku.com/articles/getting-started-with-java#deploy-the-app). 
If everything worked correctly, your should see the application in your browser.


### To deploying a jar filer that doesn't exist at Maven repository

Download and put the .jar (e.g.) file to the project's folder (e.g. C:\Users\username\Dropbox\heroku-examples\appfolder)
Then call mvn deploy:deploy-file to actually deploy the jar to `repo` folder:

    :::term
    mvn deploy:deploy-file -Durl=file:///C:\Users\username\Dropbox\heroku-examples\appfolder\repo -Dfile=json-taglib-0.4.1.jar -DgroupId=atg.taglib.json -DartifactId=json-taglib -Dpackaging=jar -Dversion=0.4.1

You are ready to add this dependancy to your main pom file as any other.
more info here: [https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html](https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html), [https://devcenter.heroku.com/articles/local-maven-dependencies](https://devcenter.heroku.com/articles/local-maven-dependencies)