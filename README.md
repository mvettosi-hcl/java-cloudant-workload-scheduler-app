#Java Workload Scheduler Web Starter - Beer Shop App
This application demonstrates how to use the Workload Scheduler service, with the 'Liberty for Java™' runtime and Cloudant No-SQL database on IBM Cloud.

## Table of contents
[Files](#files)  
[Build](#build)  
[Deploy](#deploy)  
[Usage](#usage)  

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

- Make sure to have an installed and working instance of Apache Maven, using Java 1.7 or above. Also you'll need git.
- Clone this project:  
```
git clone https://github.com/MatteoVettosiHCL/java-cloudant-workload-scheduler-app.git
```
- Move to the newly created folder:  
```
cd java-cloudant-workload-scheduler-app
```
- Install the maven dependencies  
```
mvn clean
```
- Build the app  
```
mvn package
```

## Deploy
This is how to see the application in action on the Bluemix environment.

- Create a new app using the [Java Workload Scheduler Web Starter](https://console.eu-gb.bluemix.net/catalog/starters/java-workload-scheduler-web-starter/) boilerplate.
- If you decided to build the application manually, change to the _target_ directory  
```
cd target
```
- Follow the istructions in the _Start Coding_ section to install and setup the command line tools for Bluemix.
- Using the ad-hoc commands in the same page, authenticate to the service and push your application to the cloud.
   - Optionally, modify the push command to only upload the war and save bandwidth:  
   ```
   cf push <YourAppName> -p javaCloudantWorkloadSchedulerApp.war
   ```

## Setup
If you wish to receive an email confirmation of every order done with the app, you'll need a unix/linux machine connected to the internet, with the "mailx" command installed and configured.  
- Click on the "Workload Scheduler" section, under _Services_
- Click on "Downloads"
- Download the agent for your linux architecture on your linux machine
- Unzip the agent:  
```
unzip /tmp/SCWA-SaaS_LINUX_X86_64.zip
```
- Move to the new folder  
```
cd SCWA-SaaS/
```
- Create an user for the agent  
```
useradd -m iws
```
- Install the agent using the command  
```
./installAgent.sh -new -acceptlicense yes -uname iws -displayname hybrid
```
- Wait for the Application Lab to discover the newly installed agent.

## Usage
Once the deployed app is online, go to its link and select same beers, then click on the _Checkout_ button. During the waiting time, what happens under the curtains is:
- The app creates a document with the order informations in a database called "orders", using the official Cloudant Java Library.