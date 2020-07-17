from pyNexo import NexoWrapper
from DeviceTypes import ImportTypes
import fire
from secrets import NEXO_IP, NEXO_PASS

def main(device_name):
    nex = NexoWrapper(NEXO_IP, NEXO_PASS)
    state = nex.get_state(device_name)
    state = nex.set_state(device_name, not int(state))
    nex.disconnect()

if __name__ == "__main__":
    fire.Fire(main)