#!/bin/bash

resourceGroupName="HighMarksTrial"
location="westus2"

#install the azure cli
#curl -L https://aka.ms/InstallAzureCli | bash  <--UNCOMMENT 

#restart the shell
#exec -l $SHELL   <--UNCOMMENT 


#Ask the user to login to azure
echo "Please login to azure!"
#az login   <--UNCOMMENT 

#Create the resource group where all the things will be
#az group create --name $resourceGroupName --location $location   <--UNCOMMENT 


#Create the queue where the items will be stored
echo "Creating the queue so to store the items that the generator will store"


#Create the VM to do the 1M requests
echo "Creating the virtual machine to process the 1 million requests"



#Create the Virtual Scale Set to do the auto scaling
echo "Creating the virtual scale set to process items from the queue"


