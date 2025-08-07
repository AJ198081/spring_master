# Kind Kubernetes Cluster Setup and Test

This directory contains scripts for setting up a Kind (Kubernetes IN Docker) cluster and testing that Kubernetes is running correctly.

## Scripts Overview

### 1. run-kind-kubernetes-test.ps1

The main script that orchestrates the entire process. It:
- Starts a Kind cluster
- Tests Kubernetes functionality
- Provides options for cleanup

### 2. start-kind-cluster.ps1

This script handles the Kind cluster setup:
- Checks if Kind and kubectl are installed
- Creates a Kind cluster using the configuration from practice_scripts/cluster.yaml
- Verifies the cluster was created successfully
- Ensures the Kubernetes context is set correctly
- Verifies that Kubernetes nodes and system pods are running

### 3. test-kubernetes.ps1

This script tests Kubernetes functionality:
- Creates a test namespace
- Deploys a simple nginx application with 2 replicas
- Creates a service for the nginx deployment
- Verifies pods are running correctly
- Tests connectivity to the service

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation) installed
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/) installed

## Usage

### Running the Complete Test

To run the complete setup and test process:

```powershell
.\kubernetes\run-kind-kubernetes-test.ps1
```

This will guide you through the process with prompts for confirmation at each major step.

### Running Individual Scripts

If you prefer to run the scripts individually:

1. Start the Kind cluster:
   ```powershell
   .\kubernetes\start-kind-cluster.ps1
   ```

2. Test Kubernetes functionality:
   ```powershell
   .\kubernetes\test-kubernetes.ps1
   ```

### Cleaning Up

To clean up resources when done:

```powershell
# Delete the test namespace
kubectl delete namespace test-namespace

# Delete the Kind cluster
kind delete cluster --name dev-cluster
```

## Cluster Configuration

The Kind cluster is configured with:
- 1 control-plane node
- 2 worker nodes
- Port mapping from container port 30001 to host port 30001

The configuration is defined in `kubernetes\practice_scripts\cluster.yaml`.

## Troubleshooting

If you encounter issues:

1. Ensure Docker Desktop is running
2. Check that Kind and kubectl are installed correctly
3. If the cluster fails to create, try deleting it first:
   ```powershell
   kind delete cluster --name dev-cluster
   ```
4. Check the Docker Desktop resources (memory, CPU) if container creation fails