from pyNexo import NexoWrapper
import fire
from secrets import NEXO_IP, NEXO_PASS

for i in range(0, 100):
    nex = NexoWrapper(NEXO_IP, NEXO_PASS)
    state = nex.get_state('l.gang1')
    print(state)
    nex.disconnect()