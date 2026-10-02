package mezlogo.mvnexplore.core.localrepo

/**
 * Traverse local maven repository and select by given filters.
 */
data class ListArtifactsCommand(
    /**
     * Filter by group id. When empty - select all.
     */
    val includeGlobGroupIds: List<String>,
    /**
     * Exclude from passed included by group id. When empty - select all.
     */
    val excludeGlobGroupIds: List<String>,
    /**
     * Filter by artifact id. When empty - select all.
     */
    val includeGlobArtifactIds: List<String>,
    /**
     * Exclude from passed included by artifact id. When empty - select all.
     */
    val excludeGlobArtifactIds: List<String>,
    /**
     * When true use simple major version detection for printing smaller output.
     */
    val onlyLatestVersions: Boolean,
)
