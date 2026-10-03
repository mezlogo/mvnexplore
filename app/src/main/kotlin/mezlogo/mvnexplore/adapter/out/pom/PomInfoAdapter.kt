package mezlogo.mvnexplore.adapter.out.pom

import java.nio.file.Files
import java.nio.file.Path
import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.pom.PomInfo
import mezlogo.mvnexplore.port.out.pom.PomInfoUseCase
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.slf4j.LoggerFactory

class PomInfoAdapter : PomInfoUseCase {
  private val log = LoggerFactory.getLogger(PomInfoAdapter::class.java)

  override fun readPomInfo(pomPath: Path): PomInfo {
    log.trace("readPomInfo called with pomPath={}", pomPath)
    val model =
        Files.newBufferedReader(pomPath).use { reader ->
          log.trace("Reading POM file: {}", pomPath)
          MavenXpp3Reader().read(reader)
        }
    log.trace("Parsed model: {}", model)
    val info = model.toPomInfo()
    log.trace("Converted to PomInfo: {}", info)
    return info
  }

  private fun Model.toPomInfo(): PomInfo {
    log.trace(
        "toPomInfo for model: groupId={}, artifactId={}, version={}",
        groupId,
        artifactId,
        version,
    )
    val resolvedGroup = groupId ?: parent?.groupId
    val resolvedVersion = version ?: parent?.version
    log.trace("Resolved group={}, version={}", resolvedGroup, resolvedVersion)
    val resolvedProperties =
        properties?.let { props ->
          log.trace("Properties present: {}", props)
          props.stringPropertyNames().associateWith { key -> props.getProperty(key) }
        } ?: emptyMap()
    log.trace("Resolved properties: {}", resolvedProperties)
    val dependencies =
        dependencies.orEmpty().map {
          log.trace("Mapping dependency: {}", it)
          it.toMavenArtifact()
        }
    val bom =
        dependencyManagement?.dependencies.orEmpty().map {
          log.trace("Mapping BOM dependency: {}", it)
          it.toMavenArtifact()
        }
    return PomInfo(
        group = resolvedGroup,
        artifact = artifactId,
        version = resolvedVersion,
        description = description,
        properties = resolvedProperties,
        dependencies = dependencies,
        bom = bom,
    )
  }

  private fun Dependency.toMavenArtifact(): MavenArtifact {
    log.trace(
        "toMavenArtifact for dependency: groupId={}, artifactId={}, version={}",
        groupId,
        artifactId,
        version,
    )
    val artifact =
        MavenArtifact(
            groupId = groupId ?: "",
            artifactId = artifactId ?: "",
            version = version ?: "",
        )
    log.trace("Created MavenArtifact: {}", artifact)
    return artifact
  }
}
