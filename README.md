# Multi-Threaded Logger

This is a personal sandbox project built to practice modern Java features and learn how to write clean, robust, and performant code.

The project centers around a simulated web log analyzer. Starting with a naive implementation, I will keep on improving the code as I learn new things.

Any code improvement suggesstions are always appreciated from the community!

---

## Project Structure & Core Classes

* **[Main.java](/src/main/java/com/logger/Main.java)**: Entry point of the application running log analysis.
* **[MetricsAggregator.java](/src/main/java/com/logger/analytics/MetricsAggregator.java)**: Handles the aggregation logic.
* **[LogParser.java](/src/main/java/com/logger/parser/LogParser.java)**: Extracts structured access records out of raw text.
* **[LogGenerator.java](/src/main/java/com/logger/utils/LogGenerator.java)**: Generates 1,000,000 mock web server request records for benchmarking.
* **Data Models**:
  * [LogEntry.java](/src/main/java/com/logger/model/LogEntry.java) (Java `record` representing a log line)
  * [RequestType.java](/src/main/java/com/logger/model/RequestType.java) (HTTP Verb enum: GET, POST, PUT, DELETE, PATCH, etc.)
  * [ResponseType.java](/src/main/java/com/logger/model/ResponseType.java) (HTTP Response Status Code enum with parser defaults)

---

## History of Progress

The project has evolved through the following stages:

* **Project Bootstrapping**: Created base data classes, including [LogEntry.java](src/main/java/com/logger/model/LogEntry.java) and [RequestType.java](src/main/java/com/logger/model/RequestType.java).
* **Log Generator**: Built [LogGenerator.java](src/main/java/com/logger/utils/LogGenerator.java) to append randomized mock entries into a test file `access.log`.
* **Parser & Tests**: Added regex-based [LogParser.java](src/main/java/com/logger/parser/LogParser.java) and verified assertions in [LogParserTest.java](src/test/java/com/logger/LogParserTest.java).
* **Metrics Aggregator**: Introduced [MetricsAggregator.java](src/main/java/com/logger/analytics/MetricsAggregator.java) to compile and print status category totals.
* **Optimization**: Replaced regex parsing with fast `String.indexOf` indexing to resolve execution bottlenecks.

---

## Execution & Usage

Ensure you have **Java 21** and **Maven** installed.

### 1. Build the Project
```bash
mvn clean compile
```

### 2. Run Tests
```bash
mvn test
```

### 3. Generate Benchmark Logs
Generate the test `access.log` containing 1,000,000 mock records:
```bash
mvn exec:java -Dexec.mainClass="com.logger.utils.LogGenerator"
```

### 4. Run Log Analysis
Parse the log entries and view computed metrics:
```bash
mvn exec:java -Dexec.mainClass="com.logger.Main"
```

---

## License

This project is open-source and licensed under the terms of the **[MIT License](LICENSE)**.
