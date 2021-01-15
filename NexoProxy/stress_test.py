import requests
import time

val = True
delay = 0.1
for i in range(0, 1000):
    val = not val
    #_state = 1 if val else 0
    _state = 0

    response = requests.get('http://192.168.1.154:3000/state/l.gang1')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller1', json={'state': _state})
    print(response.json())
    time.sleep(delay)
    response = requests.get('http://192.168.1.154:3000/state/l.gang11')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller11', json={'state': _state})
    print(response.json())
    time.sleep(delay)
    response = requests.get('http://192.168.1.154:3000/state/l.gang1')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller1', json={'state': _state})
    print(response.json())
    time.sleep(delay)
    response = requests.get('http://192.168.1.154:3000/state/l.korridor11')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller11', json={'state': _state})
    print(response.json())
    time.sleep(delay)
    response = requests.get('http://192.168.1.154:3000/state/l.kjeller1')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller1', json={'state': _state})
    print(response.json())
    time.sleep(delay)
    response = requests.get('http://192.168.1.154:3000/state/l.stua11')
    print(response.json())
    time.sleep(delay)
    response = requests.post('http://192.168.1.154:3000/state/l.kjeller11', json={'state': _state})
    print(response.json())
    time.sleep(delay)