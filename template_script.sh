#!/bin/bash

resourceGroupName="HighMarksTrial"
location="westus2"
virtualMachineScaleSetName="HighMarksVMSS"
imageName="UbuntuLTS"
autoscaleName="AutoScale"
autoscaleParametersFile="autoscaleparams.txt"
servicebusName="HighMarksServicebus"
serviebusQueueName="HighMarksSBQueue"

#install the azure cli
#curl -L https://aka.ms/InstallAzureCli | bash  <--UNCOMMENT 

#restart the shell
#exec -l $SHELL   <--UNCOMMENT 


#Ask the user to login to azure
echo "Please login to azure!"
#az login   <--UNCOMMENT 


#Create the resource group where all the things will be
#az group create --name $resourceGroupName --location $location 


#Create the service bus and queue where the items will be stored
echo "Creating the service bus and queue to store the items that the generator will store"
az group deployment create --resource-group $resourceGroupName -n $servicebusName --template-uri https://raw.githubusercontent.com/azure/azure-quickstart-templates/master/201-servicebus-create-queue/azuredeploy.json



#Create the VM to do the 1M requests
echo "Creating the virtual machine to process the 1 million requests"



#Create the Virtual Scale Set to do the auto scaling
echo "Creating the virtual scale set to process items from the queue"
#az vmss create --image $imageName --name $virtualMachineScaleSetName --resource-group $resourceGroupName


#Create autoscale set rules for the virtual set

