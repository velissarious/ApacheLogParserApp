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

By default the program will try to find input file called `access_log_Aug95` which is available from [NASA-FTP](ftp://ita.ee.lbl.gov/traces/NASA_access_log_Aug95.gz) and produce a plain text file report called `report.txt`. 

### Input

To use another input file you must specify it with the `-i` or `-input` option, for example:

`java -jar stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar -i small`

In this example the program will attempt to parse a file called `small`.

The program is designed to work only with Apache log files.

### Report

The program will output a file report called `report.txt` in the directory the program is located in. You can specify a different report file with the `-r` or `-report` option like this:

`java -jar stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar -r test.txt`

In this example, the program will generate the report with the name `test.txt` instead of `report.txt`. 

### Verbose

By default, the program will not display any output if successful, it will only display errors and warnings if need be, e.g. it will display an error for each malformed log entry encountered while parsing the log file. 

To make the display output more verbose you need to specify the `-v` or `-verbose` option like this:

`java -jar stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar -v`

### Option Parsing

Using option parsing you can to generate a report that contains one of the following points:

0 - Default option, all of the bellow:

1 - Top 10 requested pages and the number of requests made for each.

2 - Percentage of successful requests (anything in the 200s and 300s range).

3 - Percentage of unsuccessful requests (anything that is not in the 200s or 300s range).

4 - Top 10 unsuccessful page requests.

5 - The top 10 hosts making the most requests, displaying the IP address and number of requests made. 

7 - For each of the top 10 hosts, show the top 5 pages requested and the number of requests for each page.

You can use this feature by specifying the the `-o` or `-option` option:

`java -jar stefanos-0.9.0-SNAPSHOT-jar-with-dependencies.jar -o 1`

This example will display item 1 from the list above.



## Technical Details

### Dependencies

The project has the following dependencies that are also defined in Maven's `pom.xml` file.

*  [SQLite](https://sqlite.org/index.html) jdbc - a file based database.

* [JCommander](https://jcommander.org/) - a command line parsing library.

* [JUnit](https://junit.org/junit4/) 4 - for unit testing.

Portions of the program are written as SQL queries.

### Assumptions

All possible input are log files are Apache HTTP Server log file from 1995.

The input log will always fit in memory. 

Pages are only html pages.

