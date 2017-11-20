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
#curl -L https://aka.ms/InstallAzureCli | bash  <--Working 

#restart the shell
#exec -l $SHELL   <--Working 


#Ask the user to login to azure
echo "Please login to azure!"
#az login   <--Working 


#Create the resource group where all the things will be
#az group create --name $resourceGroupName --location $location <-- working


#Create the service bus and queue where the items will be stored template is at https://github.com/mdls85/cloud_a3/blob/master/servicebustemplate.json
echo "Creating the service bus and queue to store the items that the generator will store"
az group deployment create --resource-group $resourceGroupName -n $servicebusName --template-file servicebustemplate.json


#Create the VM to do the 1M requests. Have the vm run the python script from the git to populate the queue using it's IP address and default port
echo "Creating the virtual machine to process the 1 million requests"


#Create the VM to host Redis 
echo "Creating a virtual machine to host redis"
 

#Create the Virtual Scale Set to do the auto scaling. Let the vm's run the receiver file from the git to pull from the queue and store in the redis queue
echo "Creating the virtual scale set to process items from the queue"
#az vmss create --image $imageName --name $virtualMachineScaleSetName --resource-group $resourceGroupName


#Create autoscale set rules for the virtual set

