<!DOCTYPE html>
<html lang="en-US">
  <#include "macros.ftl">
  <head>
    <meta charset="utf-8" />
    <title>Google Cloud Platform Java Open Source Dependency Dashboard</title>
    <link rel="stylesheet" href="dashboard.css" />
    <script src="dashboard.js"></script>
  </head>
  <body>
    <h1>${coordinates} Dependency Status</h1>
    <hr />
    <#assign totalArtifacts = table?size>

    <div class="dropdown">
      <button class="dropdown-button">Google-Cloud-Bom Version</button>
      <div class="dropdown-content">
        <#list bomVersions as version>
          <a href="../${version?contains('-SNAPSHOT')?then('snapshot', version)}/index.html">${version}</a>
        </#list>
      </div>
    </div>

    <#if coordinates != "all-versions">
        <section class="statistics">
          <div class="container">
            <div class="statistic-item statistic-item-green">
              <h2>${table?size}</h2>
              <span class="desc">Total Artifacts Checked</span>
            </div>

            <div class="statistic-item statistic-item-orange">
              <#assign localUpperBoundsErrorCount = dashboardMain.countFailures(table, "Upper Bounds")>
              <h2>${dashboardMain.countFailures(table, "Upper Bounds")}</h2>
              <span class="desc">${(localUpperBoundsErrorCount == 1)?then("Has", "Have")} Upper Bounds Errors</span>
            </div>

            <div class="statistic-item statistic-item-blue">
              <#assign convergenceErrorCount = dashboardMain.countFailures(table, "Dependency Convergence")>
              <h2>${dashboardMain.countFailures(table, "Dependency Convergence")}</h2>
              <span class="desc">${(convergenceErrorCount == 1)?then("Fails", "Fail")} to Converge</span>
            </div>
          </div>
        </section>
    </#if>

    <#if coordinates != "all-versions">
        <h2>Library Versions</h2>
    </#if>
    <input type="search" id="search" onkeyup="filterFunction()" placeholder="Search column:value, column2:value2...">
    <table id="library versions">
      <tr class="header">
        <th>java-cloud-bom</th>
        <th>artifact</th>
        <th>artifact-version</th>
        <#if coordinates != "all-versions">
            <th>latest released version</th>
            <th>latest released date</th>
        </#if>
        <th>java-shared-dependencies</th>
      </tr>
      <#list artifacts as artifact>
        <#list versions as version>
          <tr>
              <#assign key = artifact + ":" + version>
              <th>${version}</th>
              <th>${artifact}</th>
              <th><a target="_blank" href=${sharedDepsPosition[key]}>${currentVersion[key]}</a></th>
              <#if coordinates != "all-versions">
                  <th><a target="_blank" href=${newestPomURL[key]}>${newestVersion[key]}</a></th>
                  <th><a target="_blank" href=${metadataURL[key]}>${updatedTime[key]}</a></th>
              </#if>
              <th>${sharedDepsVersion[key]}</th>
          </tr>
        </#list>
      </#list>
    </table>

    <hr />

    <p id='updated'>Last generated at ${lastUpdated}</p>

    <script>
      function sortByColumn(rows, col) {
        for(let i = 1; i < rows.length; i++) {
            for(let j = i + 1; j < rows.length; j++) {
                if(rows[j][col].localeCompare(rows[i][col]) < 0) {
                    let temp = rows[i][col];
                    rows[i][col] = rows[j][col];
                    rows[j][col] = temp;
                    break;
                }
            }
        }
      }

      function colsContainAllInput(cols, input) {
        for(let i = 0; i < input.length; i++) {
            if(input[i].length == 0) {
                continue;
            }
            let found = false;
            let checkSpecificColumn = input[i].indexOf(":") > -1;
            let col = checkSpecificColumn ? input[i].split(":")[0] : "";
            let currInput = checkSpecificColumn ? input[i].split(":")[1] : input[i];
            if(checkSpecificColumn) {
                if(col.indexOf("java-cloud-bom") > -1 || col.indexOf("jcb") > -1) {
                    let name = cols[0].textContent || cols[0].innerText;
                    found = name.toLowerCase().indexOf(currInput) > -1;
                } else if (col.indexOf("artifact") > -1) {
                    let name = cols[1].textContent || cols[1].innerText;
                    found = name.toLowerCase().indexOf(currInput) > -1;
                } else if (col.indexOf("artifact-version") > -1) {
                    let name = cols[2].textContent || cols[2].innerText;
                    found = name.toLowerCase().indexOf(currInput) > -1;
                } else if (col.indexOf("java-shared-dependencies") > -1 || col.indexOf("jsd") > -1) {
                    let name = cols[cols.length - 1].textContent || cols[cols.length - 1].innerText;
                    found = name.toLowerCase().indexOf(currInput) > -1
                }
            } else {
                for(let j = 0; j < cols.length; j++) {
                    let name = cols[j].textContent || cols[j].innerText;
                    if(name.toLowerCase().indexOf(currInput) > -1) {
                        found = true;
                    }
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
      }

      function filterFunction() {
        const input = document.getElementById("search").value.toLowerCase();
        if(input === "") {
            for (let i = 1; i < rows.length; i++) {
                rows[i].style.display = "";
            }
            return;
        }
        const splitInput = input.indexOf(",") > -1 ? input.replace(/ /g, '').split(",") : input.split(" ");
        const table = document.getElementById("library versions");
        const rows = table.getElementsByTagName("tr");
        for (let i = 1; i < rows.length; i++) {
          const cols = rows[i].getElementsByTagName("th");
          let isDisplay = colsContainAllInput(cols, splitInput);
          rows[i].style.display = isDisplay ? "" : "none";
        }
        sortByColumn(rows, 2);
      }
    </script>
  </body>
</html>