pluginManagement {
    repositories {
        maven(url = "https://maven.glass-launcher.net/babric")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://jitpack.io/")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Station API"

include(":station-api-base")

include(":station-registry-api-v0")
include(":station-world-events-v0")
include(":station-biome-events-v0")
include(":station-recipes-v0")
include(":station-items-v0")
include(":station-blocks-v0")
include(":station-blockentities-v0")
include(":station-entities-v0")
include(":station-networking-v0")
include(":station-blockitems-v0")
include(":station-container-api-v0")
include(":station-player-api-v0")
include(":station-armor-api-v0")
include(":station-localization-api-v0")
include(":station-achievements-v0")
include(":station-keybindings-v0")
include(":station-renderer-api-v0")
include(":station-audio-loader-v0")
include(":station-lifecycle-events-v0")
include(":station-vanilla-checker-v0")
include(":station-templates-v0")
include(":station-registry-sync-v0")
include(":station-dimensions-v0")
include(":station-tools-api-v1")
include(":station-flattening-v0")
include(":station-renderer-arsenic")
include(":station-vanilla-fix-v0")
include(":station-resource-loader-v0")
include(":station-datafixer-v0")
include(":station-nbt-v0")
include(":station-gui-api-v0")
include(":station-transitive-access-wideners-v0")
include(":station-maths-v0")
include(":station-worldgen-api-v0")
include(":glass-config-api-v3")
