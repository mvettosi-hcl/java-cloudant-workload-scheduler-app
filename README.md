# BeerShop - A _Java Workload Scheduler Web Starter_ app
This application demonstrates how to use the Workload Scheduler service, with the 'Liberty for Java™' runtime and Cloudant No-SQL database on IBM Cloud.

## Table of contents
[Files](#files)  
[Build](#build)  
[Deploy](#deploy)  
[Usage](#usage)  

## Files
The Java Workload Scheduler Web Starter application contains the following contents:
- **javaCloudantWorkloadSchedulerApp.war**  
  This WAR file is actually the application itself. It is the only file that'll be pushed to and run on the Bluemix cloud. Every time your application code is updated, you'll need to regenerate this WAR file and push to Bluemix again. See the [Build](#build) section on detailed steps.
- **src/main/webapp**  
  This directory contains the client side code (HTML/CSS/JavaScript) of your application.
- **src/main/java**  
  This directory contains the server side code (JAVA) of your application.
- **pom.xml**  
  This file allows you to easily build your application using Apache Maven.
- **lib/**  
  This directory contains external libraries that are needed to compile. 
    
## Build
This is how to compile the app and manually generate a deployable artifact. If you just wish to try it using the precompiled war, skip to the [Deploy](#deploy) section.

- Make sure to have an installed and working instance of Apache Maven, using Java 1.7 or above. You'll also need git.
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
You'll find the deployable war _javaCloudantWorkloadSchedulerApp.war_ under the _target_ folder.

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
If you wish to receive an email confirmation of every order processed by the app, you'll need a Unix/Linux machine connected to the internet, with the "mailx" command installed and configured.  
- Click on the _Workload Scheduler_ section, under _Services_
- Click on _Downloads_
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
- While waiting for the Application Lab to discover the newly installed agent, set the environment variables for the sender and recipient email:
  - Click on the _Environment Variables_ section
  - Click on the _USER-DEFINED_ tab
  - Add a _FROM_ and a _TO_ variable containing the sender and recipient email address, respectively.
  - **Note**: for some specific services, like _Gmail_, additional configurations may be needed on the agent machine.

## Usage
Once the deployed app is online, go to its link and select same beers, then click on the _Checkout_ button. During the waiting time, what happens under the curtains is:
- Using the official Cloudant Java Library, the app creates a document with the order informations in a database called _orders_.
- With the help of the Java Application Lab library, it creates (if it does not exist) a _Library_ and a _Process_ inside it
  - If the _FROM_ and _TO_ environment variables are defined, a step using the hybrid agent will be created to send a notification with the order informations
- It runs the _OrderProcess_ asynchronously which takes an order in the database, processes it and deletes the corrisponding document.