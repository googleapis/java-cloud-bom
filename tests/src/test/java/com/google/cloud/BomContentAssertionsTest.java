/*
 * Copyright 2019 Google LLC.
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

package com.google.cloud;

import com.google.cloud.tools.opensource.dependencies.Bom;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;

/** Tests for the assertions in BomContentTest. */
public class BomContentAssertionsTest {

  @Test(expected = IOException.class)
  public void testInvalidBomUnreachable() throws Exception {
    Path bomPath =
        Paths.get("src", "test", "resources", "bom-with-typo-artifact.xml").toAbsolutePath();
    BomContentTest.checkBomReachable(bomPath);
  }

  @Test
  public void testAssertDependencyConvergenceWithinCloudJavaLibraries() throws Exception {
    Bom bom = Bom.readBom("com.google.cloud:libraries-bom:26.0.0");
    try {
      BomContentTest.assertDependencyConvergenceWithinCloudJavaLibraries(bom);
      Assert.fail();
    } catch (ComparisonFailure ex) {
      String message = ex.getMessage();
      Assert.assertEquals(
          "Managed dependency com.google.cloud:google-cloud-bigquery:jar:2.13.8 has dependency "
              + "com.google.cloud:google-cloud-bigquerystorage:jar:2.14.2, which should be 2.15.0 "
              + "(the version in the BOM) expected:<2.1[5.0]> but was:<2.1[4.2]>",
          message);
    }
  }

  @Test
  public void testAssertDependencyConvergenceWithinCloudJavaLibraries2() throws Exception {
    Bom bom = Bom.readBom("com.google.cloud:libraries-bom:25.0.0");
    try {
      BomContentTest.assertDependencyConvergenceWithinCloudJavaLibraries(bom);
      Assert.fail();
    } catch (ComparisonFailure ex) {
      String message = ex.getMessage();
      Assert.assertEquals(
          "Managed dependency com.google.cloud:google-cloud-bigquery:jar:2.13.8 has dependency "
              + "com.google.cloud:google-cloud-bigquerystorage:jar:2.14.2, which should be 2.15.0 "
              + "(the version in the BOM) expected:<2.1[5.0]> but was:<2.1[4.2]>",
          message);
    }
  }
}
