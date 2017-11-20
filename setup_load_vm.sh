#!/bin/bash

sudo apt -y install python-pip
pip install locustio
git clone https://github.com/mdls85/cloud_a3.git
cd cloud_a3

sh bombard_queue.sh
