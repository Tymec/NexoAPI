import ujson as json
import time
from threading import Timer
import sys
import socket
from hashlib import md5
import logging
from enum import Enum


class CategoryTypes(Enum):
    CATEGORY_ALARM = "alarm"
    CATEGORY_AUTOMATION = "automation"
    CATEGORY_BLINDS = "blinds"
    CATEGORY_CAMERAS = "cameras"
    CATEGORY_GATES = "gates"
    CATEGORY_GEOLOCATION = "geolocation"
    CATEGORY_LIGHT = "light"
    CATEGORY_LOGICS = "logics"
    CATEGORY_MULTIMEDIA = "multimedia"
    CATEGORY_PLACES = "places"
    CATEGORY_POLYGONS = "polygons"
    CATEGORY_SENSORS = "sensors"
    CATEGORY_TEMPERATURE = "temperature"
    CATEGORY_VIDEOPHONES = "videophones"

   
class ElTypes(Enum):
    EL_TYPE_ANALOGOUTPUT = "analogoutput"
    EL_TYPE_ANALOGOUTPUT_GROUP = "analogoutput_group"
    EL_TYPE_ANALOGSENSOR = "analogsensor"
    EL_TYPE_BLIND = "blind"
    EL_TYPE_BLIND_GROUP = "blind_group"
    EL_TYPE_CAMERA = "camera"
    EL_TYPE_CATEGORY = "category"
    EL_TYPE_DIMMER = "dimmer"
    EL_TYPE_GATE = "gate"
    EL_TYPE_GEOLOCATIONPOINT = "geolocationpoint"
    EL_TYPE_LIGHT = "light"
    EL_TYPE_LIGHT_GROUP = "light_group"
    EL_TYPE_LOGIC = "logic"
    EL_TYPE_OUTPUT = "output"
    EL_TYPE_OUTPUT_GROUP = "output_group"
    EL_TYPE_PARTITION = "partition"
    EL_TYPE_PARTITION24H = "partition24h"
    EL_TYPE_POLYGON = "polygon"
    EL_TYPE_RGBW = "rgbw"
    EL_TYPE_RGBW_GROUP = "rgbw_group"
    EL_TYPE_SCENE = "scene"
    EL_TYPE_SENSOR = "sensor"
    EL_TYPE_SET = "set"
    EL_TYPE_THERMOMETER = "thermometer"
    EL_TYPE_THERMOMETER_GROUP = "thermometer_group"
    EL_TYPE_THERMOSTAT = "thermostat"
    EL_TYPE_THERMOSTAT_GROUP = "thermostat_group"
    EL_TYPE_VENTILATOR = "ventilator"
    EL_TYPE_VIDEOPHONE = "videophone"


class DeviceState(Enum):
    ON = 65281
    OFF = 0


class ImportTypes(Enum):
    SENSOR = 1
    ANALOGSENSOR = 2
    PARTITION = 3
    PARTITION24H = 4
    OUTPUT = 5
    OUTPUT_GROUP = 6
    LIGHT = 7
    DIMMER = 8
    LIGHT_GROUP = 9
    ANALOG_OUTPUT = 10
    ANALOGOUTPUT_GROUP = 11
    RGBW = 12
    RGBW_GROUP = 13
    BLIND = 14
    BLIND_GROUP = 15
    THERMOMETER = 16
    THERMOSTAT = 17
    THERMOSTAT_GROUP = 18
    GATE = 257
    VENTILATOR = 258


class NexoVisionClient:
    def __init__(self, ip, password, port=1024, timeout=2, use_ssl=False):
        #self.BUFFER_SIZE = 1024
        self.BUFFER_SIZE = 16384
        self.COMMAND_PREFIX = b'@00000000:'
        self.COMMAND_SUFFIX = b'\000'
        self.NULL_RESPONSE = '~00000000:'
        self.ENCODING = 'Cp1250'
        
        self.password = password
        self.is_connected = False
        self.sock = None
        self.address = (ip, port)
        self.timeout = timeout
        self.ssl = use_ssl 

    def initialize_connection(self):
        '''Initialize the connection with the server and authenticate'''
        self.connect(self.address[0], self.address[1], self.timeout)
        self.setup_ssl(self.ssl)
        self.authorize(self.password)
        return self.check_connection()

    def connect(self, ip, port, timeout):
        '''Open a new socket connection with the server'''
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((ip, port))
        self.sock.settimeout(timeout)
        logging.info(self.receive(encoding='ISO-8859-1'))

    def disconnect(self):
        '''Close the socket connection'''
        self.sock.close()
        logging.info("Client disconnected from the server")

    def setup_ssl(self, use_ssl):
        '''Setup a SSL connection or use plain connection'''
        if use_ssl:
            self.send(b"uSSL\n", prefix=False, suffix=False)
        else:
            self.send(b"plain\n", prefix=False)

        data = self.receive(encoding='ISO-8859-1')
        if data == "uSSL OK":
            self.ssl = True
            logging.info("Using uSSL")
            return True
        elif data == "NO uSSL":
            self.ssl = False
            logging.info("Not using uSSL")
            return False
        else:
            return

    def authorize(self, password):
        '''Authorize with the server using a password'''
        if not password:
            logging.warning("No password specified")

        pass_b = bytes(password, 'ISO-8859-1')
        m = md5(pass_b)
        output = bytearray(m.digest())
        output.append(0)
        output.append(10)
        login_success = self.send_and_read(output, prefix=False, suffix=False)

        if login_success == 'LOGIN OK':
            logging.info("Login succeeded")
        elif login_success == 'LOGIN FAILED':
            logging.error("Login failed")

    def send(self, cmd, prefix=True, suffix=True):
        '''Send a command via the socket connection'''
        if type(cmd) is str:
            cmd = bytes(cmd, self.ENCODING)
        if prefix:
            cmd = self.COMMAND_PREFIX + cmd
        if suffix:
            cmd = cmd + self.COMMAND_SUFFIX
        self.sock.sendall(cmd)

    def receive(self, encoding='utf-8'):
        '''Read the received message from the server'''
        try:
            data = self.sock.recv(self.BUFFER_SIZE)
        except socket.timeout as e:
            logging.error("Socket timeout")
            return None
        except socket.error as e:
            logging.error("Socket error")
            self.initialize_connection(self.password)
            return self.check_connection()
        else:
            if encoding:
                data = data.decode(encoding)
            return data

    def check_connection(self):
        '''Check if the connection with the server is still alive'''
        data = self.send_and_read("ping")
        if data == '~00000000:pong':
            logging.info("Connection is still active")
            self.is_connected = True
        else:
            logging.error("Connection is not active")
            self.is_connected = False
        return self.is_connected

    def send_and_read(self, cmd, prefix=True, suffix=True):
        '''Send a command and return the receive message'''
        self.send(cmd, prefix=prefix, suffix=suffix)
        return self.receive()

    def import_resources(self):
        '''Import all the existing resources from the server'''
        resources = {}
        logging.info("Importing resources")
        for device in ImportTypes:
            iterator = 0
            while True:
                data = self.send_and_read(f"system T {device.value} {iterator} ?")
                if data != "CMD OK":
                    logging.error("Something went wrong while importing devices: CMD WRONG")
                    return
                
                resp = self.send_and_read("get")
                split_resp = resp.split(' ')
                
                if len(split_resp) == 3:
                    break
                if len(split_resp) > 3:
                    device_id = split_resp[3:]

                    if not resources.get(device):
                        resources[device] = []
                    resources[device].append(' '.join(device_id))
                iterator += 1
        logging.info("Finished importing resources")
        return resources
    
    def import_resource(self, resource):
        '''Import specified resource from the server'''
        devices = []
        
        if resource in [x.name for x in ImportTypes]:
            resource = ImportTypes[resource]
        else:
            logging.info("No such resource")
            return
        
        logging.info(f"Importing resource {resource}")
        iterator = 0
        while True:
            data = self.send_and_read(f"system T {resource.value} {iterator} ?")
            if data != "CMD OK":
                logging.info("Something went wrong while importing devices: CMD WRONG")
                return
            
            resp = self.send_and_read("get")
            split_resp = resp.split(' ')
            
            if len(split_resp) == 3:
                break
            if len(split_resp) > 3:
                device_id = split_resp[3:]
                devices.append(' '.join(device_id))
            iterator += 1
            
        logging.info(f"Finished importing resource {resource}")
        return devices
    
    def clear_server_buffer_queue(self):
        '''Clear the server buffer queue on the server'''
        logging.info("Clearing the server-side buffer queue")
        while True:
            response = self.send_and_read("get")
            if response == self.NULL_RESPONSE:
                logging.info("Finished clearing the server-side buffer queue")
                return True
    
    def system_c(self, name, state):
        data = self.send_and_read(f"system C '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            return resp.split(' ')[-1]
        return False

    def system_l(self, name, state):
        data = self.send_and_read(f"system L '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            return resp.split(' ')[-1]
        return False


class NexoWrapper:
    def __init__(self, host, password):
        self.nexo_client = NexoVisionClient(host, password)
        self.nexo_client.initialize_connection()
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
    
    def connect(self):
        if self.nexo_client.is_connected:
            self.disconnect()
        return self.nexo_client.initialize_connection()

    def disconnect(self):
        self.nexo_client.disconnect()
     
    def get_state(self, name):
        state = self.nexo_client.system_c(name, '?')
        if not state:
            state = self.get_state(name)
        return int(state)
    
    def set_state(self, name, state):
        if state != 1 and state != 0:
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
