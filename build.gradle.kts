plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
