### Upgrade Overview
The `Libraries-Bom` and its managed libraries have been upgraded to use **Protobuf v4.33.2**. All Protobuf gen code included in the managed libraries has been regenerated using `protoc v33.2`. Please note that this update necessitates a minimum Protobuf runtime of **v4.33.2**.

---
### ✅ Backward Compatibility
The new protobuf-java v4.33.2 runtime is fully backward-compatible with protoc gen code 3.x. You should not experience any runtime incompatibilities from using older gen code with the new runtime.
If you encounter any compatibility issues after this upgrade, please create an issue in our [GitHub repository](https://github.com/googleapis/sdk-platform-java/tree/main) or contact our support team through the [support console](https://cloud.google.com/support-hub) for assistance.

---
### ⚠️ Advisory: Vulnerability Warnings
After upgrading, you may see a new warning in your logs: `Vulnerable protobuf generated type in use`.
This warning does not mean the Java SDK is vulnerable. It indicates that your application, or one of its other dependencies, is still using gen code < `3.21.7`.
For a detailed explanation and mitigation steps, please refer to Java section in the official Protobuf [v4.33.0 release notes](https://github.com/protocolbuffers/protobuf/releases/tag/v33.0).

---

### Known Incompatibilities & Mitigations
While rare, the following incompatibilities may occur due to the version update:

#### 1. Runtime Version Mismatch
* **Issue:** The minimum required `protobuf-java` runtime version is now **v4.33.2**. Starting with the 4.x runtime, [validations](https://github.com/protocolbuffers/protobuf/blob/1082da2da37a0887d0cfd70abf4a00e8973cd8bf/java/core/src/main/java/com/google/protobuf/RuntimeVersion.java#L55-L73) ensure the runtime version is higher than the gen code version.
* **Symptoms:**
    * `java.lang.NoClassDefFoundError: com/google/protobuf/RuntimeVersion$RuntimeDomain` (when using 3.x versions).
    * `com.google.protobuf.RuntimeVersion$ProtobufRuntimeVersionException` (when using older 4.x versions).
* **Mitigation:** Upgrade `protobuf-java` to **v4.33.2**.

#### 2. Class Hierarchy Changes (`GeneratedMessageV3`)
* **Issue:** `GeneratedMessageV3` is no longer a parent class of gen code.
* **Symptoms:** Runtime errors will occur if attempting to cast Proto 4.x gen code to `GeneratedMessageV3`. For example, `DeleteInstanceRequest` now extends [GeneratedMessage](https://github.com/googleapis/google-cloud-java/blob/d7a49aa9502012df1209b55ec741b1d4ac639361/java-compute/proto-google-cloud-compute-v1/src/main/java/com/google/cloud/compute/v1/DeleteInstanceRequest.java#L33) instead of [GeneratedMessageV3](https://github.com/googleapis/google-cloud-java/blob/1b56d017b4f93b5037f7261c488e008ac59897d8/java-compute/proto-google-cloud-compute-v1/src/main/java/com/google/cloud/compute/v1/DeleteInstanceRequest.java#L31).
* **Mitigation:** Migrate usages of `GeneratedMessageV3` to its parent interface, `Message`.

#### 3. Descriptor Syntax APIs
* **Issue:** Certain internal methods in [Descriptors](https://github.com/protocolbuffers/protobuf/commit/1aeacd4f4eb4e0aa05d6336e2988a565e475e9a0) are no longer available.
* **Mitigation:** There are no direct alternative methods; it is suggested to stop using them.

#### 4. Legacy Generated Code (v2.x)
* **Issue:** The 4.33.x runtime is incompatible with v2.x gen code.
* **Mitigation:** Migrate gen code to at least **v3.0.0**. Please note that 3.x support will [end in March 2027](https://protobuf.dev/support/version-support/#java); it is strongly recommended to upgrade gen code to 4.x.