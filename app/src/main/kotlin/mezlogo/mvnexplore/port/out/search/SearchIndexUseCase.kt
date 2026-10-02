package mezlogo.mvnexplore.port.out.search

import mezlogo.mvnexplore.port.model.MavenArtifact

interface SearchIndexUseCase {
    fun searchArtifacts(request: SearchArtifactsRequest): List<MavenArtifact>
}
