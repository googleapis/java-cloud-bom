# Google Cloud Libraries BOM

The Google Cloud Libraries BOM is a Bill-of-Materials (BOM) that provides
consistent versions of Google Cloud Java libraries and their core Google
libraries (gRPC, Protobuf, etc.).
Here is a code snippet showing how to use BOM in the `<dependencyManagement>`
section. For details, please refer to 
[Google Cloud: Using Cloud Client Libraries](https://cloud.google.com/java/docs/bom).

To use it in Maven, add the following to your POM:

<!--- {x-version-update-start:libraries-bom:released} -->
 
```xml
 <dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>26.18.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
<!--- {x-version-update-end} -->

[![Maven][maven-version-image]][maven-version-link]

When you use the Libraries BOM, you don't specify individual library versions
and your application runs on a consistent set of the dependencies.

## Libraries in Scope

The content of the Libraries BOM consists of 2 categories:
- Google Cloud Java client libraries (Maven coordinates `com.google.cloud:google-cloud-XXX`, where XXX is a GCP service name) and
- Core Google dependency libraries, such as gRPC, Protobuf, and Guava.

This is the table of modules included in the latest libraries-bom release:

<!-- TABLE_START -->
| Artifact ID | Library Type | Google Cloud Library Reference | Google Cloud Product Reference | 
| --------- | ------------ | ------------ | ------------ |
| api-common | Runtime | v2.13.0 | N/A |
| gax | Runtime | v2.30.0 | N/A |
| google-analytics-admin | Product | [v0.30.0](https://developers.google.com/analytics) | [Analytics Admin](https://cloud.google.com/java/docs/reference/google-analytics-admin/latest/overview) |
| google-analytics-data | Product | [v0.31.0](https://developers.google.com/analytics/trusted-testing/analytics-data) | [Analytics Data](https://cloud.google.com/java/docs/reference/google-analytics-data/latest/overview) |
| google-area120-tables | Product | [v0.24.0](https://area120.google.com/) | [Area 120 Tables](https://cloud.google.com/java/docs/reference/google-area120-tables/latest/overview) |
| google-auth-library | Product | v1.18.0 | [Google Auth Library](https://googleapis.dev/java/google-auth-library/latest/) |
| google-cloud-accessapproval | Product | [v2.21.0](https://cloud.google.com/access-approval/docs/) | [Access Approval](https://cloud.google.com/java/docs/reference/google-cloud-accessapproval/latest/overview) |
| google-cloud-advisorynotifications | Product | [v0.9.0](https://cloud.google.com/advisory-notifications/) | [Advisory Notifications API](https://cloud.google.com/java/docs/reference/google-cloud-advisorynotifications/latest/overview) |
| google-cloud-aiplatform | Product | [v3.21.0](https://cloud.google.com/vertex-ai/docs) | [Vertex AI](https://cloud.google.com/java/docs/reference/google-cloud-aiplatform/latest/overview) |
| google-cloud-alloydb | Product | [v0.9.0](https://cloud.google.com/alloydb/) | [AlloyDB](https://cloud.google.com/java/docs/reference/google-cloud-alloydb/latest/overview) |
| google-cloud-analyticshub | Product | [v0.17.0](https://cloud.google.com/bigquery/TBD) | [Analytics Hub API](https://cloud.google.com/java/docs/reference/google-cloud-analyticshub/latest/overview) |
| google-cloud-api-gateway | Product | [v2.20.0](https://cloud.google.com/api-gateway/docs) | [API Gateway](https://cloud.google.com/java/docs/reference/google-cloud-api-gateway/latest/overview) |
| google-cloud-apigee-connect | Product | [v2.20.0](https://cloud.google.com/apigee/docs/hybrid/v1.3/apigee-connect/) | [Apigee Connect](https://cloud.google.com/java/docs/reference/google-cloud-apigee-connect/latest/overview) |
| google-cloud-apigee-registry | Product | [v0.20.0](https://cloud.google.com/apigee/docs/api-hub/get-started-registry-api) | [Registry API](https://cloud.google.com/java/docs/reference/google-cloud-apigee-registry/latest/overview) |
| google-cloud-apikeys | Product | [v0.18.0](https://cloud.google.com/api-keys/) | [API Keys API](https://cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview) |
| google-cloud-appengine-admin | Product | [v2.20.0](https://cloud.google.com/appengine/docs/admin-api/) | [App Engine Admin API](https://cloud.google.com/java/docs/reference/google-cloud-appengine-admin/latest/overview) |
| google-cloud-artifact-registry | Product | [v1.19.0](https://cloud.google.com/artifact-registry) | [Artifact Registry](https://cloud.google.com/java/docs/reference/google-cloud-artifact-registry/latest/overview) |
| google-cloud-asset | Product | [v3.24.0](https://cloud.google.com/resource-manager/docs/cloud-asset-inventory/overview) | [Cloud Asset Inventory](https://cloud.google.com/java/docs/reference/google-cloud-asset/latest/overview) |
| google-cloud-assured-workloads | Product | [v2.20.0](https://cloud.google.com/assured-workloads/) | [Assured Workloads for Government](https://cloud.google.com/java/docs/reference/google-cloud-assured-workloads/latest/overview) |
| google-cloud-automl | Product | [v2.20.0](https://cloud.google.com/automl/docs/) | [Cloud Auto ML](https://cloud.google.com/java/docs/reference/google-cloud-automl/latest/overview) |
| google-cloud-bare-metal-solution | Product | [v0.20.0](https://cloud.google.com/bare-metal/docs) | [Bare Metal Solution](https://cloud.google.com/java/docs/reference/google-cloud-bare-metal-solution/latest/overview) |
| google-cloud-batch | Product | [v0.20.0](https://cloud.google.com/) | [Cloud Batch](https://cloud.google.com/java/docs/reference/google-cloud-batch/latest/overview) |
| google-cloud-beyondcorp-appconnections | Product | [v0.18.0](https://cloud.google.com/beyondcorp-enterprise/) | [BeyondCorp AppConnections](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appconnections/latest/overview) |
| google-cloud-beyondcorp-appconnectors | Product | [v0.18.0](cloud.google.com/beyondcorp-enterprise/) | [BeyondCorp AppConnectors](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appconnectors/latest/overview) |
| google-cloud-beyondcorp-appgateways | Product | [v0.18.0](https://cloud.google.com/beyondcorp-enterprise/) | [BeyondCorp AppGateways](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appgateways/latest/overview) |
| google-cloud-beyondcorp-clientconnectorservices | Product | [v0.18.0](https://cloud.google.com/beyondcorp-enterprise/) | [BeyondCorp ClientConnectorServices](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-clientconnectorservices/latest/overview) |
| google-cloud-beyondcorp-clientgateways | Product | [v0.18.0](https://cloud.google.com/beyondcorp-enterprise/) | [BeyondCorp ClientGateways](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-clientgateways/latest/overview) |
| google-cloud-biglake | Product | [v0.8.0](https://cloud.google.com/biglake) | [BigLake](https://cloud.google.com/java/docs/reference/google-cloud-biglake/latest/overview) |
| google-cloud-bigquery | Product | [v2.29.0](https://cloud.google.com/bigquery) | [Cloud BigQuery](https://cloud.google.com/java/docs/reference/google-cloud-bigquery/latest/history) |
| google-cloud-bigquery-data-exchange | Product | [v2.15.0](https://cloud.google.com/analytics-hub) | [Analytics Hub](https://cloud.google.com/java/docs/reference/google-cloud-bigquery-data-exchange/latest/overview) |
| google-cloud-bigqueryconnection | Product | [v2.22.0](https://cloud.google.com/bigquery/docs/reference/bigqueryconnection/rest) | [Cloud BigQuery Connection](https://cloud.google.com/bigquery/docs/reference/reservations/rpc/google.cloud.bigquery.reservation.v1beta1) |
| google-cloud-bigquerydatapolicy | Product | [v0.17.0](https://cloud.google.com/bigquery/docs/reference/datapolicy/) | [BigQuery DataPolicy API](https://cloud.google.com/java/docs/reference/google-cloud-bigquerydatapolicy/latest/overview) |
| google-cloud-bigquerydatatransfer | Product | [v2.20.0](https://cloud.google.com/bigquery/transfer/) | [BigQuery Data Transfer Service](https://cloud.google.com/java/docs/reference/google-cloud-bigquerydatatransfer/latest/overview) |
| google-cloud-bigquerymigration | Product | [v0.23.0](https://cloud.google.com/bigquery/docs) | [BigQuery Migration](https://cloud.google.com/java/docs/reference/google-cloud-bigquerymigration/latest/overview) |
| google-cloud-bigqueryreservation | Product | [v2.21.0](https://cloud.google.com/bigquery/docs/reference/reservations/rpc) | [Cloud BigQuery Reservation](https://cloud.google.com/java/docs/reference/google-cloud-bigqueryreservation/latest/overview) |
| google-cloud-bigquerystorage-bom | Product | [v2.39.1](https://cloud.google.com/bigquery/docs/reference/storage/) | [BigQuery Storage](https://cloud.google.com/java/docs/reference/google-cloud-bigquerystorage/latest/history) |
| google-cloud-bigtable-bom | Product | [v2.24.1](https://cloud.google.com/bigtable) | [Cloud Bigtable](https://cloud.google.com/java/docs/reference/google-cloud-bigtable/latest/history) |
| google-cloud-billing | Product | [v2.20.0](https://cloud.google.com/billing/docs) | [Cloud Billing](https://cloud.google.com/java/docs/reference/google-cloud-billing/latest/overview) |
| google-cloud-billingbudgets | Product | [v2.20.0](https://cloud.google.com/billing/docs/how-to/budgets) | [Cloud Billing Budgets](https://cloud.google.com/java/docs/reference/google-cloud-billingbudgets/latest/overview) |
| google-cloud-binary-authorization | Product | [v1.19.0](https://cloud.google.com/binary-authorization/docs) | [Binary Authorization](https://cloud.google.com/java/docs/reference/google-cloud-binary-authorization/latest/overview) |
| google-cloud-build | Product | [v3.22.0](https://cloud.google.com/cloud-build/) | [Cloud Build](https://cloud.google.com/java/docs/reference/google-cloud-build/latest/overview) |
| google-cloud-certificate-manager | Product | [v0.23.0](https://cloud.google.com/certificate-manager/docs) | [Certificate Manager](https://cloud.google.com/java/docs/reference/google-cloud-certificate-manager/latest/overview) |
| google-cloud-channel | Product | [v3.24.0](https://cloud.google.com/channel/docs) | [Channel Services](https://cloud.google.com/java/docs/reference/google-cloud-channel/latest/overview) |
| google-cloud-cloudcommerceconsumerprocurement | Product | [v0.18.0](https://cloud.google.com/marketplace/) | [Cloud Commerce Consumer Procurement](https://cloud.google.com/java/docs/reference/google-cloud-cloudcommerceconsumerprocurement/latest/overview) |
| google-cloud-cloudsupport | Product | [v0.4.0](https://cloud.google.com/support/docs/reference/support-api/) | [Google Cloud Support API](https://cloud.google.com/java/docs/reference/google-cloud-cloudsupport/latest/overview) |
| google-cloud-compute | Product | [v1.30.0](https://cloud.google.com/compute/) | [Compute Engine](https://cloud.google.com/java/docs/reference/google-cloud-compute/latest/overview) |
| google-cloud-confidentialcomputing | Product | [v0.6.0](https://cloud.google.com/confidential-computing/) | [Confidential Computing API](https://cloud.google.com/java/docs/reference/google-cloud-confidentialcomputing/latest/overview) |
| google-cloud-contact-center-insights | Product | [v2.20.0](https://cloud.google.com/dialogflow/priv/docs/insights/) | [CCAI Insights](https://cloud.google.com/java/docs/reference/google-cloud-contact-center-insights/latest/overview) |
| google-cloud-container | Product | [v2.23.0](https://cloud.google.com/kubernetes-engine/) | [Kubernetes Engine](https://cloud.google.com/java/docs/reference/google-cloud-container/latest/overview) |
| google-cloud-containeranalysis | Product | [v2.21.0](https://cloud.google.com/container-registry/docs/container-analysis) | [Cloud Container Analysis](https://cloud.google.com/java/docs/reference/google-cloud-containeranalysis/latest/overview) |
| google-cloud-contentwarehouse | Product | [v0.16.0](https://cloud.google.com/document-warehouse/docs/overview) | [Document AI Warehouse](https://cloud.google.com/java/docs/reference/google-cloud-contentwarehouse/latest/overview) |
| google-cloud-core | Runtime | v2.20.0 | [Google Cloud Core](https://github.com/googleapis/sdk-platform-java/tree/main/java-core) |
| google-cloud-data-fusion | Product | [v1.20.0](https://cloud.google.com/data-fusion/docs) | [Cloud Data Fusion](https://cloud.google.com/java/docs/reference/google-cloud-data-fusion/latest/overview) |
| google-cloud-datacatalog | Product | [v1.26.0](https://cloud.google.com/data-catalog) | [Data Catalog](https://cloud.google.com/java/docs/reference/google-cloud-datacatalog/latest/overview) |
| google-cloud-dataflow | Product | [v0.24.0](https://cloud.google.com/dataflow/docs) | [Dataflow](https://cloud.google.com/java/docs/reference/google-cloud-dataflow/latest/overview) |
| google-cloud-dataform | Product | [v0.19.0](https://cloud.google.com/dataform/docs) | [Cloud Dataform](https://cloud.google.com/java/docs/reference/google-cloud-dataform/latest/overview) |
| google-cloud-datalabeling | Product | [v0.140.0](https://cloud.google.com/ai-platform/data-labeling/docs/) | [Data Labeling](https://cloud.google.com/java/docs/reference/google-cloud-datalabeling/latest/overview) |
| google-cloud-datalineage | Product | [v0.12.0](https://cloud.google.com/data-catalog/docs/data-lineage/) | [Data Lineage](https://cloud.google.com/java/docs/reference/google-cloud-datalineage/latest/overview) |
| google-cloud-dataplex | Product | [v1.18.0](https://cloud.google.com/dataplex) | [Cloud Dataplex](https://cloud.google.com/java/docs/reference/google-cloud-dataplex/latest/overview) |
| google-cloud-dataproc | Product | [v4.17.0](https://cloud.google.com/dataproc) | [Dataproc](https://cloud.google.com/java/docs/reference/google-cloud-dataproc/latest/overview) |
| google-cloud-dataproc-metastore | Product | [v2.21.0](https://cloud.google.com/dataproc-metastore/docs) | [Dataproc Metastore](https://cloud.google.com/java/docs/reference/google-cloud-dataproc-metastore/latest/overview) |
| google-cloud-datastore-bom | Product | [v2.16.0](https://cloud.google.com/datastore) | [Cloud Datastore](https://cloud.google.com/java/docs/reference/google-cloud-datastore/latest/history) |
| google-cloud-datastream | Product | [v1.19.0](https://cloud.google.com/datastream/docs) | [Datastream](https://cloud.google.com/java/docs/reference/google-cloud-datastream/latest/overview) |
| google-cloud-debugger-client | Product | [v1.20.0](https://cloud.google.com/debugger/docs) | [Cloud Debugger](https://cloud.google.com/java/docs/reference/google-cloud-debugger-client/latest/overview) |
| google-cloud-deploy | Product | [v1.18.0](https://cloud.google.com/deploy/docs) | [Google Cloud Deploy](https://cloud.google.com/java/docs/reference/google-cloud-deploy/latest/overview) |
| google-cloud-dialogflow | Product | [v4.26.0](https://cloud.google.com/dialogflow-enterprise/) | [Dialogflow API](https://cloud.google.com/java/docs/reference/google-cloud-dialogflow/latest/overview) |
| google-cloud-dialogflow-cx | Product | [v0.31.0](https://cloud.google.com/dialogflow/cx/docs) | [Dialogflow CX](https://cloud.google.com/java/docs/reference/google-cloud-dialogflow-cx/latest/overview) |
| google-cloud-discoveryengine | Product | [v0.16.0](https://cloud.google.com/discovery-engine/media/docs) | [Discovery Engine API](https://cloud.google.com/java/docs/reference/google-cloud-discoveryengine/latest/overview) |
| google-cloud-distributedcloudedge | Product | [v0.17.0](https://cloud.google.com/distributed-cloud/edge/latest/) | [Google Distributed Cloud Edge](https://cloud.google.com/java/docs/reference/google-cloud-distributedcloudedge/latest/overview) |
| google-cloud-dlp | Product | [v3.24.0](https://cloud.google.com/dlp/docs/) | [Cloud Data Loss Prevention](https://cloud.google.com/java/docs/reference/google-cloud-dlp/latest/overview) |
| google-cloud-dms | Product | [v2.19.0](https://cloud.google.com/database-migration/docs) | [Database Migration Service](https://cloud.google.com/java/docs/reference/google-cloud-dms/latest/overview) |
| google-cloud-dns | Product | [v2.18.0](https://cloud.google.com/dns) | [Cloud DNS](https://cloud.google.com/java/docs/reference/google-cloud-dns/latest/overview) |
| google-cloud-document-ai | Product | [v2.24.0](https://cloud.google.com/compute/docs/documentai/) | [Document AI](https://cloud.google.com/java/docs/reference/google-cloud-document-ai/latest/overview) |
| google-cloud-domains | Product | [v1.17.0](https://cloud.google.com/domains) | [Cloud Domains](https://cloud.google.com/java/docs/reference/google-cloud-domains/latest/overview) |
| google-cloud-enterpriseknowledgegraph | Product | [v0.16.0](https://cloud.google.com/enterprise-knowledge-graph/docs/overview) | [Enterprise Knowledge Graph](https://cloud.google.com/java/docs/reference/google-cloud-enterpriseknowledgegraph/latest/overview) |
| google-cloud-errorreporting | Product | [v0.141.0-beta](https://cloud.google.com/error-reporting) | [Error Reporting](https://cloud.google.com/java/docs/reference/google-cloud-errorreporting/latest/overview) |
| google-cloud-essential-contacts | Product | [v2.20.0](https://cloud.google.com/resource-manager/docs/managing-notification-contacts/) | [Essential Contacts API](https://cloud.google.com/java/docs/reference/google-cloud-essential-contacts/latest/overview) |
| google-cloud-eventarc | Product | [v1.20.0](https://cloud.google.com/eventarc/docs) | [Eventarc](https://cloud.google.com/java/docs/reference/google-cloud-eventarc/latest/overview) |
| google-cloud-eventarc-publishing | Product | [v0.20.0](https://cloud.google.com/eventarc/docs) | [Eventarc Publishing](https://cloud.google.com/java/docs/reference/google-cloud-eventarc-publishing/latest/overview) |
| google-cloud-filestore | Product | [v1.21.0](https://cloud.google.com/filestore/docs) | [Cloud Filestore API](https://cloud.google.com/java/docs/reference/google-cloud-filestore/latest/overview) |
| google-cloud-firestore-bom | Product | [v3.13.2](https://cloud.google.com/firestore) | [Cloud Firestore](https://cloud.google.com/java/docs/reference/google-cloud-firestore/latest/history) |
| google-cloud-functions | Product | [v2.22.0](https://cloud.google.com/functions) | [Cloud Functions](https://cloud.google.com/java/docs/reference/google-cloud-functions/latest/overview) |
| google-cloud-game-servers | Product | [v2.20.0](https://cloud.google.com/docs/games/products/) | [Cloud Gaming](https://cloud.google.com/java/docs/reference/google-cloud-game-servers/latest/overview) |
| google-cloud-gke-backup | Product | [v0.19.0](https://cloud.google.com/kubernetes-engine/docs/add-on/backup-for-gke/concepts/backup-for-gke ) | [Backup for GKE](https://cloud.google.com/java/docs/reference/google-cloud-gke-backup/latest/overview) |
| google-cloud-gke-connect-gateway | Product | [v0.21.0](https://cloud.google.com/anthos/multicluster-management/gateway/) | [Connect Gateway API](https://cloud.google.com/java/docs/reference/google-cloud-gke-connect-gateway/latest/overview) |
| google-cloud-gke-multi-cloud | Product | [v0.19.0](https://cloud.google.com/anthos/clusters/docs/multi-cloud) | [Anthos Multicloud](https://cloud.google.com/java/docs/reference/google-cloud-gke-multi-cloud/latest/overview) |
| google-cloud-gkehub | Product | [v1.20.0](https://cloud.google.com/anthos/gke/docs/) | [GKE Hub API](https://cloud.google.com/java/docs/reference/google-cloud-gkehub/latest/overview) |
| google-cloud-gsuite-addons | Product | [v2.20.0](https://developers.google.com/workspace/add-ons/overview) | [Google Workspace Add-ons API](https://cloud.google.com/java/docs/reference/google-cloud-gsuite-addons/latest/overview) |
| google-cloud-iamcredentials | Product | [v2.20.0](https://cloud.google.com/iam/credentials/reference/rest/) | [IAM Service Account Credentials API](https://cloud.google.com/java/docs/reference/google-cloud-iamcredentials/latest/overview) |
| google-cloud-ids | Product | [v1.19.0](https://cloud.google.com/intrusion-detection-system/docs) | [Intrusion Detection System](https://cloud.google.com/java/docs/reference/google-cloud-ids/latest/overview) |
| google-cloud-iot | Product | [v2.20.0](https://cloud.google.com/iot) | [Cloud Internet of Things (IoT) Core](https://cloud.google.com/java/docs/reference/google-cloud-iot/latest/overview) |
| google-cloud-kms | Product | [v2.23.0](https://cloud.google.com/kms) | [Cloud Key Management Service](https://cloud.google.com/java/docs/reference/google-cloud-kms/latest/overview) |
| google-cloud-kmsinventory | Product | [v0.9.0](https://cloud.google.com/kms/docs/) | [KMS Inventory API](https://cloud.google.com/java/docs/reference/google-cloud-kmsinventory/latest/overview) |
| google-cloud-language | Product | [v2.21.0](https://cloud.google.com/natural-language/docs/) | [Natural Language](https://cloud.google.com/java/docs/reference/google-cloud-language/latest/overview) |
| google-cloud-life-sciences | Product | [v0.22.0](https://cloud.google.com/life-sciences/docs) | [Cloud Life Sciences](https://cloud.google.com/java/docs/reference/google-cloud-life-sciences/latest/overview) |
| google-cloud-live-stream | Product | [v0.22.0](https://cloud.google.com/livestream/) | [Live Stream API](https://cloud.google.com/java/docs/reference/google-cloud-live-stream/latest/overview) |
| google-cloud-logging-bom | Product | [v3.15.5](https://cloud.google.com/logging/docs) | [Cloud Logging](https://cloud.google.com/java/docs/reference/google-cloud-logging/latest/history) |
| google-cloud-logging-logback | Product | [v0.130.17-alpha](https://cloud.google.com/logging/docs/setup/java#logback_appender_for) | [Cloud Logging Logback Appender](https://cloud.google.com/java/docs/reference/google-cloud-logging-logback/latest/history) |
| google-cloud-managed-identities | Product | [v1.18.0](https://cloud.google.com/managed-microsoft-ad/) | [Managed Service for Microsoft Active Directory](https://cloud.google.com/java/docs/reference/google-cloud-managed-identities/latest/overview) |
| google-cloud-mediatranslation | Product | [v0.26.0](https://cloud.google.com/) | [Media Translation API](https://cloud.google.com/java/docs/reference/google-cloud-mediatranslation/latest/overview) |
| google-cloud-memcache | Product | [v2.20.0](https://cloud.google.com/memorystore/) | [Cloud Memcache](https://cloud.google.com/java/docs/reference/google-cloud-memcache/latest/overview) |
| google-cloud-migrationcenter | Product | [v0.2.0](https://cloud.google.com/migration-center/docs/migration-center-overview) | [Migration Center API](https://cloud.google.com/java/docs/reference/google-cloud-migrationcenter/latest/overview) |
| google-cloud-monitoring | Product | [v3.21.0](https://cloud.google.com/monitoring/docs) | [Stackdriver Monitoring](https://cloud.google.com/java/docs/reference/google-cloud-monitoring/latest/overview) |
| google-cloud-monitoring-dashboard | Product | [v2.22.0](https://cloud.google.com/monitoring/charts/dashboards) | [Monitoring Dashboards](https://cloud.google.com/java/docs/reference/google-cloud-monitoring-dashboard/latest/overview) |
| google-cloud-monitoring-metricsscope | Product | [v0.14.0](https://cloud.google.com/monitoring/api/ref_v3/rest/v1/locations.global.metricsScopes) | [Monitoring Metrics Scopes](https://cloud.google.com/java/docs/reference/google-cloud-monitoring-metricsscope/latest/overview) |
| google-cloud-network-management | Product | [v1.21.0](https://cloud.google.com/network-intelligence-center/docs/connectivity-tests/reference/networkmanagement/rest/) | [Network Management API](https://cloud.google.com/java/docs/reference/google-cloud-network-management/latest/overview) |
| google-cloud-network-security | Product | [v0.23.0](https://cloud.google.com/traffic-director/docs/reference/network-security/rest) | [Network Security API](https://cloud.google.com/java/docs/reference/google-cloud-network-security/latest/overview) |
| google-cloud-networkconnectivity | Product | [v1.19.0](https://cloud.google.com/network-connectivity/docs) | [Network Connectivity Center](https://cloud.google.com/java/docs/reference/google-cloud-networkconnectivity/latest/overview) |
| google-cloud-nio | Product | [v0.126.18](https://cloud.google.com/storage/docs) | [NIO Filesystem Provider for Google Cloud Storage](https://cloud.google.com/java/docs/reference/google-cloud-nio/latest/history) |
| google-cloud-notebooks | Product | [v1.18.0](https://cloud.google.com/ai-platform-notebooks) | [AI Platform Notebooks](https://cloud.google.com/java/docs/reference/google-cloud-notebooks/latest/overview) |
| google-cloud-notification | Product | v0.138.0-beta | [Google Cloud Pub/Sub Notifications for GCS](https://cloud.google.com/java/docs/reference/google-cloud-notification/latest/overview) |
| google-cloud-optimization | Product | [v1.18.0](https://cloud.google.com/optimization/docs) | [Cloud Fleet Routing](https://cloud.google.com/java/docs/reference/google-cloud-optimization/latest/overview) |
| google-cloud-orchestration-airflow | Product | [v1.20.0](https://cloud.google.com/composer/docs) | [Cloud Composer](https://cloud.google.com/java/docs/reference/google-cloud-orchestration-airflow/latest/overview) |
| google-cloud-orgpolicy | Product | [v2.20.0](n/a) | [Cloud Organization Policy](https://cloud.google.com/java/docs/reference/proto-google-cloud-orgpolicy-v1/latest/overview) |
| google-cloud-os-config | Product | [v2.22.0](https://cloud.google.com/compute/docs/os-patch-management) | [OS Config API](https://cloud.google.com/java/docs/reference/google-cloud-os-config/latest/overview) |
| google-cloud-os-login | Product | [v2.19.0](https://cloud.google.com/compute/docs/oslogin/) | [Cloud OS Login](https://cloud.google.com/java/docs/reference/google-cloud-os-login/latest/overview) |
| google-cloud-phishingprotection | Product | [v0.51.0](https://cloud.google.com/phishing-protection/docs/) | [Phishing Protection](https://cloud.google.com/java/docs/reference/google-cloud-phishingprotection/latest/overview) |
| google-cloud-policy-troubleshooter | Product | [v1.19.0](https://cloud.google.com/iam/docs/troubleshooting-access) | [IAM Policy Troubleshooter API](https://cloud.google.com/java/docs/reference/google-cloud-policy-troubleshooter/latest/overview) |
| google-cloud-private-catalog | Product | [v0.22.0](https://cloud.google.com/private-catalog/docs) | [Private Catalog](https://cloud.google.com/java/docs/reference/google-cloud-private-catalog/latest/overview) |
| google-cloud-profiler | Product | [v2.20.0](https://cloud.google.com/profiler/docs) | [Cloud Profiler](https://cloud.google.com/java/docs/reference/google-cloud-profiler/latest/overview) |
| google-cloud-publicca | Product | [v0.17.0](https://cloud.google.com/certificate-manager/docs/public-ca/) | [Public Certificate Authority](https://cloud.google.com/java/docs/reference/google-cloud-publicca/latest/overview) |
| google-cloud-pubsub-bom | Product | [v1.123.17](https://cloud.google.com/pubsub/docs/) | [Cloud Pub/Sub](https://cloud.google.com/java/docs/reference/google-cloud-pubsub/latest/history) |
| google-cloud-pubsublite-bom | Product | [v1.12.10](https://cloud.google.com/pubsub/lite/docs) | [Cloud Pub/Sub Lite](https://cloud.google.com/java/docs/reference/google-cloud-pubsublite/latest/history) |
| google-cloud-rapidmigrationassessment | Product | [v0.3.0](https://cloud.google.com/migration-center/docs) | [Rapid Migration Assessment API](https://cloud.google.com/java/docs/reference/google-cloud-rapidmigrationassessment/latest/overview) |
| google-cloud-recaptchaenterprise | Product | [v3.17.0](https://cloud.google.com/recaptcha-enterprise/docs/) | [reCAPTCHA Enterprise](https://cloud.google.com/java/docs/reference/google-cloud-recaptchaenterprise/latest/overview) |
| google-cloud-recommendations-ai | Product | [v0.27.0](https://cloud.google.com/recommendations-ai/) | [Recommendations AI](https://cloud.google.com/java/docs/reference/google-cloud-recommendations-ai/latest/overview) |
| google-cloud-recommender | Product | [v2.22.0](https://cloud.google.com/recommendations/) | [Recommender](https://cloud.google.com/java/docs/reference/google-cloud-recommender/latest/overview) |
| google-cloud-redis | Product | [v2.23.0](https://cloud.google.com/memorystore/docs/redis/) | [Cloud Redis](https://cloud.google.com/java/docs/reference/google-cloud-redis/latest/overview) |
| google-cloud-resource-settings | Product | [v1.20.0](https://cloud.google.com/resource-manager/docs/reference/resource-settings/rest) | [Resource Settings API](https://cloud.google.com/java/docs/reference/google-cloud-resource-settings/latest/overview) |
| google-cloud-resourcemanager | Product | [v1.22.0](https://cloud.google.com/resource-manager) | [Resource Manager API](https://cloud.google.com/java/docs/reference/google-cloud-resourcemanager/latest/overview) |
| google-cloud-retail | Product | [v2.22.0](https://cloud.google.com/solutions/retail) | [Cloud Retail](https://cloud.google.com/java/docs/reference/google-cloud-retail/latest/overview) |
| google-cloud-run | Product | [v0.20.0](https://cloud.google.com/run/docs) | [Cloud Run](https://cloud.google.com/java/docs/reference/google-cloud-run/latest/overview) |
| google-cloud-scheduler | Product | [v2.20.0](https://cloud.google.com/scheduler/docs) | [Google Cloud Scheduler](https://cloud.google.com/java/docs/reference/google-cloud-scheduler/latest/overview) |
| google-cloud-secretmanager | Product | [v2.20.0](https://cloud.google.com/solutions/secrets-management/) | [Secret Management](https://cloud.google.com/java/docs/reference/google-cloud-secretmanager/latest/overview) |
| google-cloud-security-private-ca | Product | [v2.22.0](https://cloud.google.com/certificate-authority-service/docs) | [Certificate Authority Service](https://cloud.google.com/java/docs/reference/google-cloud-security-private-ca/latest/overview) |
| google-cloud-securitycenter | Product | [v2.28.0](https://cloud.google.com/security-command-center) | [Security Command Center](https://cloud.google.com/java/docs/reference/google-cloud-securitycenter/latest/overview) |
| google-cloud-securitycenter-settings | Product | [v0.23.0](https://cloud.google.com/security-command-center/) | [Security Command Center Settings API](https://cloud.google.com/java/docs/reference/google-cloud-securitycenter-settings/latest/overview) |
| google-cloud-service-control | Product | [v1.20.0](https://cloud.google.com/service-infrastructure/docs/overview/) | [Service Control API](https://cloud.google.com/java/docs/reference/google-cloud-service-control/latest/overview) |
| google-cloud-service-management | Product | [v3.18.0](https://cloud.google.com/service-infrastructure/docs/overview/) | [Service Management API](https://cloud.google.com/java/docs/reference/google-cloud-service-management/latest/overview) |
| google-cloud-service-usage | Product | [v2.20.0](https://cloud.google.com/service-usage/docs/overview) | [Service Usage](https://cloud.google.com/java/docs/reference/google-cloud-service-usage/latest/overview) |
| google-cloud-servicedirectory | Product | [v2.21.0](https://cloud.google.com/service-directory/) | [Service Directory](https://cloud.google.com/java/docs/reference/google-cloud-servicedirectory/latest/overview) |
| google-cloud-shell | Product | [v2.19.0](https://cloud.google.com/shell/docs) | [Cloud Shell](https://cloud.google.com/java/docs/reference/google-cloud-shell/latest/overview) |
| google-cloud-spanner-bom | Product | [v6.43.1](https://cloud.google.com/spanner/docs/) | [Cloud Spanner](https://cloud.google.com/java/docs/reference/google-cloud-spanner/latest/history) |
| google-cloud-spanner-jdbc | Product | [v2.11.2](https://cloud.google.com/spanner/docs/use-oss-jdbc) | [Google Cloud Spanner JDBC](https://cloud.google.com/java/docs/reference/google-cloud-spanner-jdbc/latest/history) |
| google-cloud-speech | Product | [v4.15.0](https://cloud.google.com/speech-to-text/docs/) | [Cloud Speech](https://cloud.google.com/java/docs/reference/google-cloud-speech/latest/overview) |
| google-cloud-storage-bom | Product | [v2.22.5](https://cloud.google.com/storage) | [Cloud Storage](https://cloud.google.com/java/docs/reference/google-cloud-storage/latest/history) |
| google-cloud-storage-transfer | Product | [v1.20.0](https://cloud.google.com/storage-transfer-service) | [Storage Transfer Service](https://cloud.google.com/java/docs/reference/google-cloud-storage-transfer/latest/overview) |
| google-cloud-storageinsights | Product | [v0.5.0](https://cloud.google.com/storage/docs/insights/storage-insights/) | [Storage Insights API](https://cloud.google.com/java/docs/reference/google-cloud-storageinsights/latest/overview) |
| google-cloud-talent | Product | [v2.21.0](https://cloud.google.com/solutions/talent-solution/) | [Talent Solution](https://cloud.google.com/java/docs/reference/google-cloud-talent/latest/overview) |
| google-cloud-tasks | Product | [v2.20.0](https://cloud.google.com/tasks/docs/) | [Cloud Tasks](https://cloud.google.com/java/docs/reference/google-cloud-tasks/latest/overview) |
| google-cloud-texttospeech | Product | [v2.21.0](https://cloud.google.com/text-to-speech) | [Cloud Text-to-Speech](https://cloud.google.com/java/docs/reference/google-cloud-texttospeech/latest/overview) |
| google-cloud-tpu | Product | [v2.21.0](https://cloud.google.com/tpu/docs) | [Cloud TPU](https://cloud.google.com/java/docs/reference/google-cloud-tpu/latest/overview) |
| google-cloud-trace | Product | [v2.20.0](https://cloud.google.com/trace/docs/) | [Stackdriver Trace](https://cloud.google.com/java/docs/reference/google-cloud-trace/latest/overview) |
| google-cloud-translate | Product | [v2.20.0](https://cloud.google.com/translate/docs/) | [Cloud Translation](https://cloud.google.com/java/docs/reference/google-cloud-translate/latest/overview) |
| google-cloud-video-intelligence | Product | [v2.19.0](https://cloud.google.com/video-intelligence/docs/) | [Cloud Video Intelligence](https://cloud.google.com/java/docs/reference/google-cloud-video-intelligence/latest/overview) |
| google-cloud-video-stitcher | Product | [v0.20.0](https://cloud.google.com/video-stitcher/) | [Video Stitcher API](https://cloud.google.com/java/docs/reference/google-cloud-video-stitcher/latest/overview) |
| google-cloud-video-transcoder | Product | [v1.19.0](https://cloud.google.com/transcoder/docs) | [Video Transcoder](https://cloud.google.com/java/docs/reference/google-cloud-video-transcoder/latest/overview) |
| google-cloud-vision | Product | [v3.18.0](https://cloud.google.com/vision/docs/) | [Cloud Vision](https://cloud.google.com/java/docs/reference/google-cloud-vision/latest/overview) |
| google-cloud-vmmigration | Product | [v1.20.0](n/a) | [VM Migration](https://cloud.google.com/java/docs/reference/google-cloud-vmmigration/latest/overview) |
| google-cloud-vmwareengine | Product | [v0.14.0](https://cloud.google.com/vmware-engine/) | [Google Cloud VMware Engine](https://cloud.google.com/java/docs/reference/google-cloud-vmwareengine/latest/overview) |
| google-cloud-vpcaccess | Product | [v2.21.0](https://cloud.google.com/vpc/docs/serverless-vpc-access) | [Serverless VPC Access](https://cloud.google.com/java/docs/reference/google-cloud-vpcaccess/latest/overview) |
| google-cloud-webrisk | Product | [v2.19.0](https://cloud.google.com/web-risk/docs/) | [Web Risk](https://cloud.google.com/java/docs/reference/google-cloud-webrisk/latest/overview) |
| google-cloud-websecurityscanner | Product | [v2.20.0](https://cloud.google.com/security-scanner/docs/) | [Cloud Security Scanner](https://cloud.google.com/java/docs/reference/google-cloud-websecurityscanner/latest/overview) |
| google-cloud-workflow-executions | Product | [v2.20.0](https://cloud.google.com/workflows) | [Cloud Workflow Executions](https://cloud.google.com/java/docs/reference/google-cloud-workflow-executions/latest/overview) |
| google-cloud-workflows | Product | [v2.20.0](https://cloud.google.com/workflows) | [Cloud Workflows](https://cloud.google.com/java/docs/reference/google-cloud-workflows/latest/overview) |
| google-cloud-workstations | Product | [v0.8.0](https://cloud.google.com/workstations) | [Cloud Workstations](https://cloud.google.com/java/docs/reference/google-cloud-workstations/latest/overview) |
| google-iam-admin | Product | [v3.15.0](https://cloud.google.com/iam/docs/apis) | [IAM Admin API](https://cloud.google.com/java/docs/reference/google-iam-admin/latest/overview) |
| google-iam-policy | Runtime | [v1.18.0](n/a) | [IAM](https://cloud.google.com/java/docs/reference/proto-google-iam-v1/latest/history) |
| google-identity-accesscontextmanager | Product | [v1.21.0](n/a) | [Identity Access Context Manager](https://cloud.google.com/java/docs/reference/google-identity-accesscontextmanager/latest/overview) |
| google-maps-addressvalidation | Product | [v0.14.0](https://developers.google.com/maps/documentation/address-validation/) | [Address Validation API](https://cloud.google.com/java/docs/reference/google-maps-addressvalidation/latest/overview) |
| google-maps-mapsplatformdatasets | Product | [v0.9.0](https://developers.google.com/maps/documentation) | [Maps Platform Datasets API](https://cloud.google.com/java/docs/reference/google-maps-mapsplatformdatasets/latest/overview) |
| google-maps-routing | Product | [v1.5.0](https://developers.google.com/maps/documentation/routes) | [Routes API](https://cloud.google.com/java/docs/reference/google-maps-routing/latest/overview) |
| grafeas | Product | [v2.21.0](https://grafeas.io) | [Grafeas](https://cloud.google.com/java/docs/reference/grafeas/latest/overview) |
<!-- TABLE_END -->

## Dependency Dashboard

The [google-cloud-bom dashboard](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) provides client library consumers with easy access to dependency information pertaining to each client library that goes into the google-cloud-bom.

The dashboard shows the content of **each version** of the BOM which includes all the versions of the artifacts in it and their underlying [google-cloud-shared-dependencies BOM](https://github.com/googleapis/java-shared-dependencies#google-cloud-shared-dependencies) version.

The dashboard also has an [all versions](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) page where user can easily search on any artifact or version to see which version of the google-cloud-bom it exists in -- this could be helpful in providing client library consumer advice on which version(s) of google-cloud-bom to import to address their needs.

## Contributing

Contributions to this library are always welcome and highly encouraged.

See `google-cloud`'s [CONTRIBUTING] documentation and the [shared documentation](https://github.com/googleapis/google-cloud-common/blob/main/contributing/readme.md#how-to-contribute-to-gcloud) for more information on how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more information.

## License

Apache 2.0 - See [LICENSE] for more information.


[CONTRIBUTING]:https://github.com/googleapis/google-cloud-java/blob/main/CONTRIBUTING.md
[code-of-conduct]:https://github.com/googleapis/google-cloud-java/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[LICENSE]: https://github.com/googleapis/google-cloud-java/blob/main/LICENSE
[TESTING]: https://github.com/googleapis/google-cloud-java/blob/main/TESTING.md
[cloud-platform]: https://cloud.google.com/
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/libraries-bom.svg
[maven-version-link]: https://search.maven.org/artifact/com.google.cloud/libraries-bom

