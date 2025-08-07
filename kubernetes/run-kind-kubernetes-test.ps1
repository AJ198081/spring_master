# Main script to start Kind cluster and test Kubernetes functionality
# Author: AJ
# Date: Created as part of the task to test Kind cluster setup

Write-Host "=== KIND KUBERNETES CLUSTER SETUP AND TEST ===" -ForegroundColor Cyan
Write-Host "This script will start a Kind cluster and test Kubernetes functionality" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Ask user if they want to proceed
$confirmation = Read-Host "Do you want to proceed? (y/n)"
if ($confirmation -ne 'y') {
    Write-Host "Operation cancelled by user." -ForegroundColor Yellow
    exit 0
}

# Step 1: Start Kind cluster
Write-Host "`n=== STEP 1: STARTING KIND CLUSTER ===" -ForegroundColor Magenta
& "$PSScriptRoot\start-kind-cluster.ps1"

# Check if Kind cluster setup was successful
if ($LASTEXITCODE -ne 0) {
    Write-Host "Kind cluster setup failed. Exiting..." -ForegroundColor Red
    exit 1
}

# Ask user if they want to proceed with testing
$testConfirmation = Read-Host "`nDo you want to proceed with Kubernetes functionality testing? (y/n)"
if ($testConfirmation -ne 'y') {
    Write-Host "Skipping Kubernetes functionality testing." -ForegroundColor Yellow
    Write-Host "Kind cluster is running. You can manually test it or run test-kubernetes.ps1 later." -ForegroundColor Green
    exit 0
}

# Step 2: Test Kubernetes functionality
Write-Host "`n=== STEP 2: TESTING KUBERNETES FUNCTIONALITY ===" -ForegroundColor Magenta
& "$PSScriptRoot\test-kubernetes.ps1"

# Check if Kubernetes testing was successful
if ($LASTEXITCODE -ne 0) {
    Write-Host "Kubernetes functionality testing failed." -ForegroundColor Red
    exit 1
}

# Ask user if they want to clean up
$cleanupConfirmation = Read-Host "`nDo you want to clean up the test resources and delete the cluster? (y/n)"
if ($cleanupConfirmation -eq 'y') {
    Write-Host "Cleaning up test resources..." -ForegroundColor Yellow
    kubectl delete namespace test-namespace
    
    Write-Host "Deleting Kind cluster..." -ForegroundColor Yellow
    kind delete cluster --name dev-cluster
    
    Write-Host "Cleanup completed successfully!" -ForegroundColor Green
} else {
    Write-Host "Skipping cleanup. The cluster and test resources are still running." -ForegroundColor Yellow
    Write-Host "To clean up later, run:" -ForegroundColor Cyan
    Write-Host "  kubectl delete namespace test-namespace" -ForegroundColor Cyan
    Write-Host "  kind delete cluster --name dev-cluster" -ForegroundColor Cyan
}

Write-Host "`n=== KIND KUBERNETES CLUSTER SETUP AND TEST COMPLETED ===" -ForegroundColor Cyan