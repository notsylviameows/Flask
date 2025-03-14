plugins {
    checkstyle
}

allprojects {
    group = "io.github.sylviameows"
    version = "0.11.1"

    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "10.21.4"
    }
}

task("version") {
    println(project.version);
}
