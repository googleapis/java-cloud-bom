/*
 * Copyright 2020 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.opensource.cloudbomdashboard;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.apache.commons.io.FileUtils;

import org.eclipse.aether.artifact.Artifact;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Date;

/**
 * Container class for the conversion of a dashboard into a template
 */
public class VersionData {
    /* There can only be one version of our 'All Versions' page */
    public static final String ALL_VERSIONS_NAME = "all-versions";
    private static final VersionData ALL_VERSIONS_DATA = new VersionData();

    /* Helps to improve performance, so we don't have to repeatedly look up
        remote resources. */
    private static final Map<String, String> pomToDepsVersion = new HashMap<>();
    private static final Map<Artifact, String> artifactToTime = new HashMap<>();
    private static final Map<Artifact, String> artifactToLatestVersion = new HashMap<>();

    private final List<String> versions = new ArrayList<>();

    /* Everything associated with the template for this VersionData */
    private final Set<String> artifacts = new TreeSet<>();
    private final Map<String, String> currentVersion = new HashMap<>();
    private final Map<String, String> sharedDependenciesPosition = new HashMap<>();
    private final Map<String, String> newestVersion = new HashMap<>();
    private final Map<String, String> newestPomURL = new HashMap<>();
    private final Map<String, String> sharedDepsVersion = new HashMap<>();
    private final Map<String, String> updatedTime = new HashMap<>();
    private final Map<String, String> metadataURL = new HashMap<>();

    public VersionData(String cloudBomVersion) {
        versions.add(cloudBomVersion);
        ALL_VERSIONS_DATA.versions.add(cloudBomVersion);
    }

    //Constructor meant only for ALL_VERSIONS_DATA
    private VersionData() {
    }

    /**
     * Populate this dashboard with all artifacts in the keyset of the given mapping.
     * If 'addToAllVersions' is set, we will also add every artifact to the 'All Versions' page,
     * for creation in the
     *
     * @param addToAllVersions true to add all artifacts to the 'All Versions' page as well
     * @param infoMap Mapping of artifacts
     */
    public void populateData(boolean addToAllVersions, Map<Artifact, ArtifactInfo> infoMap) {
        String cloudBomVersion = versions.get(0);
        for (Map.Entry<Artifact, ArtifactInfo> info : infoMap.entrySet()) {
            insertData(cloudBomVersion, info.getKey());

            if (addToAllVersions) {
                ALL_VERSIONS_DATA.insertData(cloudBomVersion, info.getKey());
            }
        }
    }

    /**
     * Inserts an artifact's data into this VersionData.
     * Pass cloudBomVersion for the purpose of our artifact key, used in our FTL file.
     */
    private void insertData(String cloudBomVersion, Artifact a) {
        String artifactId = a.getArtifactId();
        //Concatenate this with the version of the current cloud BOM, so each entry has a unique
        //key for the mapping in our table.
        String artifactKey = artifactId + ":" + cloudBomVersion;
        String groupId = a.getGroupId();
        String version = a.getVersion();

        String latestVersion = latestVersion(a);
        String pomFileURL = getPomFileURL(groupId, artifactId, newestVersion.get(artifactId));
        String sharedDependencyVersion = sharedDependencyVersion(artifactKey, a, sharedDependenciesPosition);

        artifacts.add(artifactId);
        currentVersion.put(artifactKey, version);
        newestVersion.put(artifactKey, latestVersion);
        newestPomURL.put(artifactKey, pomFileURL);
        sharedDepsVersion.put(artifactKey, sharedDependencyVersion);
        updatedTime.put(artifactKey, updatedTime(a));
        metadataURL.put(artifactKey, getMetadataURL(a));
    }

    /**
     * Creates new mapping of template data for use in creating the All Versions page.
     */
    public static Map<String, Object> getAllVersionsTemplate() {
        return ALL_VERSIONS_DATA.getTemplateData();
    }

    /**
     * Returns the 'template data' formatting of our data.
     */
    public Map<String, Object> getTemplateData() {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("currentVersion", currentVersion);
        templateData.put("sharedDependenciesPosition", sharedDependenciesPosition);
        templateData.put("newestVersion", newestVersion);
        templateData.put("newestPomURL", newestPomURL);
        templateData.put("sharedDepsVersion", sharedDepsVersion);
        templateData.put("updatedTime", updatedTime);
        templateData.put("metadataURL", metadataURL);
        templateData.put("artifacts", artifacts);
        templateData.put("versions", versions);
        templateData.put("lastUpdated", LocalDateTime.now());
        //Our all-versions dashboard page is a special case
        if (versions.size() > 1) {
            templateData.put("staticVersion", "All Versions");
        } else if (versions.size() == 1) {
            templateData.put("staticVersion", versions.get(0));
        }
        return templateData;
    }

    private static String latestVersion(Artifact artifact) {
        if (artifactToLatestVersion.containsKey(artifact)) {
            return artifactToLatestVersion.get(artifact);
        }
        String pomPath = getMetadataURL(artifact);
        try {
            URL url = new URL(pomPath);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            Scanner s = new Scanner(conn.getInputStream());
            while (s.hasNextLine()) {
                String string = s.nextLine();
                if (string.contains("<latest>")) {
                    String version = string.split(">")[1].split("<")[0];
                    artifactToLatestVersion.put(artifact, version);
                    return version;
                }
            }
        } catch (IOException ignored) {
        }
        artifactToLatestVersion.put(artifact, "");
        return "";
    }

    private static String updatedTime(Artifact artifact) {
        if (artifactToTime.containsKey(artifact)) {
            return artifactToTime.get(artifact);
        }
        String groupPath = artifact.getGroupId().replace('.', '/');
        String metadataPath = DashboardMain.basePath + "/" + groupPath
                + "/" + artifact.getArtifactId()
                + "/maven-metadata.xml";
        try {
            URL url = new URL(metadataPath);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            Scanner s = new Scanner(conn.getInputStream());
            while (s.hasNextLine()) {
                String string = s.nextLine();
                if (string.contains("<lastUpdated>")) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                    DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");
                    String input = string.split(">")[1].split("<")[0];
                    Date date = dateFormat.parse(input);
                    artifactToTime.put(artifact, outputFormat.format(date));
                    return outputFormat.format(date);
                }
            }
        } catch (IOException | java.text.ParseException ignored) {
        }
        artifactToTime.put(artifact, "");
        return "";
    }

    /**
     * @param key                key to use when inserting the artifact's associated path into the given map
     * @param artifact           artifact to add into the map
     * @param sharedDependenciesPosition The map receiving the path associated with this artifact
     * @return Returns the version of shared-dependencies if found. Returns the empty string otherwise
     */
    private static String sharedDependencyVersion(String key, Artifact artifact, Map<String, String> sharedDependenciesPosition) {
        String groupPath = artifact.getGroupId().replace('.', '/');
        String pomPath = getPomFileURL(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
        String parentPath = DashboardMain.basePath + "/" + groupPath
                + "/" + artifact.getArtifactId() + "-parent"
                + "/" + artifact.getVersion()
                + "/" + artifact.getArtifactId() + "-parent-" + artifact.getVersion() + ".pom";
        String depsBomPath = DashboardMain.basePath + "/" + groupPath
                + "/" + artifact.getArtifactId() + "-deps-bom"
                + "/" + artifact.getVersion()
                + "/" + artifact.getArtifactId() + "-deps-bom-" + artifact.getVersion() + ".pom";
        String version = getSharedDepsVersionFromURL(parentPath);
        if (version != null) {
            sharedDependenciesPosition.put(key, parentPath);
            return version;
        }
        version = getSharedDepsVersionFromURL(pomPath);
        if (version != null) {
            sharedDependenciesPosition.put(key, pomPath);
            return version;
        }
        version = getSharedDepsVersionFromURL(depsBomPath);
        if (version != null) {
            sharedDependenciesPosition.put(key, depsBomPath);
            return version;
        }
        sharedDependenciesPosition.put(key, "");
        return "";
    }

    private static String getSharedDepsVersionFromURL(String pomURL) {
        if (pomToDepsVersion.containsKey(pomURL)) {
            return pomToDepsVersion.get(pomURL);
        }
        File file = new File("pomFile.xml");
        try {
            URL url = new URL(pomURL);
            FileUtils.copyURLToFile(url, file);
            MavenXpp3Reader read = new MavenXpp3Reader();
            Model model = read.read(new FileReader(file));
            if (model.getDependencyManagement() == null)
                return null;
            for (org.apache.maven.model.Dependency dep : model.getDependencyManagement().getDependencies()) {
                if ("com.google.cloud".equals(dep.getGroupId()) && "google-cloud-shared-dependencies".equals(dep.getArtifactId())) {
                    pomToDepsVersion.put(pomURL, dep.getVersion());
                    return dep.getVersion();
                }
            }

        } catch (XmlPullParserException | IOException ignored) {
        }
        file.deleteOnExit();
        return null;
    }

    private static String getPomFileURL(String groupId, String artifactId, String version) {
        String groupPath = groupId.replace('.', '/');
        return DashboardMain.basePath + "/" + groupPath
                + "/" + artifactId
                + "/" + version
                + "/" + artifactId + "-" + version + ".pom";
    }

    private static String getMetadataURL(Artifact artifact) {
        String groupPath = artifact.getGroupId().replace('.', '/');
        return DashboardMain.basePath + "/" + groupPath
                + "/" + artifact.getArtifactId()
                + "/maven-metadata.xml";
    }
}