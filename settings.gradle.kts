pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            name = "UUKotlinBuildGitHubPackages"
            url = uri("https://maven.pkg.github.com/SilverpineSoftware/UUKotlinBuild")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                // CI: pass secrets.RELEASE_PAT as env GPR_TOKEN (do not rely on default GITHUB_TOKEN for another repo's Packages).
                password = providers.gradleProperty("gpr.token").orNull
                    ?: System.getenv("GPR_TOKEN")
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "UUKotlinBuildGitHubPackages"
            url = uri("https://maven.pkg.github.com/SilverpineSoftware/UUKotlinBuild")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.token").orNull
                    ?: System.getenv("GPR_TOKEN")
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenLocal()
    }
    versionCatalogs {
        register("uuBuild") {
            val uuBuildVersion =
                Regex("""(?m)^uu_build\s*=\s*"([^"]+)"""")
                    .find(
                        layout.rootDirectory.file("gradle/libs.versions.toml").asFile.readText(),
                    )
                    ?.groupValues
                    ?.get(1)
                    ?: error("Set versions.uu_build in gradle/libs.versions.toml.")
            from("com.silverpine.uu:uu-kotlin-build-catalog:$uuBuildVersion")
        }
    }
}

rootProject.name = "UUKotlinCore"

include(":library")
