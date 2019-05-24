import socket
from hashlib import md5
import sys
import logging
from DeviceTypes import ImportTypes


class NexoVisionClient:
    def __init__(self, ip, port=1024, timeout=2, use_ssl=False):
        self.BUFFER_SIZE = 1024
        self.COMMAND_PREFIX = b'@00000000:'
        self.COMMAND_SUFFIX = b'\000'
        self.NULL_RESPONSE = '~00000000:'
        self.ENCODING = 'Cp1250'
        
        self.password = None
        self.is_connected = False
        self.sock = None
        self.address = (ip, port)
        self.timeout = timeout
        self.ssl = use_ssl 

    def initialize_connection(self, password):
        '''Initialize the connection with the server and authenticate'''
        self.connect(self.address[0], self.address[1], self.timeout)
        self.setup_ssl(self.ssl)
        self.authorize(password)
        self.password = password
        self.check_connection()

    def connect(self, ip, port, timeout):
        '''Open a new socket connection with the server'''
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((ip, port))
        self.sock.settimeout(timeout)
        logging.info(self.receive())

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

        data = self.receive()
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
