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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import com.google.cloud.cloudbomcommitcheck.ArtifactData;
import com.google.cloud.cloudbomcommitcheck.ClientLibraryStatus;
import com.google.cloud.cloudbomcommitcheck.RecentCommitCheck;
import org.junit.Test;
import com.google.cloud.tools.opensource.dependencies.MavenRepositoryException;
import java.util.Map;
import org.apache.commons.cli.ParseException;

public class RecentCommitCheckTest {

  /**
   * This test should not throw any exceptions, but rather just an exit code of 0.
   */
  @Test
  public void nullCommitTest() throws MavenRepositoryException, ParseException {
    RecentCommitCheck commitCheck = new RecentCommitCheck(null);
    int exitCode = commitCheck.execute();
    assertEquals(0, exitCode);
  }

  /**
   * Exit code should be 0 if commit does not involve any updates.
   */
  @Test
  public void emptyCommitTest() throws MavenRepositoryException, ParseException {
    RecentCommitCheck commitCheck = new RecentCommitCheck("");
    int exitCode = commitCheck.execute();
    assertEquals(0, exitCode);
  }

  /**
   * Exit code should be 0 if commit does not involve any updates.
   */
  @Test
  public void almostDependencyUpdateTest() throws MavenRepositoryException, ParseException {
    RecentCommitCheck commitCheck = new RecentCommitCheck(
        "deps: update dependency com.google.cloud:google-cloud-");
    int exitCode = commitCheck.execute();
    assertEquals(0, exitCode);
  }

  /**
   * We should always pass any kind of SNAPSHOT commit, since nothing is truly being modified.
   */
  @Test
  public void snapshotCommitTest() throws MavenRepositoryException, ParseException {
    RecentCommitCheck commitCheck = new RecentCommitCheck("chore: release-v1.0.0-SNAPSHOT");
    int exitCode = commitCheck.execute();
    assertEquals(0, exitCode);
  }

  /**
   * Runs a sample release check on all items, making sure that all POM files can be found.
   */
  @Test
  public void missingPomTest() throws MavenRepositoryException, ParseException {
    RecentCommitCheck commitCheck = new RecentCommitCheck("chore: release-v1.0.0");
    commitCheck.execute();
    Map<ArtifactData, ClientLibraryStatus> data = commitCheck.getCurrentClientLibraries();
    assertFalse(data.containsValue(ClientLibraryStatus.UNFOUND_POM));
  }

  /**
   * Checking a dependency that is known to exist to make sure we don't get an exception when
   * looking it up (i.e. the code for individual dependencies runs properly)
   */
  @Test
  public void individualDependencyTest() throws MavenRepositoryException, ParseException {
    new RecentCommitCheck(
        "deps: update dependency com.google.cloud:google-cloud-securitycenter to v1.1.0").execute();
  }
}