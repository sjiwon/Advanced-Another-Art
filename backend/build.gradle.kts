import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")
    id("nu.studer.jooq")
}

group = "${property("projectGroup")}"
version = "${property("applicationVersion")}"

java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j:${property("mysqlVersion")}")
    implementation("org.flywaydb:flyway-core:${property("flywayVersion")}")
    implementation("org.flywaydb:flyway-mysql:${property("flywayVersion")}")

    // Query Builder
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    jooqGenerator("mysql:mysql-connector-java:${property("mysqlVersion")}")
    jooqGenerator("org.jooq:jooq:${property("jooqVersion")}")
    jooqGenerator("org.jooq:jooq-codegen:${property("jooqVersion")}")
    jooqGenerator("org.jooq:jooq-meta:${property("jooqVersion")}")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:${property("redissonVersion")}")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Cloud Infra
    implementation("io.awspring.cloud:spring-cloud-aws-starter:${property("awspringVersion")}")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:${property("awspringVersion")}")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-ses:${property("awspringVersion")}")
    implementation("ca.pjer:logback-awslogs-appender:${property("cloudWatchLogAppenderVersion")}")

    // Log & Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("swaggerVersion")}")

    // Guava
    implementation("com.google.guava:guava:${property("guavaVersion")}-jre")

    // Slack API
    implementation("com.slack.api:slack-api-client:${property("slackApiVersion")}")

    // p6spy
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${property("p6spyVersion")}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-spring-test:${property("flywayTestExtensionVersion")}")

    // TestContainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:${property("testContainersVersion")}")
    testImplementation("org.testcontainers:mysql:${property("testContainersVersion")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jOOQ
val mysqlContainer = tasks.register("mysqlContainer") {
    val container = MySQLContainer<Nothing>(DockerImageName.parse("mysql:8.0.33")).apply {
        withDatabaseName("another_art")
        withUsername("root")
        withPassword("1234")
        start()
    }

    extra.set("jdbcUrl", container.jdbcUrl)
    extra.set("username", container.username)
    extra.set("password", container.password)
    extra.set("databaseName", container.databaseName)
    extra.set("container", container)
}

val mysqlJdbcUrl = mysqlContainer.get().extra["jdbcUrl"].toString()
val mysqlUsername = mysqlContainer.get().extra["username"].toString()
val mysqlPassword = mysqlContainer.get().extra["password"].toString()
val mysqlDatabaseName = mysqlContainer.get().extra["databaseName"].toString()
val container = mysqlContainer.get().extra["container"] as MySQLContainer<*>

buildscript {
    dependencies {
        classpath("mysql:mysql-connector-java:${property("mysqlVersion")}")
        classpath("org.flywaydb:flyway-mysql:${property("flywayVersion")}")
        classpath("org.testcontainers:mysql:${property("testContainersVersion")}")
    }

    configurations["classpath"].resolutionStrategy.eachDependency {
        if (requested.group.startsWith("org.jooq") && requested.name.startsWith("jooq")) {
            useVersion("${property("jooqVersion")}")
        }
    }
}

flyway {
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    url = mysqlJdbcUrl
    user = mysqlUsername
    password = mysqlPassword
}

jooq {
    version.set("${property("jooqVersion")}")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = mysqlJdbcUrl
                    user = mysqlUsername
                    password = mysqlPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = mysqlDatabaseName
                        forcedTypes.addAll(
                            arrayOf(
                                ForcedType()
                                    .withName("varchar")
                                    .withIncludeExpression(".*")
                                    .withIncludeTypes("JSONB?"),
                                ForcedType()
                                    .withName("varchar")
                                    .withIncludeExpression(".*")
                                    .withIncludeTypes("INET")
                            ).toList()
                        )
                    }
                    generate.apply {
                        isDaos = true
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        directory = "src/generated"
                        packageName = "com.sjiwon.anotherart.jooq"
                        encoding = "UTF-8"
                    }
                    strategy.name = "org.jooq.codegen.example.JPrefixGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    dependsOn("mysqlContainer")
    dependsOn("flywayMigrate")

    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migration")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    allInputsDeclared.set(true)
    outputs.cacheIf { true }
    doLast { container.stop() }
}

// Copy Submodule
tasks.register<Copy>("copySecret") {
    from("./another-art-secret")
    include("application*.yml")
    into("./src/main/resources")
}

tasks.named("processResources") {
    dependsOn("copySecret")
}

tasks.named<JavaCompile>("compileJava") {
    inputs.files(tasks.named("processResources"))
}

// jar & bootJar
tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootJar>("bootJar") {
    archiveBaseName.set("AnotherArt")
    archiveFileName.set("AnotherArt.jar")
}
