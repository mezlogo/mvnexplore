package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import mezlogo.mvnexplore.port.out.pom.PomInfoUseCase
import org.slf4j.LoggerFactory

class InfoCommand(
    private val root: RootCommand,
    private val pomInfoUseCase: PomInfoUseCase,
) : CliktCommand(name = "info") {
  private val log = LoggerFactory.getLogger(InfoCommand::class.java)

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
    root.applyVerboseLogging()

    log.trace("Running InfoCommand with pom={}, dep={}", pom, dep)
    val pomPath =
        when {
          pom != null -> {
            log.trace("Using provided --pom: {}", pom)
            pom!!
          }
          dep != null -> {
            log.trace("Resolving pom from --dep: {}", dep)
            resolvePomFromDep(dep!!)
          }
          else -> {
            log.trace("Neither --pom nor --dep provided")
            echo("Either --pom or --dep is required")
            return
          }
        }

    log.trace("Resolved pomPath: {}", pomPath)
    if (!pomPath.exists() || !pomPath.isRegularFile()) {
      log.trace("POM not found or not a regular file: {}", pomPath)
      echo("POM not found: $pomPath")
      return
    }

    log.trace("Reading POM info from: {}", pomPath)
    val info = pomInfoUseCase.readPomInfo(pomPath)
    log.trace("Received PomInfo: {}", info)
    echo("Group: ${info.group ?: ""}")
    echo("Artifact: ${info.artifact ?: ""}")
    echo("Version: ${info.version ?: ""}")
    echo("Description: ${info.description ?: ""}")

    if (info.properties.isNotEmpty()) {
      log.trace("Printing properties: {}", info.properties)
      echo("Properties:")
      info.properties.toSortedMap().forEach { (key, value) -> echo("  $key=$value") }
    }

    if (info.dependencies.isNotEmpty()) {
      log.trace("Printing dependencies: {}", info.dependencies)
      echo("Dependencies:")
      info.dependencies.forEach { echo("  ${it.groupId}:${it.artifactId}:${it.version}") }
    }

    if (info.bom.isNotEmpty()) {
      log.trace("Printing BOM: {}", info.bom)
      echo("BOM:")
      info.bom.forEach { echo("  ${it.groupId}:${it.artifactId}:${it.version}") }
    }
  }

  private fun resolvePomFromDep(dep: String): Path {
    log.trace("resolvePomFromDep called with dep={}", dep)
    val parts = dep.split(':')
    log.trace("Split dep into parts: {}", parts)
    require(parts.size >= 3) { "Dependency must be group:artifact:version" }
    val groupPath = parts[0].replace('.', '/')
    val artifact = parts[1]
    val version = parts[2]
    log.trace("groupPath={}, artifact={}, version={}", groupPath, artifact, version)
    val path =
        root.repo
            .resolve(groupPath)
            .resolve(artifact)
            .resolve(version)
            .resolve("$artifact-$version.pom")
    log.trace("Resolved pom path: {}", path)
    return path
  }
}
