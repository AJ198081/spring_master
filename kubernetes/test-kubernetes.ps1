# Script to test Kubernetes functionality after cluster is running
# Author: AJ
# Date: Created as part of the task to test Kind cluster setup

Write-Host "Starting Kubernetes functionality test..." -ForegroundColor Green

# Check if the cluster is running
try {
    $nodes = kubectl get nodes
    Write-Host "Kubernetes cluster is running with the following nodes:" -ForegroundColor Green
    Write-Host $nodes
} catch {
    Write-Host "Kubernetes cluster is not running. Please run start-kind-cluster.ps1 first." -ForegroundColor Red
    exit 1
}

# Create a test namespace
$namespace = "test-namespace"
Write-Host "Creating test namespace '$namespace'..." -ForegroundColor Yellow
kubectl create namespace $namespace

# Deploy a simple nginx deployment
Write-Host "Deploying nginx to test basic functionality..." -ForegroundColor Yellow
$deploymentYaml = @"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-test
  namespace: $namespace
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:latest
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  namespace: $namespace
spec:
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
  type: ClusterIP
"@

$deploymentYaml | Out-File -FilePath "temp-deployment.yaml" -Encoding utf8
kubectl apply -f temp-deployment.yaml
Remove-Item "temp-deployment.yaml"

# Wait for the deployment to be ready
Write-Host "Waiting for deployment to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check if pods are running
Write-Host "Checking if pods are running..." -ForegroundColor Yellow
$pods = kubectl get pods -n $namespace
Write-Host "Pods in $namespace namespace:" -ForegroundColor Green
Write-Host $pods

# Get more details about the pods
$podDetails = kubectl describe pods -n $namespace
Write-Host "Pod details:" -ForegroundColor Green
Write-Host $podDetails

# Check if service is created
Write-Host "Checking if service is created..." -ForegroundColor Yellow
$services = kubectl get services -n $namespace
Write-Host "Services in $namespace namespace:" -ForegroundColor Green
Write-Host $services

# Test connectivity to the service
Write-Host "Testing connectivity to the service..." -ForegroundColor Yellow
$podName = (kubectl get pods -n $namespace -o jsonpath="{.items[0].metadata.name}")
Write-Host "Running test command in pod $podName..." -ForegroundColor Yellow
kubectl exec -n $namespace $podName -- curl -s nginx-service

Write-Host "Kubernetes functionality test completed!" -ForegroundColor Green
Write-Host "To clean up the test resources, run: kubectl delete namespace $namespace" -ForegroundColor Cyan