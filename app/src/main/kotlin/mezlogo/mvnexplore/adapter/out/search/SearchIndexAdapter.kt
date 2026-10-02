
package mezlogo.mvnexplore.adapter.out.search

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase

/**
 * Uses the Maven Central Solr API documented in maven-central-search-api-openapi.yml.
 */
class SearchIndexAdapter(
    private val baseUrl: String = "https://search.maven.org/solrsearch",
    private val rows: Int = 200,
) : SearchIndexUseCase {
    private val httpClient = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override fun searchArtifacts(request: SearchArtifactsRequest): List<MavenArtifact> {
        val query = buildQuery(request)
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8)
        val uri = URI.create("${baseUrl.trimEnd('/')}/select?q=$encodedQuery&rows=$rows&wt=json")
        val httpRequest = HttpRequest.newBuilder(uri).GET().build()
        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            error("Search request failed with status ${response.statusCode()}: ${response.body()}")
        }
        val payload = json.decodeFromString<SolrSearchResponse>(response.body())
        return payload.response.docs.mapNotNull { doc ->
            val groupId = doc.g ?: return@mapNotNull null
            val artifactId = doc.a ?: return@mapNotNull null
            val version = doc.v ?: doc.latestVersion ?: return@mapNotNull null
            MavenArtifact(groupId, artifactId, version)
        }
    }

    private fun buildQuery(request: SearchArtifactsRequest): String {
        val parts = mutableListOf<String>()
        request.groupGlob?.takeIf { it.isNotBlank() }?.let { parts += "g:${it.toSolrWildcard()}" }
        request.artifactGlob?.takeIf { it.isNotBlank() }?.let { parts += "a:${it.toSolrWildcard()}" }
        return if (parts.isEmpty()) "*:*" else parts.joinToString(" AND ")
    }

    private fun String.toSolrWildcard(): String = this
}

@Serializable
private data class SolrSearchResponse(
    val response: SolrResponseBody,
)

@Serializable
private data class SolrResponseBody(
    val docs: List<SolrArtifactDoc> = emptyList(),
)

@Serializable
private data class SolrArtifactDoc(
    val g: String? = null,
    val a: String? = null,
    val v: String? = null,
    val latestVersion: String? = null,
)

    