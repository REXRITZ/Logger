# Multi-Threaded Logger

This is a personal sandbox project built to practice modern Java features and learn how to write clean, robust, and performant code. 

The project centers around a simulated web log analyzer. Starting with a naive implementation, I will keep on improving the code as I learn new things.


## Features

1. **Synthetic Log Generation**: Fast generation of 1,000,000 log entries with customized distributions for IPs, timestamps, HTTP methods, endpoints, and response status codes.
2. **Robust Access Parsing**: Parsers that cleanly isolate fields and map them to type-safe enums without throwing unhandled exceptions.
3. **Concurrency Execution**: Parallelized grouping and frequency summation to process massive files across available CPU cores.

---

## History of Progress (Git Log Highlights)

The project has evolved through the following stages:

* **Project Bootstrapping**: Defined base data structures including [LogEntry.java](/src/main/java/com/logger/model/LogEntry.java) and [RequestType.java](/src/main/java/com/logger/model/RequestType.java).
* **Mock Data Generator**: Implemented [LogGenerator.java](/src/main/java/com/logger/utils/LogGenerator.java) to append mock log lines into a benchmark file `access.log`.
* **Robust parsing**: Created [LogParser.java](/src/main/java/com/logger/parser/LogParser.java) using regular expressions and set up corresponding JUnit 5 assertions in [LogParserTest.java](/src/test/java/com/logger/LogParserTest.java).
* **Logs Aggregator & Architecture Review**: Added [MetricsAggregator.java](/src/main/java/com/logger/analytics/MetricsAggregator.java) to categorize and print log totals. Performed full architecture refactorings.
* **Performance Enhancements**: Identified bottlenecks and improved parser speed by replacing the heavy regex compiler with high-performance `String.indexOf` comparisons, enabling multi-threaded execution to scale effectively.

---

## 🚀 Execution & Usage

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

## 📄 License

This project is open-source and licensed under the terms of the **[MIT License](LICENSE)**.
