#!/bin/bash
numRequests=1000000
hatchRate=1000
maxSimultaneousClients=1000

locust -f load_gen.py --no-web --only-summary --num-request=$numRequests --hatch-rate=$hatchRate --clients=$maxSimultaneousClients
