package mezlogo.mvnexplore.core.localrepo

import mezlogo.mvnexplore.port.model.MavenArtifact

interface LocalRepoUseCase {
    fun selectArtifacts(localRepositoryConfig: LocalRepositoryConfig, listArtifactsCommand: ListArtifactsCommand): List<MavenArtifact>
}
