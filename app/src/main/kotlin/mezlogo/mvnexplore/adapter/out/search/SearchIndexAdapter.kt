package mezlogo.mvnexplore.adapter.out.search

import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase


/**
 * Use jdk 25 http client
 */
class SearchIndexAdapter: SearchIndexUseCase {
    override fun searchArtifacts(request: SearchArtifactsRequest): List<MavenArtifact> {
        TODO("Not yet implemented")
    }
}
