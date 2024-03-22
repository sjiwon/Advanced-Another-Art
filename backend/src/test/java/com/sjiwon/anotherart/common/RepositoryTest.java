package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.global.config.etc.P6SpyConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.AutoConfigureJooq;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Tag("Repository")
@ExtendWith(MySqlTestContainersExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(P6SpyConfig.class)
@AutoConfigureJooq
@DataJpaTest(showSql = false)
public abstract class RepositoryTest {
}
