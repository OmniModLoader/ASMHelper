# ASM Helper

ASM Helper is a library designed to facilitate the modification of JAR files, simplifying the process of making changes to ModFiles within the OmniMC framework.
This library is versatile and can be utilized in various contexts beyond OmniMC.

# Examples

[Examples can be seen here](./examples).

# Importing

### Maven

* Include JitPack in your maven build file

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

* Add ASMHelper as a dependency

```xml
<dependency>
    <groupId>com.github.OmniModLoader</groupId>
    <artifactId>ASMHelper</artifactId>
    <version>2.2.7</version>
</dependency>
```

### Gradle

* Add JitPack to your root `build.gradle` at the end of repositories

```gradle
repositories {
    maven {
        url 'https://jitpack.io'
    }
}
```

* Add the dependency

```gradle
dependencies {
    implementation 'com.github.OmniModLoader:ASMHelper:2.2.7'
}
```

# License

[ASMHelper is licensed under MIT](./LICENSE).

# Contributing

If you want to contribute it is best to comment everything and test it a lot with large JAR files and small ones.
All additions will be also tested by me and approved by me. But other than that you are free to contribute as much as
you want.