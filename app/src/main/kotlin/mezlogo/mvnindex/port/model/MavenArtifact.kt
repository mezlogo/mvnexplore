package mezlogo.mvnindex.port.model

data class MavenArtifact(
    val groupId: String,
    val artifactId: String,
    val version: String,
)
