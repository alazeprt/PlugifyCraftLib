plugins {
    id("java")
    kotlin("jvm")
}

group = "top.alazeprt.pclib"
version = "1.17"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
    implementation("org.yaml:snakeyaml:2.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}