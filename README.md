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
      <version>26.46.0</version>
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

<!--- {x-version-update-start:libraries-bom:released} -->
### libraries-bom: v<version>26.46.0</version>
<!--- {x-version-update-end} -->

<!-- TABLE_START -->
| Artifact ID | Library Type | Google Cloud Library Reference | Google Cloud Product Reference | 
| --------- | ------------ | ------------ | ------------ |
| api-common | Runtime | [2.35.0](https://cloud.google.com/java/docs/reference/api-common/latest/overview) | N/A |
| gax | Runtime | [2.52.0](https://cloud.google.com/java/docs/reference/gax/latest/overview) | N/A |
| google-analytics-admin | Product | [0.58.0](https://cloud.google.com/java/docs/reference/google-analytics-admin/latest/overview) | [Analytics Admin](https://developers.google.com/analytics) |
| google-analytics-data | Product | [0.59.0](https://cloud.google.com/java/docs/reference/google-analytics-data/latest/overview) | [Analytics Data](https://developers.google.com/analytics/trusted-testing/analytics-data) |
| google-area120-tables | Product | [0.52.0](https://cloud.google.com/java/docs/reference/google-area120-tables/latest/overview) | [Area 120 Tables](https://area120.google.com/) |
| google-auth-library | Product | [1.24.1](https://cloud.google.com/java/docs/reference/google-auth-library/latest/overview) | [Google Auth Library](n/a) |
| google-cloud-accessapproval | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-accessapproval/latest/overview) | [Access Approval](https://cloud.google.com/access-approval/docs/) |
| google-cloud-advisorynotifications | Product | [0.37.0](https://cloud.google.com/java/docs/reference/google-cloud-advisorynotifications/latest/overview) | [Advisory Notifications API](https://cloud.google.com/advisory-notifications/) |
| google-cloud-aiplatform | Product | [3.49.0](https://cloud.google.com/java/docs/reference/google-cloud-aiplatform/latest/overview) | [Vertex AI](https://cloud.google.com/vertex-ai/docs) |
| google-cloud-alloydb | Product | [0.37.0](https://cloud.google.com/java/docs/reference/google-cloud-alloydb/latest/overview) | [AlloyDB](https://cloud.google.com/alloydb/) |
| google-cloud-alloydb-connectors | Product | [0.26.0](https://cloud.google.com/java/docs/reference/google-cloud-alloydb-connectors/latest/overview) | N/A |
| google-cloud-analyticshub | Product | [0.45.0](https://cloud.google.com/java/docs/reference/google-cloud-analyticshub/latest/overview) | [Analytics Hub API](https://cloud.google.com/bigquery/TBD) |
| google-cloud-api-gateway | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-api-gateway/latest/overview) | [API Gateway](https://cloud.google.com/api-gateway/docs) |
| google-cloud-apigee-connect | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-apigee-connect/latest/overview) | [Apigee Connect](https://cloud.google.com/apigee/docs/hybrid/v1.3/apigee-connect/) |
| google-cloud-apigee-registry | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-apigee-registry/latest/overview) | [Registry API](https://cloud.google.com/apigee/docs/api-hub/get-started-registry-api) |
| google-cloud-apihub | Product | [0.1.0](https://cloud.google.com/java/docs/reference/google-cloud-apihub/latest/overview) | N/A |
| google-cloud-apikeys | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview) | [API Keys API](https://cloud.google.com/api-keys/) |
| google-cloud-appengine-admin | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-appengine-admin/latest/overview) | [App Engine Admin API](https://cloud.google.com/appengine/docs/admin-api/) |
| google-cloud-apphub | Product | [0.12.0](https://cloud.google.com/java/docs/reference/google-cloud-apphub/latest/overview) | N/A |
| google-cloud-artifact-registry | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-artifact-registry/latest/overview) | [Artifact Registry](https://cloud.google.com/artifact-registry) |
| google-cloud-asset | Product | [3.52.0](https://cloud.google.com/java/docs/reference/google-cloud-asset/latest/overview) | [Cloud Asset Inventory](https://cloud.google.com/resource-manager/docs/cloud-asset-inventory/overview) |
| google-cloud-assured-workloads | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-assured-workloads/latest/overview) | [Assured Workloads for Government](https://cloud.google.com/assured-workloads/) |
| google-cloud-automl | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-automl/latest/overview) | [Cloud Auto ML](https://cloud.google.com/automl/docs/) |
| google-cloud-backupdr | Product | [0.7.0](https://cloud.google.com/java/docs/reference/google-cloud-backupdr/latest/overview) | N/A |
| google-cloud-bare-metal-solution | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-bare-metal-solution/latest/overview) | [Bare Metal Solution](https://cloud.google.com/bare-metal/docs) |
| google-cloud-batch | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-batch/latest/overview) | [Cloud Batch](https://cloud.google.com/) |
| google-cloud-beyondcorp-appconnections | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appconnections/latest/overview) | [BeyondCorp AppConnections](https://cloud.google.com/beyondcorp-enterprise/) |
| google-cloud-beyondcorp-appconnectors | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appconnectors/latest/overview) | [BeyondCorp AppConnectors](cloud.google.com/beyondcorp-enterprise/) |
| google-cloud-beyondcorp-appgateways | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-appgateways/latest/overview) | [BeyondCorp AppGateways](https://cloud.google.com/beyondcorp-enterprise/) |
| google-cloud-beyondcorp-clientconnectorservices | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-clientconnectorservices/latest/overview) | [BeyondCorp ClientConnectorServices](https://cloud.google.com/beyondcorp-enterprise/) |
| google-cloud-beyondcorp-clientgateways | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-beyondcorp-clientgateways/latest/overview) | [BeyondCorp ClientGateways](https://cloud.google.com/beyondcorp-enterprise/) |
| google-cloud-biglake | Product | [0.36.0](https://cloud.google.com/java/docs/reference/google-cloud-biglake/latest/overview) | [BigLake](https://cloud.google.com/biglake) |
| google-cloud-bigquery | Product | [2.42.1](https://cloud.google.com/java/docs/reference/google-cloud-bigquery/latest/overview) | [Cloud BigQuery](https://cloud.google.com/bigquery) |
| google-cloud-bigquery-data-exchange | Product | [2.43.0](https://cloud.google.com/java/docs/reference/google-cloud-bigquery-data-exchange/latest/overview) | [Analytics Hub](https://cloud.google.com/analytics-hub) |
| google-cloud-bigqueryconnection | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-bigqueryconnection/latest/overview) | [Cloud BigQuery Connection](https://cloud.google.com/bigquery/docs/reference/bigqueryconnection/rest) |
| google-cloud-bigquerydatapolicy | Product | [0.45.0](https://cloud.google.com/java/docs/reference/google-cloud-bigquerydatapolicy/latest/overview) | [BigQuery DataPolicy API](https://cloud.google.com/bigquery/docs/reference/datapolicy/) |
| google-cloud-bigquerydatatransfer | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-bigquerydatatransfer/latest/overview) | [BigQuery Data Transfer Service](https://cloud.google.com/bigquery/transfer/) |
| google-cloud-bigquerymigration | Product | [0.51.0](https://cloud.google.com/java/docs/reference/google-cloud-bigquerymigration/latest/overview) | [BigQuery Migration](https://cloud.google.com/bigquery/docs) |
| google-cloud-bigqueryreservation | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-bigqueryreservation/latest/overview) | [Cloud BigQuery Reservation](https://cloud.google.com/bigquery/docs/reference/reservations/rpc) |
| google-cloud-bigquerystorage | Product | [3.9.0](https://cloud.google.com/java/docs/reference/google-cloud-bigquerystorage/latest/overview) | [BigQuery Storage](https://cloud.google.com/bigquery/docs/reference/storage/) |
| google-cloud-bigtable | Product | [2.43.0](https://cloud.google.com/java/docs/reference/google-cloud-bigtable/latest/overview) | [Cloud Bigtable](https://cloud.google.com/bigtable) |
| google-cloud-billing | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-billing/latest/overview) | [Cloud Billing](https://cloud.google.com/billing/docs) |
| google-cloud-billingbudgets | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-billingbudgets/latest/overview) | [Cloud Billing Budgets](https://cloud.google.com/billing/docs/how-to/budgets) |
| google-cloud-binary-authorization | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-binary-authorization/latest/overview) | [Binary Authorization](https://cloud.google.com/binary-authorization/docs) |
| google-cloud-build | Product | [3.50.0](https://cloud.google.com/java/docs/reference/google-cloud-build/latest/overview) | [Cloud Build](https://cloud.google.com/cloud-build/) |
| google-cloud-certificate-manager | Product | [0.51.0](https://cloud.google.com/java/docs/reference/google-cloud-certificate-manager/latest/overview) | [Certificate Manager](https://cloud.google.com/certificate-manager/docs) |
| google-cloud-channel | Product | [3.52.0](https://cloud.google.com/java/docs/reference/google-cloud-channel/latest/overview) | [Channel Services](https://cloud.google.com/channel/docs) |
| google-cloud-chat | Product | [0.12.0](https://cloud.google.com/java/docs/reference/google-cloud-chat/latest/overview) | N/A |
| google-cloud-cloudcommerceconsumerprocurement | Product | [0.46.0](https://cloud.google.com/java/docs/reference/google-cloud-cloudcommerceconsumerprocurement/latest/overview) | [Cloud Commerce Consumer Procurement](https://cloud.google.com/marketplace/) |
| google-cloud-cloudcontrolspartner | Product | [0.12.0](https://cloud.google.com/java/docs/reference/google-cloud-cloudcontrolspartner/latest/overview) | N/A |
| google-cloud-cloudquotas | Product | [0.16.0](https://cloud.google.com/java/docs/reference/google-cloud-cloudquotas/latest/overview) | N/A |
| google-cloud-cloudsupport | Product | [0.32.0](https://cloud.google.com/java/docs/reference/google-cloud-cloudsupport/latest/overview) | [Google Cloud Support API](https://cloud.google.com/support/docs/reference/support-api/) |
| google-cloud-compute | Product | [1.58.0](https://cloud.google.com/java/docs/reference/google-cloud-compute/latest/overview) | [Compute Engine](https://cloud.google.com/compute/) |
| google-cloud-confidentialcomputing | Product | [0.34.0](https://cloud.google.com/java/docs/reference/google-cloud-confidentialcomputing/latest/overview) | [Confidential Computing API](https://cloud.google.com/confidential-computing/) |
| google-cloud-contact-center-insights | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-contact-center-insights/latest/overview) | [CCAI Insights](https://cloud.google.com/dialogflow/priv/docs/insights/) |
| google-cloud-container | Product | [2.51.0](https://cloud.google.com/java/docs/reference/google-cloud-container/latest/overview) | [Kubernetes Engine](https://cloud.google.com/kubernetes-engine/) |
| google-cloud-containeranalysis | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-containeranalysis/latest/overview) | [Cloud Container Analysis](https://cloud.google.com/container-registry/docs/container-analysis) |
| google-cloud-contentwarehouse | Product | [0.44.0](https://cloud.google.com/java/docs/reference/google-cloud-contentwarehouse/latest/overview) | [Document AI Warehouse](https://cloud.google.com/document-warehouse/docs/overview) |
| google-cloud-core | Runtime | [2.42.0](https://cloud.google.com/java/docs/reference/google-cloud-core/latest/overview) | [Google Cloud Core](n/a) |
| google-cloud-data-fusion | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-data-fusion/latest/overview) | [Cloud Data Fusion](https://cloud.google.com/data-fusion/docs) |
| google-cloud-datacatalog | Product | [1.54.0](https://cloud.google.com/java/docs/reference/google-cloud-datacatalog/latest/overview) | [Data Catalog](https://cloud.google.com/data-catalog) |
| google-cloud-dataflow | Product | [0.52.0](https://cloud.google.com/java/docs/reference/google-cloud-dataflow/latest/overview) | [Dataflow](https://cloud.google.com/dataflow/docs) |
| google-cloud-dataform | Product | [0.47.0](https://cloud.google.com/java/docs/reference/google-cloud-dataform/latest/overview) | [Cloud Dataform](https://cloud.google.com/dataform/docs) |
| google-cloud-datalabeling | Product | [0.168.0](https://cloud.google.com/java/docs/reference/google-cloud-datalabeling/latest/overview) | [Data Labeling](https://cloud.google.com/ai-platform/data-labeling/docs/) |
| google-cloud-datalineage | Product | [0.40.0](https://cloud.google.com/java/docs/reference/google-cloud-datalineage/latest/overview) | [Data Lineage](https://cloud.google.com/data-catalog/docs/data-lineage/) |
| google-cloud-dataplex | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-cloud-dataplex/latest/overview) | [Cloud Dataplex](https://cloud.google.com/dataplex) |
| google-cloud-dataproc | Product | [4.45.0](https://cloud.google.com/java/docs/reference/google-cloud-dataproc/latest/overview) | [Dataproc](https://cloud.google.com/dataproc) |
| google-cloud-dataproc-metastore | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-dataproc-metastore/latest/overview) | [Dataproc Metastore](https://cloud.google.com/dataproc-metastore/docs) |
| google-cloud-datastore | Product | [2.21.2](https://cloud.google.com/java/docs/reference/google-cloud-datastore/latest/overview) | [Cloud Datastore](https://cloud.google.com/datastore) |
| google-cloud-datastream | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-datastream/latest/overview) | [Datastream](https://cloud.google.com/datastream/docs) |
| google-cloud-debugger-client | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-debugger-client/latest/overview) | [Cloud Debugger](https://cloud.google.com/debugger/docs) |
| google-cloud-deploy | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-cloud-deploy/latest/overview) | [Google Cloud Deploy](https://cloud.google.com/deploy/docs) |
| google-cloud-developerconnect | Product | [0.5.0](https://cloud.google.com/java/docs/reference/google-cloud-developerconnect/latest/overview) | N/A |
| google-cloud-dialogflow | Product | [4.54.0](https://cloud.google.com/java/docs/reference/google-cloud-dialogflow/latest/overview) | [Dialogflow API](https://cloud.google.com/dialogflow-enterprise/) |
| google-cloud-dialogflow-cx | Product | [0.59.0](https://cloud.google.com/java/docs/reference/google-cloud-dialogflow-cx/latest/overview) | [Dialogflow CX](https://cloud.google.com/dialogflow/cx/docs) |
| google-cloud-discoveryengine | Product | [0.44.0](https://cloud.google.com/java/docs/reference/google-cloud-discoveryengine/latest/overview) | [Discovery Engine API](https://cloud.google.com/discovery-engine/media/docs) |
| google-cloud-distributedcloudedge | Product | [0.45.0](https://cloud.google.com/java/docs/reference/google-cloud-distributedcloudedge/latest/overview) | [Google Distributed Cloud Edge](https://cloud.google.com/distributed-cloud/edge/latest/) |
| google-cloud-dlp | Product | [3.52.0](https://cloud.google.com/java/docs/reference/google-cloud-dlp/latest/overview) | [Cloud Data Loss Prevention](https://cloud.google.com/dlp/docs/) |
| google-cloud-dms | Product | [2.47.0](https://cloud.google.com/java/docs/reference/google-cloud-dms/latest/overview) | [Database Migration Service](https://cloud.google.com/database-migration/docs) |
| google-cloud-dns | Product | [2.46.0](https://cloud.google.com/java/docs/reference/google-cloud-dns/latest/overview) | [Cloud DNS](https://cloud.google.com/dns) |
| google-cloud-document-ai | Product | [2.52.0](https://cloud.google.com/java/docs/reference/google-cloud-document-ai/latest/overview) | [Document AI](https://cloud.google.com/compute/docs/documentai/) |
| google-cloud-domains | Product | [1.45.0](https://cloud.google.com/java/docs/reference/google-cloud-domains/latest/overview) | [Cloud Domains](https://cloud.google.com/domains) |
| google-cloud-edgenetwork | Product | [0.16.0](https://cloud.google.com/java/docs/reference/google-cloud-edgenetwork/latest/overview) | N/A |
| google-cloud-enterpriseknowledgegraph | Product | [0.44.0](https://cloud.google.com/java/docs/reference/google-cloud-enterpriseknowledgegraph/latest/overview) | [Enterprise Knowledge Graph](https://cloud.google.com/enterprise-knowledge-graph/docs/overview) |
| google-cloud-errorreporting | Product | [0.169.0-beta](https://cloud.google.com/java/docs/reference/google-cloud-errorreporting/latest/overview) | [Error Reporting](https://cloud.google.com/error-reporting) |
| google-cloud-essential-contacts | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-essential-contacts/latest/overview) | [Essential Contacts API](https://cloud.google.com/resource-manager/docs/managing-notification-contacts/) |
| google-cloud-eventarc | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-eventarc/latest/overview) | [Eventarc](https://cloud.google.com/eventarc/docs) |
| google-cloud-eventarc-publishing | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-eventarc-publishing/latest/overview) | [Eventarc Publishing](https://cloud.google.com/eventarc/docs) |
| google-cloud-filestore | Product | [1.49.0](https://cloud.google.com/java/docs/reference/google-cloud-filestore/latest/overview) | [Cloud Filestore API](https://cloud.google.com/filestore/docs) |
| google-cloud-firestore | Product | [3.25.1](https://cloud.google.com/java/docs/reference/google-cloud-firestore/latest/overview) | [Cloud Firestore](https://cloud.google.com/firestore) |
| google-cloud-functions | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-functions/latest/overview) | [Cloud Functions](https://cloud.google.com/functions) |
| google-cloud-gdchardwaremanagement | Product | [0.3.0](https://cloud.google.com/java/docs/reference/google-cloud-gdchardwaremanagement/latest/overview) | N/A |
| google-cloud-gke-backup | Product | [0.47.0](https://cloud.google.com/java/docs/reference/google-cloud-gke-backup/latest/overview) | [Backup for GKE](https://cloud.google.com/kubernetes-engine/docs/add-on/backup-for-gke/concepts/backup-for-gke) |
| google-cloud-gke-connect-gateway | Product | [0.49.0](https://cloud.google.com/java/docs/reference/google-cloud-gke-connect-gateway/latest/overview) | [Connect Gateway API](https://cloud.google.com/anthos/multicluster-management/gateway/) |
| google-cloud-gke-multi-cloud | Product | [0.47.0](https://cloud.google.com/java/docs/reference/google-cloud-gke-multi-cloud/latest/overview) | [Anthos Multicloud](https://cloud.google.com/anthos/clusters/docs/multi-cloud) |
| google-cloud-gkehub | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-gkehub/latest/overview) | [GKE Hub API](https://cloud.google.com/anthos/gke/docs/) |
| google-cloud-gsuite-addons | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-gsuite-addons/latest/overview) | [Google Workspace Add-ons API](https://developers.google.com/workspace/add-ons/overview) |
| google-cloud-iamcredentials | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-iamcredentials/latest/overview) | [IAM Service Account Credentials API](https://cloud.google.com/iam/credentials/reference/rest/) |
| google-cloud-iap | Product | [0.4.0](https://cloud.google.com/java/docs/reference/google-cloud-iap/latest/overview) | N/A |
| google-cloud-ids | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-ids/latest/overview) | [Intrusion Detection System](https://cloud.google.com/intrusion-detection-system/docs) |
| google-cloud-infra-manager | Product | [0.25.0](https://cloud.google.com/java/docs/reference/google-cloud-infra-manager/latest/overview) | N/A |
| google-cloud-iot | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-iot/latest/overview) | [Cloud Internet of Things (IoT) Core](https://cloud.google.com/iot) |
| google-cloud-kms | Product | [2.51.0](https://cloud.google.com/java/docs/reference/google-cloud-kms/latest/overview) | [Cloud Key Management Service](https://cloud.google.com/kms) |
| google-cloud-kmsinventory | Product | [0.37.0](https://cloud.google.com/java/docs/reference/google-cloud-kmsinventory/latest/overview) | [KMS Inventory API](https://cloud.google.com/kms/docs/) |
| google-cloud-language | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-language/latest/overview) | [Natural Language](https://cloud.google.com/natural-language/docs/) |
| google-cloud-life-sciences | Product | [0.50.0](https://cloud.google.com/java/docs/reference/google-cloud-life-sciences/latest/overview) | [Cloud Life Sciences](https://cloud.google.com/life-sciences/docs) |
| google-cloud-live-stream | Product | [0.50.0](https://cloud.google.com/java/docs/reference/google-cloud-live-stream/latest/overview) | [Live Stream API](https://cloud.google.com/livestream/) |
| google-cloud-logging | Product | [3.20.1](https://cloud.google.com/java/docs/reference/google-cloud-logging/latest/overview) | [Cloud Logging](https://cloud.google.com/logging/docs) |
| google-cloud-logging-logback | Product | [0.131.11-alpha](https://cloud.google.com/java/docs/reference/google-cloud-logging-logback/latest/overview) | [Cloud Logging Logback Appender](https://cloud.google.com/logging/docs/setup/java#logback_appender_for) |
| google-cloud-managed-identities | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-cloud-managed-identities/latest/overview) | [Managed Service for Microsoft Active Directory](https://cloud.google.com/managed-microsoft-ad/) |
| google-cloud-managedkafka | Product | [0.4.0](https://cloud.google.com/java/docs/reference/google-cloud-managedkafka/latest/overview) | N/A |
| google-cloud-mediatranslation | Product | [0.54.0](https://cloud.google.com/java/docs/reference/google-cloud-mediatranslation/latest/overview) | [Media Translation API](https://cloud.google.com/) |
| google-cloud-meet | Product | [0.15.0](https://cloud.google.com/java/docs/reference/google-cloud-meet/latest/overview) | N/A |
| google-cloud-memcache | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-memcache/latest/overview) | [Cloud Memcache](https://cloud.google.com/memorystore/) |
| google-cloud-migrationcenter | Product | [0.30.0](https://cloud.google.com/java/docs/reference/google-cloud-migrationcenter/latest/overview) | [Migration Center API](https://cloud.google.com/migration-center/docs/migration-center-overview) |
| google-cloud-monitoring | Product | [3.49.0](https://cloud.google.com/java/docs/reference/google-cloud-monitoring/latest/overview) | [Stackdriver Monitoring](https://cloud.google.com/monitoring/docs) |
| google-cloud-monitoring-dashboard | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-monitoring-dashboard/latest/overview) | [Monitoring Dashboards](https://cloud.google.com/monitoring/charts/dashboards) |
| google-cloud-monitoring-metricsscope | Product | [0.42.0](https://cloud.google.com/java/docs/reference/google-cloud-monitoring-metricsscope/latest/overview) | [Monitoring Metrics Scopes](https://cloud.google.com/monitoring/api/ref_v3/rest/v1/locations.global.metricsScopes) |
| google-cloud-netapp | Product | [0.27.0](https://cloud.google.com/java/docs/reference/google-cloud-netapp/latest/overview) | N/A |
| google-cloud-network-management | Product | [1.49.0](https://cloud.google.com/java/docs/reference/google-cloud-network-management/latest/overview) | [Network Management API](https://cloud.google.com/network-intelligence-center/docs/connectivity-tests/reference/networkmanagement/rest/) |
| google-cloud-network-security | Product | [0.51.0](https://cloud.google.com/java/docs/reference/google-cloud-network-security/latest/overview) | [Network Security API](https://cloud.google.com/traffic-director/docs/reference/network-security/rest) |
| google-cloud-networkconnectivity | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-networkconnectivity/latest/overview) | [Network Connectivity Center](https://cloud.google.com/network-connectivity/docs) |
| google-cloud-networkservices | Product | [0.4.0](https://cloud.google.com/java/docs/reference/google-cloud-networkservices/latest/overview) | N/A |
| google-cloud-nio | Product | [0.127.22](https://cloud.google.com/java/docs/reference/google-cloud-nio/latest/overview) | [NIO Filesystem Provider for Google Cloud Storage](https://cloud.google.com/storage/docs) |
| google-cloud-notebooks | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-cloud-notebooks/latest/overview) | [AI Platform Notebooks](https://cloud.google.com/ai-platform-notebooks) |
| google-cloud-notification | Product | [0.166.0-beta](https://cloud.google.com/java/docs/reference/google-cloud-notification/latest/overview) | [Google Cloud Pub/Sub Notifications for GCS](n/a) |
| google-cloud-optimization | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-cloud-optimization/latest/overview) | [Cloud Fleet Routing](https://cloud.google.com/optimization/docs) |
| google-cloud-orchestration-airflow | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-orchestration-airflow/latest/overview) | [Cloud Composer](https://cloud.google.com/composer/docs) |
| google-cloud-orgpolicy | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-orgpolicy/latest/overview) | [Cloud Organization Policy](n/a) |
| google-cloud-os-config | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-os-config/latest/overview) | [OS Config API](https://cloud.google.com/compute/docs/os-patch-management) |
| google-cloud-os-login | Product | [2.47.0](https://cloud.google.com/java/docs/reference/google-cloud-os-login/latest/overview) | [Cloud OS Login](https://cloud.google.com/compute/docs/oslogin/) |
| google-cloud-parallelstore | Product | [0.11.0](https://cloud.google.com/java/docs/reference/google-cloud-parallelstore/latest/overview) | N/A |
| google-cloud-phishingprotection | Product | [0.79.0](https://cloud.google.com/java/docs/reference/google-cloud-phishingprotection/latest/overview) | [Phishing Protection](https://cloud.google.com/phishing-protection/docs/) |
| google-cloud-policy-troubleshooter | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-policy-troubleshooter/latest/overview) | [IAM Policy Troubleshooter API](https://cloud.google.com/iam/docs/troubleshooting-access) |
| google-cloud-policysimulator | Product | [0.27.0](https://cloud.google.com/java/docs/reference/google-cloud-policysimulator/latest/overview) | N/A |
| google-cloud-private-catalog | Product | [0.50.0](https://cloud.google.com/java/docs/reference/google-cloud-private-catalog/latest/overview) | [Private Catalog](https://cloud.google.com/private-catalog/docs) |
| google-cloud-privilegedaccessmanager | Product | [0.2.0](https://cloud.google.com/java/docs/reference/google-cloud-privilegedaccessmanager/latest/overview) | N/A |
| google-cloud-profiler | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-profiler/latest/overview) | [Cloud Profiler](https://cloud.google.com/profiler/docs) |
| google-cloud-publicca | Product | [0.45.0](https://cloud.google.com/java/docs/reference/google-cloud-publicca/latest/overview) | [Public Certificate Authority](https://cloud.google.com/certificate-manager/docs/public-ca/) |
| google-cloud-pubsub | Product | [1.132.1](https://cloud.google.com/java/docs/reference/google-cloud-pubsub/latest/overview) | [Cloud Pub/Sub](https://cloud.google.com/pubsub/docs/) |
| google-cloud-pubsublite | Product | [1.14.1](https://cloud.google.com/java/docs/reference/google-cloud-pubsublite/latest/overview) | [Cloud Pub/Sub Lite](https://cloud.google.com/pubsub/lite/docs) |
| google-cloud-rapidmigrationassessment | Product | [0.31.0](https://cloud.google.com/java/docs/reference/google-cloud-rapidmigrationassessment/latest/overview) | [Rapid Migration Assessment API](https://cloud.google.com/migration-center/docs) |
| google-cloud-recaptchaenterprise | Product | [3.45.0](https://cloud.google.com/java/docs/reference/google-cloud-recaptchaenterprise/latest/overview) | [reCAPTCHA Enterprise](https://cloud.google.com/recaptcha-enterprise/docs/) |
| google-cloud-recommendations-ai | Product | [0.55.0](https://cloud.google.com/java/docs/reference/google-cloud-recommendations-ai/latest/overview) | [Recommendations AI](https://cloud.google.com/recommendations-ai/) |
| google-cloud-recommender | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-recommender/latest/overview) | [Recommender](https://cloud.google.com/recommendations/) |
| google-cloud-redis | Product | [2.51.0](https://cloud.google.com/java/docs/reference/google-cloud-redis/latest/overview) | [Cloud Redis](https://cloud.google.com/memorystore/docs/redis/) |
| google-cloud-redis-cluster | Product | [0.20.0](https://cloud.google.com/java/docs/reference/google-cloud-redis-cluster/latest/overview) | N/A |
| google-cloud-resource-settings | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-resource-settings/latest/overview) | [Resource Settings API](https://cloud.google.com/resource-manager/docs/reference/resource-settings/rest) |
| google-cloud-resourcemanager | Product | [1.50.0](https://cloud.google.com/java/docs/reference/google-cloud-resourcemanager/latest/overview) | [Resource Manager API](https://cloud.google.com/resource-manager) |
| google-cloud-retail | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-retail/latest/overview) | [Cloud Retail](https://cloud.google.com/solutions/retail) |
| google-cloud-run | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-run/latest/overview) | [Cloud Run](https://cloud.google.com/run/docs) |
| google-cloud-scheduler | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-scheduler/latest/overview) | [Google Cloud Scheduler](https://cloud.google.com/scheduler/docs) |
| google-cloud-secretmanager | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-secretmanager/latest/overview) | [Secret Management](https://cloud.google.com/solutions/secrets-management/) |
| google-cloud-securesourcemanager | Product | [0.18.0](https://cloud.google.com/java/docs/reference/google-cloud-securesourcemanager/latest/overview) | N/A |
| google-cloud-security-private-ca | Product | [2.50.0](https://cloud.google.com/java/docs/reference/google-cloud-security-private-ca/latest/overview) | [Certificate Authority Service](https://cloud.google.com/certificate-authority-service/docs) |
| google-cloud-securitycenter | Product | [2.56.0](https://cloud.google.com/java/docs/reference/google-cloud-securitycenter/latest/overview) | [Security Command Center](https://cloud.google.com/security-command-center) |
| google-cloud-securitycenter-settings | Product | [0.51.0](https://cloud.google.com/java/docs/reference/google-cloud-securitycenter-settings/latest/overview) | [Security Command Center Settings API](https://cloud.google.com/security-command-center/) |
| google-cloud-securitycentermanagement | Product | [0.16.0](https://cloud.google.com/java/docs/reference/google-cloud-securitycentermanagement/latest/overview) | N/A |
| google-cloud-securityposture | Product | [0.13.0](https://cloud.google.com/java/docs/reference/google-cloud-securityposture/latest/overview) | N/A |
| google-cloud-service-control | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-service-control/latest/overview) | [Service Control API](https://cloud.google.com/service-infrastructure/docs/overview/) |
| google-cloud-service-management | Product | [3.46.0](https://cloud.google.com/java/docs/reference/google-cloud-service-management/latest/overview) | [Service Management API](https://cloud.google.com/service-infrastructure/docs/overview/) |
| google-cloud-service-usage | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-service-usage/latest/overview) | [Service Usage](https://cloud.google.com/service-usage/docs/overview) |
| google-cloud-servicedirectory | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-servicedirectory/latest/overview) | [Service Directory](https://cloud.google.com/service-directory/) |
| google-cloud-servicehealth | Product | [0.15.0](https://cloud.google.com/java/docs/reference/google-cloud-servicehealth/latest/overview) | N/A |
| google-cloud-shell | Product | [2.47.0](https://cloud.google.com/java/docs/reference/google-cloud-shell/latest/overview) | [Cloud Shell](https://cloud.google.com/shell/docs) |
| google-cloud-spanner | Product | [6.74.0](https://cloud.google.com/java/docs/reference/google-cloud-spanner/latest/overview) | [Cloud Spanner](https://cloud.google.com/spanner/docs/) |
| google-cloud-spanner-jdbc | Product | [2.21.0](https://cloud.google.com/java/docs/reference/google-cloud-spanner-jdbc/latest/overview) | [Google Cloud Spanner JDBC](https://cloud.google.com/spanner/docs/use-oss-jdbc) |
| google-cloud-speech | Product | [4.43.0](https://cloud.google.com/java/docs/reference/google-cloud-speech/latest/overview) | [Cloud Speech](https://cloud.google.com/speech-to-text/docs/) |
| google-cloud-storage | Product | [2.42.0](https://cloud.google.com/java/docs/reference/google-cloud-storage/latest/overview) | [Cloud Storage](https://cloud.google.com/storage) |
| google-cloud-storage-control | Product | [2.42.0](https://cloud.google.com/java/docs/reference/google-cloud-storage-control/latest/overview) | N/A |
| google-cloud-storage-transfer | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-storage-transfer/latest/overview) | [Storage Transfer Service](https://cloud.google.com/storage-transfer-service) |
| google-cloud-storageinsights | Product | [0.33.0](https://cloud.google.com/java/docs/reference/google-cloud-storageinsights/latest/overview) | [Storage Insights API](https://cloud.google.com/storage/docs/insights/storage-insights/) |
| google-cloud-talent | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-talent/latest/overview) | [Talent Solution](https://cloud.google.com/solutions/talent-solution/) |
| google-cloud-tasks | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-tasks/latest/overview) | [Cloud Tasks](https://cloud.google.com/tasks/docs/) |
| google-cloud-telcoautomation | Product | [0.18.0](https://cloud.google.com/java/docs/reference/google-cloud-telcoautomation/latest/overview) | N/A |
| google-cloud-texttospeech | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-texttospeech/latest/overview) | [Cloud Text-to-Speech](https://cloud.google.com/text-to-speech) |
| google-cloud-tpu | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-tpu/latest/overview) | [Cloud TPU](https://cloud.google.com/tpu/docs) |
| google-cloud-trace | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-trace/latest/overview) | [Stackdriver Trace](https://cloud.google.com/trace/docs/) |
| google-cloud-translate | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-translate/latest/overview) | [Cloud Translation](https://cloud.google.com/translate/docs/) |
| google-cloud-vertexai | Product | [1.8.0](https://cloud.google.com/java/docs/reference/google-cloud-vertexai/latest/overview) | N/A |
| google-cloud-video-intelligence | Product | [2.47.0](https://cloud.google.com/java/docs/reference/google-cloud-video-intelligence/latest/overview) | [Cloud Video Intelligence](https://cloud.google.com/video-intelligence/docs/) |
| google-cloud-video-stitcher | Product | [0.48.0](https://cloud.google.com/java/docs/reference/google-cloud-video-stitcher/latest/overview) | [Video Stitcher API](https://cloud.google.com/video-stitcher/) |
| google-cloud-video-transcoder | Product | [1.47.0](https://cloud.google.com/java/docs/reference/google-cloud-video-transcoder/latest/overview) | [Video Transcoder](https://cloud.google.com/transcoder/docs) |
| google-cloud-vision | Product | [3.46.0](https://cloud.google.com/java/docs/reference/google-cloud-vision/latest/overview) | [Cloud Vision](https://cloud.google.com/vision/docs/) |
| google-cloud-visionai | Product | [0.5.0](https://cloud.google.com/java/docs/reference/google-cloud-visionai/latest/overview) | N/A |
| google-cloud-vmmigration | Product | [1.48.0](https://cloud.google.com/java/docs/reference/google-cloud-vmmigration/latest/overview) | [VM Migration](n/a) |
| google-cloud-vmwareengine | Product | [0.42.0](https://cloud.google.com/java/docs/reference/google-cloud-vmwareengine/latest/overview) | [Google Cloud VMware Engine](https://cloud.google.com/vmware-engine/) |
| google-cloud-vpcaccess | Product | [2.49.0](https://cloud.google.com/java/docs/reference/google-cloud-vpcaccess/latest/overview) | [Serverless VPC Access](https://cloud.google.com/vpc/docs/serverless-vpc-access) |
| google-cloud-webrisk | Product | [2.47.0](https://cloud.google.com/java/docs/reference/google-cloud-webrisk/latest/overview) | [Web Risk](https://cloud.google.com/web-risk/docs/) |
| google-cloud-websecurityscanner | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-websecurityscanner/latest/overview) | [Cloud Security Scanner](https://cloud.google.com/security-scanner/docs/) |
| google-cloud-workflow-executions | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-workflow-executions/latest/overview) | [Cloud Workflow Executions](https://cloud.google.com/workflows) |
| google-cloud-workflows | Product | [2.48.0](https://cloud.google.com/java/docs/reference/google-cloud-workflows/latest/overview) | [Cloud Workflows](https://cloud.google.com/workflows) |
| google-cloud-workspaceevents | Product | [0.12.0](https://cloud.google.com/java/docs/reference/google-cloud-workspaceevents/latest/overview) | N/A |
| google-cloud-workstations | Product | [0.36.0](https://cloud.google.com/java/docs/reference/google-cloud-workstations/latest/overview) | [Cloud Workstations](https://cloud.google.com/workstations) |
| google-http-client | Runtime | [1.44.2](https://cloud.google.com/java/docs/reference/google-http-client/latest/overview) | N/A |
| google-iam-admin | Product | [3.43.0](https://cloud.google.com/java/docs/reference/google-iam-admin/latest/overview) | [IAM Admin API](https://cloud.google.com/iam/docs/apis) |
| google-iam-policy | Product | [1.46.0](https://cloud.google.com/java/docs/reference/google-iam-policy/latest/overview) | [IAM](n/a) |
| google-identity-accesscontextmanager | Product | [1.49.0](https://cloud.google.com/java/docs/reference/google-identity-accesscontextmanager/latest/overview) | [Identity Access Context Manager](n/a) |
| graal-sdk | Product | [24.0.2](https://cloud.google.com/java/docs/reference/graal-sdk/latest/overview) | N/A |
| grafeas | Product | [2.49.0](https://cloud.google.com/java/docs/reference/grafeas/latest/overview) | [Grafeas](https://grafeas.io) |
| nativeimage | Product | [24.0.2](https://cloud.google.com/java/docs/reference/nativeimage/latest/overview) | N/A |
<!-- TABLE_END -->

## Dependency Dashboard

The [google-cloud-bom dashboard](https://storage.googleapis.com/java-cloud-bom-dashboard/com.google.cloud/google-cloud-bom/all-versions/index.html) provides client library consumers with easy access to dependency information pertaining to each client library that goes into the google-cloud-bom.

The dashboard shows the content of **each version** of the BOM which includes all the versions of the artifacts in it and their underlying [google-cloud-shared-dependencies BOM](https://github.com/googleapis/sdk-platform-java/tree/main/java-shared-dependencies) version.

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

