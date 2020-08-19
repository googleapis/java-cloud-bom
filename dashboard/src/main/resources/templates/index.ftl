<!DOCTYPE html>
<html lang="en-US">
<#include "macros.ftl">
<head>
  <meta charset="utf-8"/>
  <title>Google Cloud Platform Java Open Source Dependency Dashboard</title>
  <link rel="stylesheet" href="dashboard.css"/>
  <script src="dashboard.js"></script>
</head>
<body>
<h1 style="text-align:center">Google-Cloud-BOM ${staticVersion}</h1>
<hr/>
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

<p>Search for artifacts and the versions of associated google-cloud-shared-dependencies each uses,
  within its correspondence of Google-Cloud-BOM.</p>
<p>Search by column using column1:value1 (Example: Search for google-cloud-accesssapproval with version 1.4.0
  by using either 'artifact:approval artifact-version:1.4.0' or 'approval 1.4.0' or 'approval, 1.4.0') </p>
<table id="libraryVersions">
  <input type="text" id="filterBar" onkeyup="filterFunction()" placeholder="Search...">
  <tr class="header">
    <th>google-cloud-bom</th>
    <th>artifact</th>
    <th>artifact-version</th>
      <#if coordinates != "all-versions">
        <th>latest-released-version</th>
        <th>latest-released-date</th>
        <th>google-cloud-shared-dependencies</th>
      <#else>
        <th>google-cloud-shared-dependencies</th>
      </#if>
  </tr>
    <#list artifacts as artifact>
        <#list versions as version>
          <tr>
              <#assign key = artifact + ":" + version>
            <td>${version}</td>
            <td>${artifact}</td>
              <#if sharedDependenciesPosition[key]??>
                <td><a target="_blank" href=${sharedDependenciesPosition[key]}>${currentVersion[key]}</a></td>
              <#else>
                <td><a target="_blank">N/A</a></td>
              </#if>
              <#if coordinates != "all-versions">
                <td><a target="_blank" href=${newestPomUrl[key]}>${newestVersion[key]}</a></td>
                <td><a target="_blank" href=${metadataUrl[key]}>${updatedTime[key]}</a></td>
              </#if>
              <#if sharedDependenciesVersion[key]??>
                <td>${sharedDependenciesVersion[key]}</td>
              <#else>
                <td>N/A</td>
              </#if>
          </tr>
        </#list>
    </#list>
</table>

<hr/>

<p id='updated'>Last generated at ${lastUpdated}</p>

<script>
    const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

    const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
            v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
    )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

    document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
        const table = th.closest('table');
        document.querySelectorAll('th').forEach(otherTh => {
            if (otherTh != th) {
                // Remove all special characters
                otherTh.innerText = otherTh.innerText.replace(/[^\x00-\x7F]/g, "");
            } else {
                if (otherTh.innerText.indexOf('\u25BC') > -1) {
                    otherTh.innerText = otherTh.innerText.split(" ")[0] + " \u25B2";
                } else {
                    otherTh.innerText = otherTh.innerText.split(" ")[0] + " \u25BC";
                }
            }
        });
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
            .forEach(tr => table.appendChild(tr));
    })));

    function colsContainAllInput(cols, input) {
        for (let i = 0; i < input.length; i++) {
            if (input[i].length == 0) {
                continue;
            }
            let found = false;
            let checkSpecificColumn = input[i].indexOf(":") > -1;
            let col = checkSpecificColumn ? input[i].split(":")[0] : "";
            let currInput = checkSpecificColumn ? input[i].split(":")[1] : input[i];
            /* Enough specific conditions that it's simpler to hard-code */
            if (checkSpecificColumn) {
                var name;
                if (col.indexOf("google-cloud-bom") > -1 || col.indexOf("gcb") > -1) {
                    name = cols[0].textContent || cols[0].innerText;
                } else if (col.indexOf("artifact") > -1 || col === "a") {
                    name = cols[1].textContent || cols[1].innerText;
                } else if (col.indexOf("artifact-version") > -1 || col.indexOf("av") > -1) {
                  name = cols[2].textContent || cols[2].innerText;
                } else if (col.indexOf("google-cloud-shared-dependencies") > -1 || col.indexOf("gcsd") > -1) {
                    name = cols[cols.length - 1].textContent || cols[cols.length - 1].innerText;
                }
                // Make sure name has a non-empty value that matches a column name
                found = name && name !== "" && name.toLowerCase().indexOf(currInput) > -1;
            } else {
                check_all_cols:
                    for (let j = 0; j < cols.length; j++) {
                        let name = cols[j].textContent || cols[j].innerText;
                        if (name.toLowerCase().indexOf(currInput) > -1) {
                            found = true;
                            break check_all_cols;
                        }
                    }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    function filterFunction() {
        const input = document.getElementById("filterBar").value.toLowerCase();
        const table = document.getElementById("libraryVersions");
        const rows = table.getElementsByTagName("tr")
        if (input === "") {
            for (let i = 1; i < rows.length; i++) {
                rows[i].style.display = "";
            }
            return;
        }
        const splitInput = input.indexOf(",") > -1 ? input.replace(/ /g, '').split(",") : input.split(" ");
        for (let i = 1; i < rows.length; i++) {
            const cols = rows[i].getElementsByTagName("td");
            let isDisplay = colsContainAllInput(cols, splitInput);
            rows[i].style.display = isDisplay ? "" : "none";
        }
    }
</script>
</body>
</html>