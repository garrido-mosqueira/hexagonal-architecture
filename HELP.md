# 🚀 Getting Started

## 📚 Reference Documentation
For the development of this project the next where used :

* [Hexagonal Architecture - Netflix Tech Blog](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)
* [Official Testcontainers documentation](https://www.testcontainers.org/quickstart/junit_5_quickstart/)
* [Spring Boot with MongoDB](https://www.mongodb.com/compatibility/spring-boot)
* [Spring Boot with Redis](https://spring.io/guides/gs/messaging-redis)
* [Java Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

## 📋 Project Brief
The project's structure tried to follow the Hexagonal Architecture (also knows as: Onion, Clean).

The main idea is to take advantage of Dependency Inversion, where high level modules (domain) doesn't depend on low level modules (persistence, web api, etc).

The modules are:

    * task-domain              : defines the domain models and ports (interfaces)
    * task-application         : implementation of use cases (application services) without framework dependencies
    * task-persistence         : outbound adapter for MongoDB persistence
    * task-api                 : inbound adapter (REST API) and Spring Boot entry point
    * virtual-threads-tasks    : outbound adapter for task execution using Java Virtual Threads without framework dependencies

Cool thing about using this approach with modules is it's easy to exchange an `infrastructure` dependency.
For example, the task execution module can be swapped to use different concurrency approaches by changing the dependency in the parent POM.

## ⚙️ Execution
### ⚡ Quickstart
First check if everything is ok running `mvn clean verify`

This project is using Docker. So it will be necessary to set up a local environment. 

### 🍎 macOS setup with Homebrew and Colima
If you're on macOS and not using Docker Desktop, you can use Colima to provide a local Docker runtime.

1) Install required tools via Homebrew:

```
brew install docker
brew install colima
brew install docker-compose docker-credential-helper
```

2) Start Colima (this creates a local Docker environment):

```
colima start
```

3) Set the following environment variables in your shell or JVM run configuration so Docker/Testcontainers target Colima's socket:

```
export DOCKER_HOST="unix://${HOME}/.colima/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
```

To persist these settings, add the two `export` lines above to your shell profile file (choose the one you use):

- `~/.bashrc` or `~/.bash_profile`
- `~/.zshrc`

4) Check Docker config credential store (macOS):

If you previously used Docker Desktop, your Docker CLI might still be configured to use its credential helper, which can cause `docker login` or Testcontainers auth to fail when running under Colima.

Inspect `~/.docker/config.json` and look for a `credsStore` entry. If you see:

```
{
  "credsStore": "desktop"
}
```

On macOS, replace `desktop` with the default `osxkeychain` or remove the `credsStore` line entirely. For example:

```
{
  "credsStore": "osxkeychain"
}
```

After updating, re-run `docker login` if needed.

The next command will up the Spring Boot Application, MongoDB, Redis, Prometheus and Grafana.

``` docker-compose up ```

A Postman collection (`tasks.postman.json`) is available in the root directory in case you want to test the app with Postman. 

### 💡 Considerations
To run/debug the project from your IDE it will be necessary to run MongoDB and Redis separately.
You can use the following commands to start only the required infrastructure:

```bash
docker-compose up -d mongodb-challenge redis-challenge
```

And also change the hosts in application.properties file. By default, they are set with the container names instead of localhost.

```
spring.data.mongodb.host=localhost
spring.data.redis.host=localhost
```

### 🧪 Tests
The project has a suite of integration tests. To achieve that we are using Testcontainers to set up MongoDB and Redis with container reuse enabled for optimized performance.
Docker is required for running the tests. 

### 📈 Monitoring

After running `docker-compose up` to monitor the JVM. Grafana will be working on `localhost:3000`

### 🚢 Deployment

#### 🐙 GitHub Actions: Build and Deploy to GKE

This project includes a GitHub Actions workflow to automatically build and deploy the application to a Google Kubernetes Engine (GKE) cluster.

**📍 Workflow Location:** `.github/workflows/google-cloud-gke-deploy.yml`

**📋 Prerequisites:**

1.  **Google Cloud Project**: A project with GKE and Artifact Registry APIs enabled.
2.  **GKE Cluster**: A running Kubernetes cluster.
3.  **Artifact Registry**: A Docker repository in Artifact Registry.
4.  **Workload Identity Federation**: Recommended for secure authentication from GitHub Actions to Google Cloud.

**🔑 Required GitHub Secrets:**

-   `GKE_PROJECT`: Your Google Cloud Project ID.
-   `WIF_PROVIDER`: Full identifier of the Workload Identity Provider (e.g., `projects/123456789/locations/global/workloadIdentityPools/<POOL_NAME>/providers/<PROVIDER_NAME>`).
-   `WIF_SERVICE_ACCOUNT`: Email of the Google Cloud Service Account (e.g., `my-service-account@my-project.iam.gserviceaccount.com`).

> **Note on Naming:** In this project, `<POOL_NAME>` refers to the identity group created for GitHub, and `<PROVIDER_NAME>` refers to the specific authentication link for this repository.

**⚙️ Configuration:**

Update the following environment variables in `.github/workflows/google-cloud-gke-deploy.yml`:

-   `GAR_LOCATION`: Region of your Artifact Registry (e.g., `us-central1`).
-   `GKE_CLUSTER`: Name of your GKE cluster.
-   `GKE_ZONE`: Zone or region of your GKE cluster (e.g., `us-central1-c`).
-   `REPOSITORY`: Name of your Artifact Registry repository.

## 🛠 GCP Infrastructure Setup (CLI Reference)

Below are the exact commands used to provision and authorize the Google Cloud resources.

### 1. 🆔 Identity & Access (WIF)
```bash
# Link the GitHub repository identity to the Service Account
gcloud iam service-accounts add-iam-policy-binding 389945593863-compute@developer.gserviceaccount.com \
    --project="project-123456789" \
    --role="roles/iam.workloadIdentityUser" \
    --member="principalSet://[iam.googleapis.com/projects/123456789/locations/global/workloadIdentityPools/github-pool/attribute.repository/github-user/repo-name](https://iam.googleapis.com/projects/123456789/locations/global/workloadIdentityPools/github-pool/attribute.repository/github-user/repo-name)"

# Enable the Service Account to sign its own tokens (Required for Docker Login)
gcloud iam service-accounts add-iam-policy-binding 389945593863-compute@developer.gserviceaccount.com \
    --project="project-123456789" \
    --role="roles/iam.serviceAccountTokenCreator" \
    --member="serviceAccount:123456789-compute@developer.gserviceaccount.com"
```
### 2. 📦 Artifact Registry
```bash
# Create the Docker repository
gcloud artifacts repositories create samples \
--repository-format=docker \
--location=us-central1 \
--project=project-123456789

# Grant the Service Account permission to upload images
gcloud artifacts repositories add-iam-policy-binding samples \
--project="project-123456789" \
--location="us-central1" \
--member="serviceAccount:123456789-compute@developer.gserviceaccount.com" \
--role="roles/artifactregistry.writer"
```

### 3. 🛡️ GKE Cluster Permissions
```bash
# Grant GKE nodes permission to pull images from the registry
gcloud projects add-iam-policy-binding project-123456789 \
    --member="serviceAccount:123456789-compute@developer.gserviceaccount.com" \
    --role="roles/artifactregistry.reader"
```

## 🎓 GKE CI/CD Deployment: Lessons Learned

This project features a fully automated CI/CD pipeline that deploys a Java (Spring Boot) Hexagonal Architecture API to Google Kubernetes Engine (GKE) Autopilot. Below is a summary of the technical hurdles overcome and the configuration required for success.

### 🛠 Challenges & Solutions

#### 1. 🔑 Authentication (Workload Identity Federation)
* **The Issue:** Initial attempts failed with `HTTP 404` errors.
* **The Cause:** The GitHub Action incorrectly inferred the project name as "developer" based on the Service Account email suffix (`...-compute@developer.gserviceaccount.com`).
* **The Fix:** Explicitly defined the `project_id` within the `google-github-actions/auth` step to override the automatic inference.

#### 2. 🚫 Docker Registry Permissions (`unauthorized`)
* **The Issue:** Authentication to Google Cloud was successful, but Docker was rejected when trying to push to the Artifact Registry.
* **The Cause:** The Service Account lacked the permission to "sign" tokens for itself and did not have explicit "Writer" access to the repository.
* **The Fix:** Executed the following commands in Google Cloud Shell to grant the necessary permissions:

```bash
# 1. Allow the Service Account to create tokens for itself (Crucial for Docker Login)
gcloud iam service-accounts add-iam-policy-binding 389945593863-compute@developer.gserviceaccount.com \
    --project="project-123456789" \
    --role="roles/iam.serviceAccountTokenCreator" \
    --member="serviceAccount:123456789-compute@developer.gserviceaccount.com"

# 2. Grant explicit Writer access to the Artifact Registry
gcloud artifacts repositories add-iam-policy-binding samples \
    --project="project-123456789" \
    --location="us-central1" \
    --member="serviceAccount:123456789-compute@developer.gserviceaccount.com" \
    --role="roles/artifactregistry.writer"
```
#### 3. ♾️ Infrastructure "Death Loop" (CrashLoopBackOff)
* **The Issue:** The API pod would start but immediately crash/restart, causing the GitHub Action to hang during the `kubectl rollout status` step.
* **The Cause:** The Java application requires MongoDB and Redis to initialize the Spring Boot context. Since these were not running in the GKE cluster, the application context failed, health checks (`/actuator/health`) returned errors, and Kubernetes killed the pod.
* **The Fix:** * Created a `k8s/databases.yaml` manifest to deploy standalone MongoDB and Redis instances as internal cluster services.
    * Updated `k8s/kustomization.yaml` to include `databases.yaml` as a resource, ensuring the full stack is applied in a single command.
    * Increased `initialDelaySeconds` for Liveness and Readiness probes in `deployment.yaml` to 45 seconds to account for database startup time.

---

### 🏗️ Final Architecture Steps

To achieve this automated state, the following architecture was established:

#### 1. 🌐 Environment Configuration
The following **GitHub Secrets** must be configured for the pipeline to connect to GCP:
* `GKE_PROJECT`: `project-123456789`
* `WIF_PROVIDER`: `projects/123456789/locations/global/workloadIdentityPools/github-pool/providers/github-provider`
* `WIF_SERVICE_ACCOUNT`: `123456789-compute@developer.gserviceaccount.com`

#### 2. 🎼 Kubernetes Manifest Orchestration (`/k8s`)
The deployment is managed by **Kustomize**, which bundles the following:
* **`databases.yaml`**: Provisions the internal MongoDB and Redis backend.
* **`deployment.yaml`**: Configures the API container with environment variables pointing to the internal services:
    * `SPRING_DATA_MONGODB_URI`: `mongodb://mongodb-challenge:27017/test`
    * `SPRING_DATA_REDIS_URL`: `redis://redis-challenge:6379`
* **`kustomization.yaml`**: Differentiates between the generic placeholder image and the real Artifact Registry destination, dynamically injecting the current **Commit SHA** as the image tag.

#### 3. 🤖 Automated CI/CD Workflow
Every push to the `main` branch triggers the following automated sequence:
1.  **Build & Test**: Maven packages the Java application and runs integration tests.
2.  **Authenticate**: The pipeline logs into GCP using Workload Identity Federation (keyless).
3.  **Docker Push**: The image is built and pushed to the Artifact Registry using `gcloud auth configure-docker`.
4.  **Deploy**: Kustomize builds the final YAML and applies it via `kubectl apply -f -`.
5.  **Validation**: The workflow monitors the rollout status to ensure all pods are running and healthy.

---

### 🔗 Accessing the API
Find the public entry point using:
```bash
kubectl get service task-api
```

Public URL: ```http://[EXTERNAL-IP]```

Health Check: ```http://[EXTERNAL-IP]/actuator/health```
