plugins {
    checkstyle
}

allprojects {
    group = "io.github.sylviameows"
    version = "0.10.5"

    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "10.21.4"
    }
}

task("version") {
    println(project.version);
}
