/**
 * Copyright (C) 2023 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.intersmash.deployments;

import java.io.IOException;
import java.util.Properties;

/**
 * Class loads .properties from resources and provides method to get properties.
 */
public class DeploymentProperties {
	public static final String WILDFLY_DEPLOYMENTS_BUILD_PROFILE_VALUE_EAP = "eap";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_PROFILE_VALUE_EAP_XP = "eapxp";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_COMMUNITY = "community";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_80 = "eap80";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_81 = "eap81";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP5 = "eapxp5";
	public static final String WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP6 = "eapxp6";

	static String version() {
		return "${project.version}";
	}

	static String groupID() {
		return "${project.groupId}";
	}

	static String getDeploymentsProviderPath() {
		return "${project.basedir}";
	}

	public static String getWildflyDeploymentsBuildProfile() {
		return "${intersmash.deployments.wildfly.build.profile}";
	}

	public static String getWildflyDeploymentsBuildStream() {
		return "${intersmash.deployments.wildfly.build.stream}";
	}

	public static Boolean isWildFlyDeploymentsBuildProfileEnabled() {
		return "".equals(getWildflyDeploymentsBuildProfile());
	}

	public static Boolean isCommunityDeploymentsBuildStreamEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_COMMUNITY.equals(getWildflyDeploymentsBuildStream());
	}

	public static Boolean isEapDeploymentsBuildProfileEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_PROFILE_VALUE_EAP.equals(getWildflyDeploymentsBuildProfile());
	}

	public static Boolean isEap80DeploymentsBuildStreamEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_80.equals(getWildflyDeploymentsBuildStream());
	}

	public static Boolean isEap81DeploymentsBuildStreamEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_81.equals(getWildflyDeploymentsBuildStream());
	}

	public static Boolean isEapXpDeploymentsBuildProfileEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_PROFILE_VALUE_EAP_XP.equals(getWildflyDeploymentsBuildProfile());
	}

	public static Boolean isEapXp5DeploymentsBuildStreamEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP5.equals(getWildflyDeploymentsBuildStream());
	}

	public static Boolean isEapXp6DeploymentsBuildStreamEnabled() {
		return WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP6.equals(getWildflyDeploymentsBuildStream());
	}

	public static String getWildflyDeploymentVariantFromStream(final String deploymentStream) {
		switch (deploymentStream) {
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_80:
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_81:
				return "eap";
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP5:
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP6:
				return "eapxp";
			default:
				throw new IllegalStateException("Unexpected value: " + deploymentStream);
		}
	}

	public static String getWildflyDeploymentVariantProfileNameFromStream(final String deploymentStream) {
		switch (deploymentStream) {
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_80:
				return "80";
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_81:
				return "81";
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP5:
				return "xp5";
			case DeploymentProperties.WILDFLY_DEPLOYMENTS_BUILD_STREAM_VALUE_EAP_XP6:
				return "xp6";
			default:
				throw new IllegalStateException("Unexpected value: " + deploymentStream);
		}
	}
}
