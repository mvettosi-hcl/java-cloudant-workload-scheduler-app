#Java Workload Scheduler Web Starter - Beer Shop App
This application demonstrates how to use the Workload Scheduler service, with the 'Liberty for Java™' runtime and Cloudant No-SQL database on IBM Cloud.

## Table of contents
[Files](#files)
[Build](#build)
[Deploy](#deploy)

## Files
The Java Workload Scheduler Web Starter application contains the following contents:
- **javaCloudantWorkloadSchedulerApp.war**  
  This WAR file is actually the application itself. It is the only file that'll be pushed to and run on the Bluemix cloud. Every time your application code is updated, you'll need to regenerate this WAR file and push to Bluemix again. See the next section on detailed steps.
- **src/main/webapp**  
  This directory contains the client side code (HTML/CSS/JavaScript) of your application.
- **src/main/java**  
  This directory contains the server side code (JAVA) of your application.
- **pom.xml**  
  This file allows you to easily build your application using Apache Maven.
- **lib/**  
  This directory contains external libraries that are needed to compile. 
    
## Build
This is how to compile the app and manually generate a deployable artifact. If you wish to just try it using the precompiled war, just skip to the [Deploy](#deploy) section.

1. Make sure to have an installed and working instance of Apache Maven, using Java 1.7 or above. Also you'll need git.
2. Clone this project:  
```
git clone https://github.com/MatteoVettosiHCL/java-cloudant-workload-scheduler-app.git
```
3. Move to the newly created folder:  
```
cd java-cloudant-workload-scheduler-app
```
4. Install the maven dependencies  
```
mvn clean
```
5. Build the app  
```
mvn package
```

## Deploy
This is how to see the application in action on the Bluemix environment.

1. Create a new app using the [Java Workload Scheduler Web Starter](https://console.eu-gb.bluemix.net/catalog/starters/java-workload-scheduler-web-starter/) boilerplate.
2. If you decided to build the application manually, change to the _target_ directory  
```
cd target
```
2. Follow the istructions in the _Start Coding_ section to install and setup the command line tools for Bluemix.
3. Using the ad-hoc commands in the same page, authenticate to the service and push your application to the cloud.
   4. Optionally, modify the push command to only upload the war and save bandwidth:  
   ```
   cf push <YourAppName> -p javaCloudantWorkloadSchedulerApp.war
   ```