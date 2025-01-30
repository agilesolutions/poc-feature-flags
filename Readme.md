# Feature flags with Azure AKS and SpringBoot
incorporate Azure App Configuration into a Spring Boot web app to create an end-to-end implementation of feature management.

# Prerequisites

## create resource group
```
az group create --name aks-cluster-template_rg001 --location centralus
```

## Create template spec and form
```
az ts create --name clusterspec --version 1 --resource-group tks-cluster-template_rg001 --location centralus  --template-file ./arm-templates/azuredeploy.json --ui-form-definition ./ arm-templates/clusterform.json
```


## Portal deploy cluster with custom ARM template
- From portal search for "Deploy a", this will lead you to custom deployment (Deploy from a custom deployment).
- Build your own template...
- upload your custom  template and go for it

## Test cluster
```
az aks get-credentials --resource-group aks-cluster-template_rg001 --name aks101cluster
kubectl get nodes
kubectl describe node
kubectl top node aks-agentpool-14651446-vmss000000
```

## cleanup

```
az group delete --name aks-cluster-template_rg001
```

