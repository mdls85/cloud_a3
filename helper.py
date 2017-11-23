import datetime
import hashlib
import hmac
import urllib
import base64
import random
import time

namespace = 'highmarks4e90967c5a1'
queue_name = 'queue'
key_name = 'test'
shared_key = 'Du/fldI/769m+plYGHBXhwxaNrKvh6dtJYvAsqBomyk='

def get_auth_token():
    """
    Returns an authorization token dictionary
    for making calls to Event Hubs REST API.
    """
    uri = build_host_name()
    uri = urllib.quote(uri, safe='')
    sas = shared_key.encode('utf-8')
    expiry = str(int(time.time() + 10000))
    string_to_sign = (uri + '\n' + expiry).encode('utf-8')
    signed_hmac_sha256 = hmac.HMAC(sas, string_to_sign, hashlib.sha256)
    signature = urllib.pathname2url(base64.b64encode(signed_hmac_sha256.digest()))
    return  {"namespace": namespace,
             "queue_name": queue_name,
             "token":'SharedAccessSignature sr={}&sig={}&se={}&skn={}' \
                     .format(uri, signature, expiry, key_name)
            }

def get_random_product_name():
    # returns a random product name
    names = ["prod1", 'prod2', 'prod3', 'prod4', 'prod5', 'prod6', 'prod7', 'prod8', 'prod9', 'prod10']
    return names[random.randint(0, len(names) - 1)]

def get_id(curr_id):
    # returns id
    return curr_id

def get_random_price():
    # returns a randomly generated price between 10 and 1000
    return random.randint(10,10000)

def get_current_time():
    # returns the current time
    return datetime.datetime.now()

def build_json_obj(id, name, price, time):
    data_obj = {"TransactionID": id, "UserId": "U" + str(id), "SellerID": "S" + str(id), "Product Name": name,
                "Sale Price": price, "Transaction Date": str(time)}
    return data_obj

def build_host_name():
    uri = 'https://' + namespace + '.servicebus.windows.net/' + queue_name
    return uri

# generating SAS token for later use
token_dict = get_auth_token()
token = token_dict['token']
