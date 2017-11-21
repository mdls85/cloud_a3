#!/bin/bash
numRequests=1000000
hatchRate=20
maxSimultaneousClients=100

locust -f load_gen.py --no-web --only-summary --num-request=$numRequests --hatch-rate=$hatchRate --clients=$maxSimultaneousClients
