plugins {
    id("java")
    id("application")
    id("jacoco")
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "org.spotifumtp37"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.jqwik:jqwik:1.8.5")
}

application {
    mainClass.set("org.spotifumtp37.Main")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "org/spotifumtp37/delegate/**",
                    "org/spotifumtp37/Main.class"
                )
            }
        })
    )
    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(listOf("org.spotifumtp37.model.*", "org.spotifumtp37.util.*"))
    excludedClasses.set(listOf("org.spotifumtp37.delegate.*", "org.spotifumtp37.Main"))
    mutators.set(listOf("STRONGER"))
    outputFormats.set(listOf("HTML"))
    timestampedReports.set(false)
    threads.set(2)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}
