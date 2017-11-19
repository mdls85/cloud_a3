#!/bin/bash

sudo apt -y install python-pip
pip install locustio
git clone https://github.com/mdls85/cloud_a3.git
cd cloud_a3

locust -f load_gen.py --no-web --only-summary --num-request=500 --hatch-rate=100 --clients=10