package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.global.config.etc.P6SpyConfig;
import com.sjiwon.anotherart.global.config.infra.QueryDslConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Tag("Repository")
@DataJpaTest(showSql = false)
@ExtendWith(MySqlTestContainersExtension.class)
@Import({QueryDslConfig.class, P6SpyConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTest {
}
