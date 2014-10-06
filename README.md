Archaius example
================

This is just a simple application to showcase some of [Archaius](https://github.com/Netflix/archaius) features, including
reading from property files with custom names, use cascade properties, using a custom JDBC source to read properties from
a DB and playing around with JConsole to modify properties in runtime.

## Step by step

The project has a few tags to show the evolution from using the most simple possible configuration to more complex ones.
To see the list of available tags you can do ```git tag``` and ```git show #tagName``` to see more info about any of the
tags listed before.

You can move through these tags to take a look at the different states of the showcase with
```git checkout #tagName``` where #tagName is one of the tags listed with ```git tag```. 

The current available tags are the following:

  * v0.1: The simplest possible example of using archaius to dynamically get properties defined on a 'config.properties'
    file. This is the default file name that archaius expects so you don't need to do anything
    
  * v0.2: Shows how you can tell archaius to use a different name for the configuration file
  
  * v0.3: Shows how you can define [cascaded properties](https://github.com/Netflix/archaius/wiki/Deployment-context) to
  load several properties files or override the values defined by a default file with some context-specific ones
  
  * v0.4: Introduces an in-memory H2 database with a property table. Then, using Archaius JDBC source you can read
  properties from this DB exactly the same way you read them from the property file.
  
  * v0.5: This version shows how you can register your configuration as an [MBean](http://docs.oracle.com/javase/tutorial/jmx/mbeans/)
  via JMX to be able to connect to your application via JConsole and both query the existing properties and update them.