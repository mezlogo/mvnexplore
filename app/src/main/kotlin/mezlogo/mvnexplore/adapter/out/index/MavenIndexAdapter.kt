package mezlogo.mvnexplore.adapter.out.index

import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import org.apache.maven.index.reader.ChunkReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPOutputStream

fun String.indexOfNth(char: Char, n: Int): Int {
  if (n <= 0) return -1
  var index = -1
  for (i in 1..n) {
    index = this.indexOf(char, index + 1)
    if (index == -1) break
  }
  return index
}

class MavenIndexAdapter : IndexUseCase {

  override fun createGroupAndArtifactId(
      indexConfig: IndexConfig,
      indexToCreate: Path,
  ) {
    val indexFileGzip = indexConfig.pathToIndexGzip

    Files.newOutputStream(indexToCreate).use { fos ->
      GZIPOutputStream(fos).use { gzip ->
        OutputStreamWriter(gzip, StandardCharsets.UTF_8).buffered().use { writer ->
          Files.newInputStream(indexFileGzip).use { fis ->
            ChunkReader("nexus-maven-repository-index", fis).use { chunkReader ->
              /**
               * example of chunk: {d=This project contains libraries to interface with Zimbabwe's
               * Leading Payments Gateway Paynow,
               * u=zw.co.paynow|java-sdk|1.1.2|javadoc|jar.asc.sha256,
               * i=jar.asc.sha256|1738155103000|64|2|2|0|jar.asc.sha256, m=1765378927627,
               * n=java-sdk}
               */
              var previousGroupAndArtifactId = ""
              for (record in chunkReader) {
                /** example of u: "zone.src.sheaf|web-sheaf|0.1.7|sources|jar" */
                val unique = record["u"] ?: continue
                val groupAndArtifactId = extractGroupAndArtifactId(unique)

                /** This preverns any duplications. */
                if (groupAndArtifactId != previousGroupAndArtifactId) {
                  previousGroupAndArtifactId = groupAndArtifactId
                  writer.append(groupAndArtifactId)
                  writer.append('\n')
                }
              }
            }
          }
        }
      }
    }
  }

  private fun extractGroupAndArtifactId(u: String): String {
    val indexForSplit = u.indexOfNth('|', 2)
    return u.substring(0, indexForSplit)
  }
}
