package mezlogo.mvnexplore.port.out.index

import java.nio.file.Path

interface IndexUseCase {
  fun createGroupAndArtifactId(indexConfig: IndexConfig, indexToCreate: Path): String
}
