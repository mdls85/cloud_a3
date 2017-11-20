az vmss extension set --name "Runner" --version 2.0 --publisher Microsoft.Azure.Extensions --resource-group HighMarks --version 1.0 --vmss-name vmsshighm --settings $(w3m -dump https://raw.githubusercontent.com/mdls85/cloud_a3/master/trial.json)



