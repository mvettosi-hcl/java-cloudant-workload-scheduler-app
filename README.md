#Java Workload Scheduler Web Starter
This application demonstrates how to use the Workload Scheduler service, with the 'Liberty for Java™' runtime and Cloudant No-SQL database on IBM Cloud.

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
    
## How-To
This is how to build, deploy and try this application.