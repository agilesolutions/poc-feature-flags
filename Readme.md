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

## Create a feature flag
```
az appconfig feature set --name poc-feature-flag --feature Beta
```

## Setup k8s manifests to mount AZ connection string vault into POD

### Step 1: Install the Azure Key Vault Provider for Secrets Store CSI Driver

```
kubectl apply -f https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/main/deployment/secretproviderclass-crd.yaml
```
### Step 2: Create an Azure Key Vault Secret

```
az keyvault secret set --vault-name <your-keyvault-name> --name "my-secret-key" --value "my-secret-value"
```
### Step 3: Create a SecretProviderClass

```
apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: azure-keyvault-secret-provider
spec:
  provider: azure
  parameters:
    usePodIdentity: "false"
    clientID: "<your-client-id>" # Only needed for Service Principal
    keyvaultName: "<your-keyvault-name>"
    objects: |
      array:
        - objectName: "my-secret-key"
          objectType: "secret"
    tenantId: "<your-tenant-id>"
  secretObjects:
  - secretName: my-k8s-secret
    type: Opaque
    data:
    - objectName: "my-secret-key"
      key: "MY_SECRET_VALUE"
```
### Step 4: Deploy the SecretProviderClas
```
kubectl apply -f secret-provider.yaml
```

### Step 5: Mount the Secret in a Pod

```
apiVersion: v1
kind: Pod
metadata:
  name: my-app-pod
spec:
  containers:
  - name: my-container
    image: nginx
    volumeMounts:
    - name: secrets-store
      mountPath: "/mnt/secrets"
      readOnly: true
  volumes:
  - name: secrets-store
    csi:
      driver: secrets-store.csi.k8s.io
      readOnly: true
      volumeAttributes:
        secretProviderClass: "azure-keyvault-secret-provider"

```
### 

```

```

## cleanup

```
az group delete --name aks-cluster-template_rg001
```

## references
- [Azure feature flags with Spring Boot app](https://learn.microsoft.com/en-us/azure/azure-app-configuration/quickstart-feature-flag-spring-boot?tabs=entra-id)
- [Tutorial feature flags in Spring Boot](https://learn.microsoft.com/en-us/azure/azure-app-configuration/use-feature-flags-spring-boot?tabs=spring-boot-3)
