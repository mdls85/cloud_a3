#!/bin/bash

sudo apt-get -y update
sudo apt-get -y install build-essential

wget http://download.redis.io/releases/redis-4.0.2.tar.gz
tar xzf redis-4.0.2.tar.gz
cd redis-4.0.2
make

wget https://raw.githubusercontent.com/mdls85/cloud_a3/master/redis.conf
src/redis-server /home/cloud-a3/redis-4.0.2/redis.conf.1
