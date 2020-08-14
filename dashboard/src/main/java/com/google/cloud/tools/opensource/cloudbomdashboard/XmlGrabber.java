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

import java.io.File;
import java.io.IOException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

public class XmlGrabber {

  /**
   * Grabs the given value from a metadata file for a given artifact
   *
   * @param xmlFile The metadata file
   * @param value   The value to grab (options are lastUpdated, latest, and release)
   * @return The unformatted value
   */
  public static String grabMetadataValue(File xmlFile, String value) {
    Builder builder = new Builder();

    Document doc;
    try {
      doc = builder.build(xmlFile);
    } catch (ParsingException | IOException ignored) {
      return "";
    }

    Element rootElement = doc.getRootElement();
    if (rootElement == null) {
      return null;
    }
    String namespace = rootElement.getNamespaceURI();
    Element versioning = rootElement.getFirstChildElement("versioning", namespace);
    if (versioning == null) {
      return null;
    }

    Element lastUpdated = versioning.getFirstChildElement(value, namespace);
    if (lastUpdated == null) {
      return null;
    }

    String lastUpdatedValue = lastUpdated.getValue();
    return lastUpdatedValue == null ? null : lastUpdatedValue.trim();
  }
}