rootProject.name = "Advanced-Another-Art"

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val flywayVersion: String by settings
    val jooqPluginVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
                "org.flywaydb.flyway" -> useVersion(flywayVersion)
                "nu.studer.jooq" -> useVersion(jooqPluginVersion)
            }
        }
    }
}
