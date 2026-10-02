package mezlogo.mvnexplore.core.localrepo.impl

import mezlogo.mvnexplore.core.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.core.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.port.model.MavenArtifact

class LocalRepoService: LocalRepoUseCase {
    override fun selectArtifacts(listArtifactsCommand: ListArtifactsCommand): List<MavenArtifact> {
        TODO("Not yet implemented")
    }
}
