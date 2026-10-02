package mezlogo.mvnexplore.core.localrepo

import mezlogo.mvnexplore.port.model.MavenArtifact

interface LocalRepoUseCase {
    fun selectArtifacts(listArtifactsCommand: ListArtifactsCommand): List<MavenArtifact>
}
