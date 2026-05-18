pluginManagement {

    // This has to live in the pluginManagement block because it's a special
    // block that gets evaluated before all other parts of the script
    val configureUuKotlinBuildRepo: MavenArtifactRepository.() -> Unit = {
        name = "UUKotlinBuildGitHubPackages"
        url = uri(System.getenv("UU_KOTLIN_BUILD_PACKAGE_URL"))
        credentials {
            username = providers.gradleProperty("gpr.user").orNull
                ?: System.getenv("GITHUB_ACTOR")
            // CI: pass secrets.RELEASE_PAT as env GPR_TOKEN (do not rely on default GITHUB_TOKEN for another repo's Packages).
            password = providers.gradleProperty("gpr.token").orNull
                ?: System.getenv("GPR_TOKEN")
                        ?: System.getenv("GITHUB_TOKEN")
        }
    }

    // Store the closure for use in the dependencyResolutionManagement block below
    settings.extra["uuKotlinBuildConfiguration"] = configureUuKotlinBuildRepo

    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(configureUuKotlinBuildRepo)


    }
}

dependencyResolutionManagement {
    @Suppress("UNCHECKED_CAST")
    val configureUuKotlinBuildRepo = settings.extra["uuKotlinBuildConfiguration"] as MavenArtifactRepository.() -> Unit

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(configureUuKotlinBuildRepo)
    }

    versionCatalogs {
        register("uuBuild") {
            val uuBuildVersion = providers.gradleProperty("uu_build").orNull
                ?: error("Set `uu_build=<version>` in gradle.properties.")
            from("com.silverpine.uu:uu-kotlin-build-catalog:$uuBuildVersion")
        }
    }
}

rootProject.name = "UUKotlinCore"

include(":library")
