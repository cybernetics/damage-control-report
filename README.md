[![Build Status](https://snap-ci.com/damage-control/report/branch/master/build_image)](https://snap-ci.com/damage-control/report/branch/master)

Damage Control Report
---------------------

Report generation tool for [Spock Framework](http://github.com/spockframework) based tests. It reads the test result files and produces a nice HTML report using the specification steps (given, when, then).

See some sample reports [here](https://github.com/damage-control/report/wiki/Sample-Reports).

And a sample Spock project using Maven [here](https://github.com/damage-control/report/tree/master/end-2-end-test/sample-spock-project).

Modules
-------

- html-generator
- plugins:maven
- end-2-end-test

Building
--------

Prerequisites:
- JDK 6 or higher
- [Maven](http://maven.apache.org)
- [Gradle](http://www.gradle.org)

To build the whole project and run all tests:

```
gradle
```

To build `html-generator` module only:

```
gradle :html-generator:install
```

Change Notes
------------

### 1.2.0

* capability of reading test results from several directories (testResultsFolders property)
* custom report title (reportTitle property)
* specs sorted by name

### 1.1.0

* new "skip" configuration property and support to -DskipTests flag (Maven plugin)
* summary on index page
* navigation link on top of each page
* generation timestamps on each page
* ignoring missing sources so plain JUnit tests are displayed as well
* presenting spec error details when available
* handling special characters in feature names

