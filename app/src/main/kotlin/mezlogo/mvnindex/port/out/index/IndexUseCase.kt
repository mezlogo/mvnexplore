package mezlogo.mvnindex.port.out.index

import mezlogo.mvnindex.port.model.MavenArtifact

interface IndexUseCase {
  fun selectArtifacts(
      indexConfig: IndexConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact>
}
