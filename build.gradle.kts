buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

allprojects {
    group = "com.github.parimatchtech"

    repositories {
        jcenter()
        mavenCentral()
    }
}
