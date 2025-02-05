# Azure dynamic App Configuration and Feature flags with Azure AKS and SpringBoot, Kubernetes Liveness and Readiness probes and more...
This POC is about incorporating Azure App Configuration into a Spring Boot web app to create an end-to-end implementation of feature management and dynamic application configuration push and pull models. 
Implementing Liveness and Readiness probes to gracefully shutting down Spring Boot applications. Integrating Azure Storage services (Blob and File) natively on Spring Boot. 


## in this demo...
- Demonstrating implementing feature flags on Spring Boot applications.
- Integrate with App Configuration when you're using it to manage feature flags.
- Implement feature flags to redirect REST API endpoint routes.
- Azure CLI configuring feature flags on Azure App Configuration store.
- ARM templates to booting up AKS cluster to deploying this demo.
- Azure DevOps pipeline to gradle build and package, docker build and push and finally k8s deploy manifests to deploy app and AZ loadbalancer service.
- Implement POD spec Liveness probes to gracefully shutting down Spring Boot by implementing custom health checking indicators.
- Demonstrate Azure dynamic App config push and pull models.
- Provide Spring Boot resources abstraction to accessing Azure Blob and File Storage services.
- ARM Templates to creating AZ Storage Accounts and File Shares.

# Prerequisites

## create resource group
```
az group create --name aks-cluster-template_rg001 --location centralus
```
## Create ARM template spec and form to deploy POC AKS cluster
```
az ts create --name clusterspec --version 1 --resource-group tks-cluster-template_rg001 --location centralus  --template-file ./arm-templates/azuredeploy.json --ui-form-definition ./ arm-templates/clusterform.json
```

## Deploy AKS POC cluster with custom ARM template from AZ Portal
- From portal search for "Deploy a", this will lead you to custom deployment (Deploy from a custom deployment).
- Build your own template...
- upload your custom  template and go for it

## Test cluster
```
az aks get-credentials --resource-group aks-cluster-template_rg001 --name aks101cluster --overwrite-existing
kubectl get nodes
kubectl describe node
kubectl top node aks-agentpool-14651446-vmss000000
```

## Create an Azure App Configuration Store
```
az appconfig create --name poc-app-config001 --resource-group aks-cluster-template_rg001 --location centralus --sku free
```

## Create a feature flag, list, toggle on and off
```
az appconfig feature set --n poc-app-config001 --feature Beta
az appconfig feature list -n poc-app-config001
az appconfig feature enable -n poc-app-config001 --feature Beta
az appconfig feature disable -n poc-app-config001 --feature Beta
```

## Display AZ app config connection string
```
az appconfig credential list --name poc-app-config001 --query "[?name=='Primary'].connectionString" --output tsv
```

## Add Key-Value Pairs to App Configuration to test dynamic push configuration on Spring Boot apps
```
az appconfig kv set -n poc-app-config001 --key sentinel --value "1"
az appconfig kv set -n poc-app-config001 --key /application/config.message --value "hello"
```

## Retrieve a Configuration Value
```
az appconfig kv show --name poc-app-config001 --key connection.string
```

# Setup and verify AZ Storage Account and File Share with ARM template to POC test Spring Cloud shared files resource handling
```
az deployment group create --resource-group tks-cluster-template_rg001 --template-file storage-template.json --parameters storageAccountName=poc-sa-001 shareName=poc-file-share-001 shareQuota=1
az storage account list --resource-group tks-cluster-template_rg001
az storage share list --account-name storageAccountName=poc-sa-001
```
### run ADO pipeline to gradle build, docker build and push and kubectl deploy k8s POD and Service
- Test loadbalancer external IP endpoints
  - http://130.131.168.254/actuator/health
  - http://130.131.168.254/actuator/hello
  - http://130.131.168.254/actuator/newFeature
  - curl http://130.131.168.254/file/file1.txt -d "new message" -H "Content-Type: text/plain"
  - curl -XGET http://130.131.168.254/file/file1.txt
```
kubectl logs -f -l app=feature-flags
kubectl rollout history deployment/feature-flags-deployment
az appconfig feature enable -n poc-app-config001 --feature Beta
az appconfig feature disable -n poc-app-config001 --feature Beta
```

## cleanup

```
az appconfig delete --name poc-app-config --resource-group aks-cluster-template_rg001
az group delete --name aks-cluster-template_rg001
```

## references
- [Azure feature flags with Spring Boot app](https://learn.microsoft.com/en-us/azure/azure-app-configuration/quickstart-feature-flag-spring-boot?tabs=entra-id)
- [Tutorial feature flags in Spring Boot](https://learn.microsoft.com/en-us/azure/azure-app-configuration/use-feature-flags-spring-boot?tabs=spring-boot-3)
- [Java Spring app with Azure App Configuration](https://learn.microsoft.com/en-us/azure/azure-app-configuration/quickstart-java-spring-app)
- [Use dynamic configuration in a Java Spring app](https://learn.microsoft.com/en-us/azure/azure-app-configuration/enable-dynamic-configuration-java-spring-app)
- [Graceful Shutdown of Spring Boot Applications in Kubernetes](https://medium.com/trendyol-tech/graceful-shutdown-of-spring-boot-applications-in-kubernetes-f80e0b3a30b0)
- [Spring Cloud Azure resource handling](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/resource-handling)
- [Reading and Writing Files Stored in Azure Files by Spring Resource Abstraction in Spring Boot Application](https://github.com/Azure-Samples/azure-spring-boot-samples/tree/main/storage/spring-cloud-azure-starter-storage-file-share/storage-file-sample)