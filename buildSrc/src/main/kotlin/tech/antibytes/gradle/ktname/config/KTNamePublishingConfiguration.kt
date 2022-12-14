/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.ktname.config

import tech.antibytes.gradle.publishing.api.DeveloperConfiguration
import tech.antibytes.gradle.publishing.api.GitRepositoryConfiguration
import tech.antibytes.gradle.publishing.api.LicenseConfiguration
import tech.antibytes.gradle.publishing.api.MavenRepositoryConfiguration
import tech.antibytes.gradle.publishing.api.MemorySigningConfiguration
import tech.antibytes.gradle.publishing.api.SourceControlConfiguration
import tech.antibytes.gradle.versioning.api.VersioningConfiguration

open class KTNamePublishingConfiguration {
    private val username = System.getenv("PACKAGE_REGISTRY_UPLOAD_USERNAME")?.toString() ?: ""
    private val password = System.getenv("PACKAGE_REGISTRY_UPLOAD_TOKEN")?.toString() ?: ""

    private val key = System.getenv("MAVEN_KEY") ?: ""
    private val passphrase = System.getenv("MAVEN_PASSPHRASE") ?: ""

    private val nexusUsername = System.getenv("OSSR_USERNAME") ?: ""
    private val nexusPassword = System.getenv("OSSR_PASSWORD") ?: ""

    private val githubOwner = "bitPogo"
    private val githubRepository = "ktname"

    private val host = "github.com"
    private val path = "$githubOwner/$githubRepository"

    protected val gitHubRepositoryPath = "$host/$path"
    private val gitHubOwnerPath = "$host/$githubOwner"

    protected val license = LicenseConfiguration(
        name = "Apache License, Version 2.0",
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt",
        distribution = "repo",
    )

    protected val developer = DeveloperConfiguration(
        id = githubOwner,
        name = githubOwner,
        url = "https://$host/$githubOwner",
        email = "bitpogo@antibytes.tech",
    )

    protected val sourceControl = SourceControlConfiguration(
        url = "git://$gitHubRepositoryPath.git",
        connection = "scm:git://$gitHubRepositoryPath.git",
        developerConnection = "scm:git://$gitHubRepositoryPath.git",
    )

    val repositories = setOf(
        MavenRepositoryConfiguration(
            name = "MavenCentral",
            url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/",
            username = nexusUsername,
            password = nexusPassword,
        ),
        GitRepositoryConfiguration(
            name = "Development",
            gitWorkDirectory = "dev",
            url = "https://$gitHubOwnerPath/maven-dev",
            username = username,
            password = password,
        ),
        GitRepositoryConfiguration(
            name = "Snapshot",
            gitWorkDirectory = "snapshots",
            url = "https://$gitHubOwnerPath/maven-snapshots",
            username = username,
            password = password,
        ),
        GitRepositoryConfiguration(
            name = "Release",
            gitWorkDirectory = "releases",
            url = "https://$gitHubOwnerPath/maven-releases",
            username = username,
            password = password,
        ),
    )

    val versioning = VersioningConfiguration(
        featurePrefixes = listOf("feature"),
    )

    val signing = MemorySigningConfiguration(
        key = key,
        password = passphrase,
    )

    companion object {
        private val configuration = KTNamePublishingConfiguration()

        val repositories = configuration.repositories
        val versioning = configuration.versioning
    }
}
