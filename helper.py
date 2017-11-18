import datetime
import hashlib
import hmac
import urllib
import base64
import random
import time

namespace = 'second-bus'
queue_name = 'queue'
key_name = 'test'
shared_key = 'UKoJV2DMgw38m+cE1v9rmwqhADsgT/Ke0Vq95Ly1KsE='

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
    # returns either a valid id or id to indicate failure depending on the currId number

    # failure rate of 1 in every 1000 requests, i.e., 0.1% (sending 1 million requests)
    if curr_id % 1000 == 0:
        # negative id indicates a failed request
        return -1
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