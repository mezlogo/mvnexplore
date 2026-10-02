
package mezlogo.mvnexplore.core.pom.impl

import java.nio.file.Files
import java.nio.file.Path
import mezlogo.mvnexplore.core.pom.PomInfo
import mezlogo.mvnexplore.core.pom.PomInfoUseCase
import mezlogo.mvnexplore.port.model.MavenArtifact
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader

class PomInfoService : PomInfoUseCase {
    override fun readPomInfo(pomPath: Path): PomInfo {
        val model = Files.newBufferedReader(pomPath).use { reader ->
            MavenXpp3Reader().read(reader)
        }
        return model.toPomInfo()
    }

    private fun Model.toPomInfo(): PomInfo {
        val resolvedGroup = groupId ?: parent?.groupId
        val resolvedVersion = version ?: parent?.version
        val resolvedProperties = properties?.let { props ->
            props.stringPropertyNames().associateWith { key -> props.getProperty(key) }
        } ?: emptyMap()
        return PomInfo(
            group = resolvedGroup,
            artifact = artifactId,
            version = resolvedVersion,
            description = description,
            properties = resolvedProperties,
            dependencies = dependencies.orEmpty().map { it.toMavenArtifact() },
            bom = dependencyManagement?.dependencies.orEmpty().map { it.toMavenArtifact() },
        )
    }

    private fun org.apache.maven.model.Dependency.toMavenArtifact(): MavenArtifact =
        MavenArtifact(
            groupId = groupId ?: "",
            artifactId = artifactId ?: "",
            version = version ?: "",
        )
}

    