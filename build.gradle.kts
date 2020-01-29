plugins {
    java
    application
}
application {
    mainClassName = "ComputerVisionQuickstarts"
}
repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "com.microsoft.azure.cognitiveservices", name = "azure-cognitiveservices-computervision", version = "1.0.2-beta")
    implementation("com.google.code.gson:gson:2.8.6")
}
