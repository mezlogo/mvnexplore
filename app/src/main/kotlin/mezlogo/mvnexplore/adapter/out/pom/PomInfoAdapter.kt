package mezlogo.mvnexplore.adapter.out.pom

import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.pom.PomInfo
import mezlogo.mvnexplore.port.out.pom.PomInfoUseCase
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.nio.file.Files
import java.nio.file.Path

class PomInfoAdapter : PomInfoUseCase {

  override fun readPomInfo(pomPath: Path): PomInfo {

    val model =
        Files.newBufferedReader(pomPath).use { reader ->
          MavenXpp3Reader().read(reader)
        }

    val info = model.toPomInfo()

    return info
  }

  private fun Model.toPomInfo(): PomInfo {

    val resolvedGroup = groupId ?: parent?.groupId
    val resolvedVersion = version ?: parent?.version

    val resolvedProperties =
        properties?.let { props ->
          props.stringPropertyNames().associateWith { key -> props.getProperty(key) }
        } ?: emptyMap()

    val dependencies =
        dependencies.orEmpty().map {
          it.toMavenArtifact()
        }
    val bom =
        dependencyManagement?.dependencies.orEmpty().map {
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

    val artifact =
        MavenArtifact(
            groupId = groupId ?: "",
            artifactId = artifactId ?: "",
            version = version ?: "",
        )

    return artifact
  }
}
