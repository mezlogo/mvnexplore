package mezlogo.mvnindex.port.out.index

import java.nio.file.Path

data class IndexConfig(
    val pathToIndexGzip: Path,
)
