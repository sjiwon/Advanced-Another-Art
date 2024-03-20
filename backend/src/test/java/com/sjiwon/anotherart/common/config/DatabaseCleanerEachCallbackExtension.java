package com.sjiwon.anotherart.common.config;

import com.sjiwon.anotherart.common.utils.DatabaseCleaner;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerEachCallbackExtension implements AfterEachCallback {
    @Override
    public void afterEach(final ExtensionContext context) {
        SpringExtension.getApplicationContext(context)
                .getBean(DatabaseCleaner.class)
                .cleanUpDatabase();
    }
}
