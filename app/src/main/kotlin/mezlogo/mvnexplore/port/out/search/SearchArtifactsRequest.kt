package mezlogo.mvnexplore.port.out.search

data class SearchArtifactsRequest(
    val groupGlob: String?,
    val artifactGlob: String?,
)
