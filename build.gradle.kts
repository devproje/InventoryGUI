plugins {
    kotlin("jvm") version "1.5.20"
    java
    `java-library`
    `maven-publish`
    signing
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    val implementation by configurations

    dependencies {
        implementation(kotlin("stdlib"))
        compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    }
}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("source")
    from(sourceSets["main"].allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

publishing {
    publications {
        create<MavenPublication>(rootProject.name) {
            from(components["java"])
            artifact(tasks["sourceJar"])
            artifact(tasks["javadocJar"])

            val mavenUploadUser: String by project
            val mavenUploadPwd: String by project

            repositories {
                maven {
                    name = "MavenCentral"
                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

                    credentials {
                        username = mavenUploadUser
                        password = mavenUploadPwd
                    }
                }
            }

            pom {
                name.set(rootProject.name)
                description.set("")
                url.set("https://github.com/ProjectTL12345/InventoryGUI/")

                developers {
                    developer {
                        name.set("ProjectTL12345")
                        email.set("me@projecttl.net")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/ProjectTL12345/InventoryGUI.git")
                    developerConnection.set("scm:git:https://github.com/ProjectTL12345/InventoryGUI.git")
                    url.set("https://github.com/ProjectTL12345/")
                }

            }
        }
    }
}

signing {
    val pgpSigningKey: String? by project
    val pgpSigningPwd: String? by project
    useInMemoryPgpKeys(pgpSigningKey, pgpSigningPwd)
    sign(publishing.publications["mavenJava"])
}