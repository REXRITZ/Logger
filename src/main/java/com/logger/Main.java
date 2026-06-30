package com.logger;

import com.logger.analytics.MetricsAggregator;

public class Main {

    public static void main(String[] args) {
        
        MetricsAggregator analyzer = new MetricsAggregator();

        analyzer.analyzeLogs("access.log");
    }
}
