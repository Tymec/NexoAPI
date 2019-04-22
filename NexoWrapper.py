from NexoVisionClient import NexoVisionClient
import ujson


class NexoWrapper:
    def __init__(self, ip_address, password):
        self.nexo_client = NexoVisionClient(ip_address, password)
        self.nexo_client.clear_server_buffer_queue()
        
    def get_state(self, name):
        state = self.nexo_client.system_c(name, '?')
        return state
    
    def set_state(self, name, state):
        if state is not 1 and state is not 0:
            self.nexo_client.log("Wrong value", 'error')
            return
        self.nexo_client.system_c(name, state)
        new_state = self.get_state(name)
        return new_state

    def stress_test(self):
        import time
        vifte = False
        i = 0
        start = time.time()
        while not vifte:
            if i > 120:
                break
            self.get_state('vifte bad')
            i += 1
        print(f"Took {time.time() - start}")

    def debug(self):
        with open('resources.json', 'r') as f:
            resources = ujson.load(f)
        state = self.get_state('GANG term')
        print(state)
        self.nexo_client.disconnect()
        
        
if __name__ == "__main__":
    nexo_wrapper = NexoWrapper('192.168.1.75', '1510')
    nexo_wrapper.stress_test()
