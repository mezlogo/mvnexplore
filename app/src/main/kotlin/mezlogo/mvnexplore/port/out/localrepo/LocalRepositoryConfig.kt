package mezlogo.mvnexplore.port.out.localrepo

import java.nio.file.Path

data class LocalRepositoryConfig(
    val m2repository: Path,
)
