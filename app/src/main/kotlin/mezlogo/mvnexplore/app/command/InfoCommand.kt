
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import mezlogo.mvnexplore.port.out.pom.PomInfoUseCase

class InfoCommand(
    private val root: RootCommand,
    private val pomInfoUseCase: PomInfoUseCase,
) : CliktCommand(name = "info") {
    private val pom: Path? by
        option(
            "--pom",
            help = "Path to pom.xml",
        )
        .path()

    private val dep: String? by
        option(
            "--dep",
            help = "Dependency coordinates group:artifact:version",
        )

    override fun run() {
        val pomPath =
            when {
                pom != null -> pom!!
                dep != null -> resolvePomFromDep(dep!!)
                else -> {
                    echo("Either --pom or --dep is required")
                    return
                }
            }

        if (!pomPath.exists() || !pomPath.isRegularFile()) {
            echo("POM not found: $pomPath")
            return
        }

        val info = pomInfoUseCase.readPomInfo(pomPath)
        echo("Group: ${info.group ?: ""}")
        echo("Artifact: ${info.artifact ?: ""}")
        echo("Version: ${info.version ?: ""}")
        echo("Description: ${info.description ?: ""}")

        if (info.properties.isNotEmpty()) {
            echo("Properties:")
            info.properties.toSortedMap().forEach { (key, value) -> echo("  $key=$value") }
        }

        if (info.dependencies.isNotEmpty()) {
            echo("Dependencies:")
            info.dependencies.forEach { echo("  ${it.groupId}:${it.artifactId}:${it.version}") }
        }

        if (info.bom.isNotEmpty()) {
            echo("BOM:")
            info.bom.forEach { echo("  ${it.groupId}:${it.artifactId}:${it.version}") }
        }
    }

    private fun resolvePomFromDep(dep: String): Path {
        val parts = dep.split(':')
        require(parts.size >= 3) { "Dependency must be group:artifact:version" }
        val groupPath = parts[0].replace('.', '/')
        val artifact = parts[1]
        val version = parts[2]
        return root.repo
            .resolve(groupPath)
            .resolve(artifact)
            .resolve(version)
            .resolve("$artifact-$version.pom")
    }
}

    