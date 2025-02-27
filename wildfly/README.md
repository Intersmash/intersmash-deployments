# Intersmash Deployments - WildFly

Application server deployments that support latest WildFly, EAP 8.0.z, EAP 8.1.x and EAP XP 6.x deliverables 

## Profiles

- `eap-xp6`
Adding `-Peap-xp6` to the build will let Maven use the specifics for building an EAP XP 6 deployment, rather than a 
WildFly one - which is the default behavior.

**IMPORTANT**:
_So far, no EAP XP 6 bits have been released, so the configuration is still using EAP XP 5 bits._

## Properties

| Name                                                                                                       | Default (WildFly)                                        | Default (EAP XP 6)                                                  |
|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|---------------------------------------------------------------------|
| **WildFly/EAP 8.z Maven Plugin coordinates**                                                               |                                                          |                                                                     |
| wildfly-maven-plugin.groupId                                                                               | org.wildfly.plugins                                      | org.jboss.eap.plugins                                               |
| wildfly-maven-plugin.artifactId                                                                            | wildfly-maven-plugin                                     | eap-maven-plugin                                                    |
| wildfly-maven-plugin.version                                                                               | 5.1.1.Final                                              | 1.0.0.Final-redhat-00014                                            |
| **Default WildFly/EAP 8.z stream version**                                                                 |                                                          |                                                                     |
| wildfly.version                                                                                            | 35.0.0.Final                                             | N/A - Not used                                                      |
| **Default WildFly/EAP 8 `ee` BOMs version is set here and can be overridden for pulling the right BOM**    |                                                          |                                                                     |
| bom.wildfly-ee.groupId                                                                                     | org.wildfly.bom                                          | N/A - Not used                                                      |
| bom.wildfly-ee.artifactId                                                                                  | wildfly-ee                                               | N/A - Not used                                                      |
| bom.wildfly-ee.version                                                                                     | ${wildfly.version}                                       | N/A - Not used                                                      |
| **Default WildFly `microprofile` BOM version is set here and can be overridden for pulling the right BOM** |                                                          |                                                                     |
| bom.wildfly-microprofile.groupId                                                                           | org.wildfly.bom                                          | org.jboss.bom                                                       |
| bom.wildfly-microprofile.artifactId                                                                        | wildfly-expansion                                        | jboss-eap-xp-microprofile                                           |
| bom.wildfly-microprofile.version                                                                           | ${version.wildfly-server}                                | 5.0.0.GA-redhat-00009                                               |
| **Default WildFly/EAP 8.z Feature Pack coordinates**                                                       |                                                          |                                                                     |
| wildfly.feature-pack.location                                                                              | org.wildfly:wildfly-galleon-pack:${wildfly.version}      | org.jboss.eap.xp:wildfly-galleon-pack:5.0.0.GA-redhat-00005         |
| wildfly.ee-feature-pack.location                                                                           | org.wildfly:wildfly-ee-galleon-pack:${wildfly.version}   | N/A - Not used                                                      |
| **Default WildFly/EAP 8.z Cloud Feature Pack coordinates**                                                 |                                                          |                                                                     |
| wildfly.cloud-feature-pack.location                                                                        | org.wildfly.cloud:wildfly-cloud-galleon-pack:7.0.2.Final | org.jboss.eap.cloud:eap-cloud-galleon-pack:1.0.0.Final-redhat-00008 |
| **Channel manifest coordinates (empty for WildFly)**                                                       |                                                          |                                                                     |
| wildfly.ee-channel.groupId                                                                                 | org.wildfly.channels                                     | org.jboss.eap.channels                                              |
| wildfly.ee-channel.artifactId                                                                              | wildfly-ee                                               | eap-8.0                                                             |
| wildfly.ee-channel.version                                                                                 | ${wildfly.version}                                       | 1.0.1.GA-redhat-00003                                               |
| wildfly.xp-channel.groupId                                                                                 | N/A - Not used                                           | org.jboss.eap.channels                                              |
| wildfly.xp-channel.artifactId                                                                              | N/A - Not used                                           | eap-xp-5.0                                                          |
| wildfly.xp-channel.version                                                                                 | N/A - Not used                                           | 1.0.0.GA-redhat-00006                                               |
