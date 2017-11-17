# cloud_a3

Requires:  
python 2  
locustio  
  
Run with  

locust -f load_gen.py --no-web --num-request=x --only-summary --hatch-rate=y --clients=z  
  
where x is the number of requests to perform, y is the rate per second in which clients are spawned and z is the number of concurrent clients.
