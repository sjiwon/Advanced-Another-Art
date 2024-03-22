import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
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
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Query Builder
    implementation("com.querydsl:querydsl-jpa:${property("queryDslVersion")}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${property("queryDslVersion")}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:${property("redissonVersion")}")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Cloud Infra
    listOf(
        "spring-cloud-aws-starter",
        "spring-cloud-aws-starter-s3",
        "spring-cloud-aws-starter-ses",
    ).forEach {
        implementation("io.awspring.cloud:$it:${property("awspringVersion")}")
    }
    implementation("ca.pjer:logback-awslogs-appender:${property("cloudWatchLogAppenderVersion")}")

    // Log & Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jwtTokenVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jwtTokenVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jwtTokenVersion")}")

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

    // TestContainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    // TestContainers + RDB(MySQL)
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-spring-test:${property("flywayTestExtensionVersion")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// QueryDsl QClass
val queryDslTypeDir: String = "src/main/generated"

tasks.withType<JavaCompile>().configureEach {
    options.generatedSourceOutputDirectory.set(file(queryDslTypeDir))
}

sourceSets {
    getByName("main").java.srcDirs(queryDslTypeDir)
}

tasks.named("clean") {
    doLast {
        file(queryDslTypeDir).deleteRecursively()
    }
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
