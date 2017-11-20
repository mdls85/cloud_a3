#!/bin/bash


git clone https://github.com/mdls85/cloud_a3.git

sudo apt-get install openjdk-9-jdk

java -jar /var/lib/waagent/Microsoft.OSTCExtensions.CustomScriptForLinux-1.5.2.2/download/0/cloud_a3/Receiver/CloudAssignment3.jar

