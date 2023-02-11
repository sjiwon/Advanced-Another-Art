package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.global.config.QueryDslConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfiguration.class)
public class RepositoryTest {
}
