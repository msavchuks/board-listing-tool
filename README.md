# Boards Listing Tool

## Analysis / Assumptions

- Application must combine multiple JSON files containing board data into a single json
- Output json must be ordered by `vendor` and then `name` fields
- Output json must include `_metadata` property that includes `total_vendors` and `total_boards`
- Since data comes from different sources I assume that duplicate data may be present
- I assume that vendor and board names are standardised between the different sources and there will be no
  capitalization or other differences
- There are total of 466 boards available. Assuming 200 bytes per board the total of 94kb is needed to store everything,
  so the data can be handled in memory.
- If name and vendor match, but other board properties are different we will log a warning but include both boards in
  the output ordered by core and wifi presence

## Java & Gradle

The Gradle project has JDK auto download enabled, so the required JDK version will be downloaded unless already
present on the system. One of JVM versions 8-23 must be present on the system to start the Gradle for the first time.
Installation location can be found with `./gradlew -q javaToolchains` or `gradlew.bat -q javaToolchains` for windows

```
 + Eclipse Temurin JDK 21.0.5+11-LTS
     | Location:           /home/developer/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2
     | Language Version:   21
     | Vendor:             Eclipse Temurin
     | Architecture:       amd64
     | Is JDK:             true
     | Detected by:        Auto-provisioned by Gradle
```

## Build

To build the application run `./gradlew clean build` or `gradlew.bat clean build` on linux. This will create a JAR file
under the `./build/libs/` path. Execution scripts assume that the JAR file exists in this location.

## Run

Ensure `JAVA_HOME` is pointing to a folder with at least Java 21.

- Run `./build-list.sh help` to see general help message
- Run `./build-list.sh help build` to see help message for the command
- Run `./build-list.sh build --inputDir src/test/resources/example-boards/ --outputFile boards.json` to process files
  in the `src/test/resources/example-boards` and output JSON into 'boards.json' file

## Time taken

In total the assignment took me slightly more than 3 hours. Rough estimate would be as follows:

- Research: 30m
- Development: 2.5h
- Documentation, testing under Windows, etc: 30m

## Potential improvements

- More consistent error reporting. Right now some errors are logged as a stack trace, some with messages.
- Make output file optional and output directly to terminal if not present.
- Look into parallel file reading. Might be worth it depending on the average file size.
