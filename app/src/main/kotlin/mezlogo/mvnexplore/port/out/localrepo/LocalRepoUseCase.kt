package mezlogo.mvnexplore.port.out.localrepo

import mezlogo.mvnexplore.port.model.MavenArtifact

interface LocalRepoUseCase {
  fun selectArtifacts(
      localRepositoryConfig: LocalRepositoryConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact>
}
