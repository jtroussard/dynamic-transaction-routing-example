package com.example.demo.config;

import com.example.demo.enums.DataSourceType;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@DependsOn("transactionManager")
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        log.info("[TUNATRANSACTIONROUTING] Checking transaction state in TransactionRoutingDataSource");

        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        boolean isSynchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();

        Object currentDataSource = isReadOnly ? DataSourceType.READ_ONLY : DataSourceType.READ_WRITE;

        // Log additional details
        log.info("[TUNATRANSACTIONROUTING] Transaction state: Synchronization active? {}, Read-only? {}",
                isSynchronizationActive, isReadOnly);
        log.info("[TUNATRANSACTIONROUTING] Routing to DataSource: {}", currentDataSource);

        return currentDataSource;
    }
}
