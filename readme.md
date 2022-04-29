# Apache Log Parser App

The `Apache Log Parser App` or `ALPA` for short is a command line application that can be used to parse old [Apache HTTP Server](https://httpd.apache.org/) logs form 1995 to generate a report with a number of statistics.

## How to build the app

To build the app you will need to download and install (if not already available on your system):

* [Java SE 12 JDK](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html)

* [Maven](https://maven.apache.org/download.cgi?Preferred=ftp://ftp.osuosl.org/pub/apache/)

Open a terminal (in UNIX-like systems) or command prompt (in Windows) and do the following steps:

Clone the project locally:

`git clone git@github.com:velissarious/ApacheLogParserApp.git`

Move to the directory:

`cd ApacheLogParserApp/`

Use Maven with the `package` target to generate the executable jar file which will also contain all the dependencies. Like this:

`mvn package`

This will produce a number of jar files. Specifically:

```shell
stefanos-0.9.0-SNAPSHOT.jar
stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar
```

Use the second jar file as listed for convenience, as it is the one that contains all dependencies. 

## How to use the app

To use the app you must start a terminal or a command prompt at its location and type the following to execute it: 

`java -jar stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar`

The program will try to find the default input file called `access_log_Aug95` which is available from [NASA-FTP](ftp://ita.ee.lbl.gov/traces/NASA_access_log_Aug95.gz). 
