
plugins {
    id("java")
}

group = "ncepu.sa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.jfree/jfreechart
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("org.slf4j:slf4j-api:1.7.36")
    // https://mvnrepository.com/artifact/redis.clients/jedis
    implementation("redis.clients:jedis:6.0.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    implementation("mysql:mysql-connector-java:8.0.33")
}

tasks.test {
    useJUnitPlatform()
}