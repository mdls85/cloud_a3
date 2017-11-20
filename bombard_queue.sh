#!/bin/bash
numRequests=1000
hatchRate=100
maxSimultaneousClients=10

locust -f load_gen.py --no-web --only-summary --num-request=$numRequests --hatch-rate=$hatchRate --clients=$maxSimultaneousClients
