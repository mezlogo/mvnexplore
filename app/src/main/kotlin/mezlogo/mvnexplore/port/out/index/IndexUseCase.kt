package mezlogo.mvnexplore.port.out.index

import mezlogo.mvnexplore.port.model.MavenArtifact

interface IndexUseCase {
  fun selectArtifacts(
      indexConfig: IndexConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact>
}
