# toolkit-java

This toolkit allow to manipulate and download information about Wikipedia's pages and users through the apis:
- https://[domain].wikipedia.org/w/api.php
- http://stats.grok.se/
The main classes for handling informations are WikiPage and WikiUser.

This toolkit allows multithreading for downloading the data using the classes DownloadThread and JobManager. 

This toolkit also allow to store the data either in a folder or in a MongoDB (see the classes FolderManager and MongoManager).

An example of how to use this toolkit can be found with the class Main.

## require

Require the mongoDB driver in the classpath for java to work (http://docs.mongodb.org/ecosystem/drivers/java/).

# Ant file

## documentation

To generate the documentation launch.

## compilation and running

Before compiling, you need to edit the path section at the beginning of the build with the location of the mongoDB java driver. Then you can compile using to 'ant init' and try the toolkit using the class main with 'ant Main'.

To generate a runnable jar, you need to edit the manifest section at the end of the build with the location of the mongoDB java driver. Then, launch 'ant jar'.
