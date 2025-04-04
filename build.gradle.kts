plugins {
    id("java")
    kotlin("jvm")
}

group = "top.alazeprt.pclib"
version = "1.12"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}