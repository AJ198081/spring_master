# Script to start a Kind cluster and verify Kubernetes is running
# Author: AJ
# Date: Created as part of the task to test Kind cluster setup

Write-Host "Starting Kind cluster setup and verification..." -ForegroundColor Green

# Check if Kind is installed
try {
    $kindVersion = kind version
    Write-Host "Kind is installed: $kindVersion" -ForegroundColor Green
} catch {
    Write-Host "Kind is not installed. Please install Kind first: https://kind.sigs.k8s.io/docs/user/quick-start/#installation" -ForegroundColor Red
    exit 1
}

# Check if kubectl is installed
try {
    $kubectlVersion = kubectl version --client
    Write-Host "kubectl is installed: $kubectlVersion" -ForegroundColor Green
} catch {
    Write-Host "kubectl is not installed. Please install kubectl first: https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/" -ForegroundColor Red
    exit 1
}

# Create the Kind cluster using the existing configuration
Write-Host "Creating Kind cluster using configuration from practice_scripts/cluster.yaml..." -ForegroundColor Yellow
kind create cluster --config .\kubernetes\practice_scripts\cluster.yaml

# Verify the cluster was created
Write-Host "Verifying cluster creation..." -ForegroundColor Yellow
$clusters = kind get clusters
if ($clusters -contains "dev-cluster") {
    Write-Host "Cluster 'dev-cluster' created successfully!" -ForegroundColor Green
} else {
    Write-Host "Failed to create cluster 'dev-cluster'" -ForegroundColor Red
    exit 1
}

# Check if the current context is set to the Kind cluster
Write-Host "Checking Kubernetes context..." -ForegroundColor Yellow
$currentContext = kubectl config current-context
if ($currentContext -eq "kind-dev-cluster") {
    Write-Host "Kubernetes context is correctly set to 'kind-dev-cluster'" -ForegroundColor Green
} else {
    Write-Host "Setting Kubernetes context to 'kind-dev-cluster'..." -ForegroundColor Yellow
    kubectl config use-context kind-dev-cluster
}

# Verify that nodes are running
Write-Host "Verifying that Kubernetes nodes are running..." -ForegroundColor Yellow
$nodes = kubectl get nodes
Write-Host "Kubernetes nodes:" -ForegroundColor Green
Write-Host $nodes

# Verify that system pods are running
Write-Host "Verifying that system pods are running..." -ForegroundColor Yellow
$pods = kubectl get pods --all-namespaces
Write-Host "Kubernetes pods:" -ForegroundColor Green
Write-Host $pods

Write-Host "Kind cluster setup and verification completed successfully!" -ForegroundColor Green
Write-Host "To delete the cluster when done, run: kind delete cluster --name dev-cluster" -ForegroundColor Cyan