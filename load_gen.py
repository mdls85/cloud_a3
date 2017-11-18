import helper
from locust import HttpLocust, TaskSet, task
import json

class MyTaskSet(TaskSet):

    # id is a static variable used in generating user and seller ids
    id = 1

    @task()
    def push_load(self):

        # fetch json object values
        id = helper.get_id(MyTaskSet.id)
        name = helper.get_random_product_name()
        price = helper.get_random_price()
        time = helper.get_current_time()

        data_obj = helper.build_json_obj(id,name,price,time)

        # increment id regardless of whether failure occurred
        MyTaskSet.id += 1

        # fetching SAS token
        token = helper.token

        # preparing headers to be used in POST request
        broker_props = {'ContentType' : 'application/json'}

        headers = {
            'Authorization': token,
            'Content-Type': 'application/atom+xml;type=entry;charset=utf-8',
            'BrokerProperties': json.dumps(broker_props)}

        # send post request to specified host
        self.client.post('/messages', data=json.dumps(data_obj), headers=headers)


class MyLocust(HttpLocust):
    # this class represents an HTTP user which is to be hatched and attack the system that is to be load tested.

    task_set = MyTaskSet            # defining the behaviour of the user
    min_wait = 0
    max_wait = 0
    # host to bombard
    #host = 'https://second-bus.servicebus.windows.net/queue'
    host = helper.build_host_name()