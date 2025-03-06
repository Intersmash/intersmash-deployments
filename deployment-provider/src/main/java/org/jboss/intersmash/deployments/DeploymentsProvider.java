package org.jboss.intersmash.deployments;

import java.nio.file.Path;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.jboss.intersmash.deployments.maven.ArtifactProvider;

/**
 * A class which is expected to provide access deployment applications. Archive
 * based deployments (e.g.: WAR, JAR) must be installed in local repository.
 */
public class DeploymentsProvider {
	static final String WILDFLY_MICROPROFILE_REACTIVE_MESSAGING_KAFKA_BOOTABLE_JAR = "wildfly-microprofile-reactive-messaging-kafka";
	static final String BOOTABLE_JAR_ARTIFACT_PACKAGING = "jar";

	public static Path wildflyMicroprofileReactiveMessagingKafkaBootableJar() {
		Path file = null;
		try {
			file = ArtifactProvider.resolveArtifact(DeploymentProperties.groupID(),
					WILDFLY_MICROPROFILE_REACTIVE_MESSAGING_KAFKA_BOOTABLE_JAR, DeploymentProperties.version(),
					BOOTABLE_JAR_ARTIFACT_PACKAGING, "bootable-openshift").toPath();
		} catch (SettingsBuildingException | ArtifactResolutionException e) {
			throw new RuntimeException("Can not get artifact", e);
		}
		return file;
	}
}
