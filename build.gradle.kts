plugins {
	alias(libs.plugins.loom)
	java
}

val lwjglNatives = resolveLwjglNatives()

val modVersion = "${providers.gradleProperty("mod_version").get()}+${libs.versions.bta.get()}"
val modGroup: Provider<String> = providers.gradleProperty("mod_group")
val modName: Provider<String> = providers.gradleProperty("mod_name")

val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

base.archivesName = modName
group = modGroup.get()
version = modVersion
loom {
	val btaChannel = libs.versions.btaChannel.get()
	val btaVersion = (if (btaChannel == "nightly") "" else "v") + libs.versions.bta.get()
	customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${btaChannel}/$btaVersion/manifest.json")
}
repositories {
	mavenCentral()
	maven("https://maven.fabricmc.net/") { name = "Fabric" }
	maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
	maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
	maven("https://maven.thesignalumproject.net/nightly") { name = "SignalumMavenNightly" }
	ivy("https://piston-data.mojang.com") {
		patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
		metadataSources { artifact() }
	}
}
dependencies {
	minecraft("::${libs.versions.bta.get()}")

	// Required at compilation & runtime
	implementation(libs.loader)
	implementation(libs.halplibe)

	// Ship HalpLibe inside the jar so the mod works on its own. Fabric treats a nested jar as one more
	// candidate rather than an override: if the player already has HalpLibe, the loader picks whichever
	// version is higher. Not transitive -- the loader and Minecraft it asks for are the game's own.
	include(libs.halplibe) { isTransitive = false }

	// Only required at compilation
	compileOnly(libs.bundles.btaLwjgl)
	compileOnly(libs.joml)
	compileOnly(libs.joml.primitives)
	compileOnly(libs.slf4jApi)

	// Only required for development/launch at runtime, won't be part of any builds
	localRuntime(libs.modMenu)
	runtimeClasspath(libs.clientJar)
	val lwjglVer = libs.versions.lwjgl.get()
	localRuntime(platform("org.lwjgl:lwjgl-bom:${lwjglVer}"))
	localRuntime("org.lwjgl:lwjgl::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-glfw::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-openal::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-opengl::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-stb::$lwjglNatives")
}
java {
	toolchain {
		languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	sourceCompatibility = JavaVersion.toVersion(javaVersion.get())
	targetCompatibility = JavaVersion.toVersion(javaVersion.get())
	withSourcesJar()
}
tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.get().toString()
		targetCompatibility = javaVersion.get().toString()
		if (javaVersion.get() > 8) options.release = javaVersion
	}
	named<UpdateDaemonJvm>("updateDaemonJvm") {
		languageVersion = libs.versions.gradleJava.map { JavaLanguageVersion.of(it.toInt()) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
	withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
	withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
	processResources {
		val resourceMap = mapOf(
			"version" to modVersion,
			"fabricloader" to libs.versions.loader.get(),
			"halplibe" to libs.versions.halplibe.get(),
			"java" to libs.versions.java.get(),
			"modmenu" to libs.versions.modMenu.get()
		)
		inputs.properties(resourceMap)

		duplicatesStrategy = DuplicatesStrategy.INCLUDE
		with(copySpec {
			from("src/main/resources/") {
				include("fabric.mod.json")
				include("*.mixins.json")
				expand(resourceMap)
			}
		})
	}
}

/**
 * Fails the build if a class under the mixin package is not listed in the mixin config.
 *
 * The config claims `com.shapesnsizes.mixin` wholesale, and the transformer treats every class it
 * loads from there as a mixin -- an ordinary class or interface parked in that package is not
 * ignored, it throws the moment something first touches it. That can be a long way into a session:
 * a helper interface only the in-world player code references got as far as a player's first world
 * before bringing the game down, having started and reached the title screen perfectly happily.
 *
 * A text match rather than anything clever: every mixin is named in the config, so the config is
 * the list, and anything in the package missing from it is the bug this catches.
 */
val verifyMixinsRegistered by tasks.registering {
	group = "verification"
	description = "Fails if a class under the mixin package is missing from the mixin config."
	val mixinRoot = layout.projectDirectory.dir("src/main/java/com/shapesnsizes/mixin")
	val configFile = layout.projectDirectory.file("src/main/resources/shapesnsizes.mixins.json")
	inputs.dir(mixinRoot)
	inputs.file(configFile)
	outputs.upToDateWhen { false }
	doLast {
		val config = configFile.asFile.readText()
		val root = mixinRoot.asFile
		val unregistered = root.walkTopDown()
			.filter { it.isFile && it.extension == "java" }
			.map { it.relativeTo(root).path.removeSuffix(".java").replace(File.separatorChar, '.') }
			.filterNot { config.contains("\"$it\"") }
			.toList()
		if (unregistered.isNotEmpty()) {
			throw GradleException(
				"These classes live under the mixin package but are not listed in " +
					"shapesnsizes.mixins.json, which crashes the game when they are first loaded: " +
					unregistered.joinToString(", ") +
					". Either add them to the config or move them out of the mixin package."
			)
		}
	}
}

tasks.named("check") { dependsOn(verifyMixinsRegistered) }
tasks.named("build") { dependsOn(verifyMixinsRegistered) }

// Removes all outdated manifest.json dependencies
configurations.configureEach {
	exclude(group = "org.lwjgl.lwjgl")
	exclude(group = "net.java.jutils")
	exclude(group = "net.java.jinput")
	exclude(group = "net.sf.jopt-simple")
	exclude(group = "net.minecraft", module = "launchwrapper")
}

fun resolveLwjglNatives(): String { // Sourced from https://www.lwjgl.org/
	return Pair(
		System.getProperty("os.name")!!,
		System.getProperty("os.arch")!!
	).let { (name, arch) ->
		when {
			arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
				if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
					"natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
				else
					"natives-linux"
			arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
				"natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
			arrayOf("Windows").any { name.startsWith(it) } ->
				if (arch.contains("64"))
					"natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
				else
					"natives-windows-x86"
			else ->
				throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
		}
	}
}
