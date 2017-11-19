import redis

host = '52.168.39.207'
password = 'P#RZKyg%s05jO&Q4vnOr'
conn = redis.StrictRedis(host=host, password=password)

def get_connection():
    return conn
