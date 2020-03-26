from NexoVisionClient import NexoVisionClient
import ujson as json
import time
from threading import Timer
import sys


class NexoWrapper:
    def __init__(self, host, password):
        self.nexo_client = NexoVisionClient(host)
        self.nexo_client.initialize_connection(password)
        self.nexo_client.clear_server_buffer_queue()
        self.check_connection()
        
    def check_connection(self):
        alive = self.nexo_client.check_connection()
        # Timer(2.0, self.check_connection).start()
        # print(alive)
        return alive
     
    def process_queue(self, queue):
        states = {}
        for item in queue:
            states[item] = self.get_state(item)
        return states
     
    def disconnect(self):
        self.nexo_client.disconnect()
     
    def get_state(self, name):
        state = self.nexo_client.system_c(name, '?')
        if not state:
            state = self.get_state(name)
        return int(state)
    
    def set_state(self, name, state):
        if state is not 1 and state is not 0:
            return
        self.nexo_client.system_c(name, state)
        new_state = self.get_state(name)
        return new_state
    
    def import_resource(self, resource):
        res = self.nexo_client.import_resource(resource)
        return res
        
    def import_resources(self):
        res = self.nexo_client.import_resources()
        return res

if __name__ == "__main__":
    nex = NexoWrapper('192.168.1.75', '1510')
    for x in range(100):
        print(nex.get_state('l.gang'))
        time.sleep(5)
