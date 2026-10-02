package mezlogo.mvnexplore.core.localrepo.impl

import mezlogo.mvnexplore.core.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.core.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.core.localrepo.LocalRepositoryConfig
import mezlogo.mvnexplore.port.model.MavenArtifact

/**
 * Use only file names, don't read pom.xml at all, however check that pom.xml is here.
 */
class LocalRepoService: LocalRepoUseCase {
    override fun selectArtifacts(
        localRepositoryConfig: LocalRepositoryConfig,
        listArtifactsCommand: ListArtifactsCommand
    ): List<MavenArtifact> {
        TODO("Not yet implemented")
    }
}
