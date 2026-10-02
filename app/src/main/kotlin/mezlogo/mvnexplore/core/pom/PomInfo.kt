
package mezlogo.mvnexplore.core.pom

import mezlogo.mvnexplore.port.model.MavenArtifact

data class PomInfo(
    val group: String?,
    val artifact: String?,
    val version: String?,
    val description: String?,
    val properties: Map<String, String>,
    val dependencies: List<MavenArtifact>,
    val bom: List<MavenArtifact>,
)

    