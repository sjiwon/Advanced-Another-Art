package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.global.config.P6SpyConfiguration;
import com.sjiwon.anotherart.global.config.QueryDslConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = false)
@ExtendWith(MySqlTestContainersExtension.class)
@Import({QueryDslConfiguration.class, P6SpyConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTest {
}
