import helper
from locust import HttpLocust, TaskSet, task
import db_handler
import json

class MyTaskSet(TaskSet):

    @task()
    def pull(self):

        # fetching SAS token
        token = helper.token

        # preparing headers to be used in DELETE request

        headers = {
            'Authorization': token,
        }

        # send delete request to specified host
        response = self.client.delete('/messages/head', headers=headers)

        # build json object from response and get id
        json_obj = json.loads(response.content)
        id = json_obj['TransactionID']

        # obtain a connection to redis to 'process' the record
        conn = db_handler.get_connection()

        conn.execute_command('JSON.SET', id, '.', response.content)

        # reply = json.loads(conn.execute_command('JSON.GET', id))
        # print reply

class MyLocust(HttpLocust):
    # this class represents an HTTP user which is to be hatched and attack the system that is to be load tested.

    task_set = MyTaskSet            # defining the behaviour of the user
    min_wait = 0
    max_wait = 0
    # host to bombard
    host = helper.build_host_name()
