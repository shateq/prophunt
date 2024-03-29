plugins {
    `java-library`
    `maven-publish`
    id("fabric-loom")
    id("com.modrinth.minotaur")
}

fun e(key: String) = project.extra.get(key) as String

version = "1.0.0"
group = "shateq.mods"
base.archivesName.set("prop-hunt-mc${e("mc")}")
description = "Introduce a fuzzy search to Minecraft."

repositories.mavenCentral()
dependencies {
    minecraft("com.mojang:minecraft:${e("mc")}")
    mappings("net.fabricmc:yarn:${e("yarn")}:v2")
    //mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${e("loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${e("fapi")}")

    implementation("me.xdrop:fuzzywuzzy:1.4.0")
    include("me.xdrop:fuzzywuzzy:1.4.0")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
loom.mixin.defaultRefmapName.set("prophunt.refmap.json")

tasks {
    jar {
        from("LICENSE") {
            rename { "${it}_${project.name}" }
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "description" to project.description
            )
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) //Remember to have the MODRINTH_TOKEN environment variable set or else this will fail, or set it to whatever you want - just make sure it stays private!
    projectId.set("prophunt")
    versionType.set("alpha")

    versionName.set("${project.version} for MC ${project.extra["mc"]}")
    versionNumber.set(version.toString())

    uploadFile.set(tasks[tasks.remapJar.name])
    gameVersions.addAll("1.19", "1.19.1", "1.19.2")
    dependencies {
        // scope.type: can be `required`, `optional`, `incompatible`, or `embedded`
        optional.project("fabric-api")
    }
}
