plugins {
    `kotlin-dsl`
}

// Convention plugins apply plugins without versions, so their marker artifacts are dependencies here.
fun Provider<PluginDependency>.marker() = map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }

dependencies {
    implementation(libs.plugins.spotless.plugin.marker())
    implementation(libs.plugins.spring.dependency.management.marker())
}
