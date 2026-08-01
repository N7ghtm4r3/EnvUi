import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    alias(libs.plugins.changelog)
    alias(libs.plugins.intellijPlatform)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.grammarKit)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation(libs.junit)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.3.5")
        testFramework(TestFrameworkType.Platform)

        // Add plugin dependencies for compilation here:
        bundledPlugin("com.intellij.properties")
        composeUI()
    }
}

compose.resources {
    packageOfResClass = "com.tecknobit.envui.generated.resources"
    generateResClass = always
}

sourceSets {
    main {
        java.srcDir("src/main/gen")
    }
}

tasks {
    generateParser {
        sourceFile.set(file("src/main/grammar/dEnv.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("/com/tecknobit/envui/ide/envfile/EnvParser.java")
        pathToPsiRoot.set("/com/tecknobit/envui/ide/envfile")
        purgeOldFiles.set(true)
    }

    generateLexer {
        dependsOn(generateParser)
        sourceFile.set(file("src/main/grammar/_EnvLexer.flex"))
        targetOutputDir.set(file("src/main/gen/com/tecknobit/envui/ide/envfile"))
        purgeOldFiles.set(false)
    }

    compileKotlin {
        dependsOn(generateLexer)
    }

    compileJava {
        dependsOn(generateLexer)
    }
}
