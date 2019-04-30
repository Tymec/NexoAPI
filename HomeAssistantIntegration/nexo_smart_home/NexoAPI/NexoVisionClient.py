import socket
from hashlib import md5
import sys
import logging
from DeviceTypes import ImportTypes


class NexoVisionClient:
    def __init__(self, ip, port=1024, timeout=2, silent_log=False, use_ssl=False, custom_logger=None):
        self.BUFFER_SIZE = 1024
        self.COMMAND_PREFIX = b'@00000000:'
        self.COMMAND_SUFFIX = b'\000'
        self.NULL_RESPONSE = '~00000000:'
        self.ENCODING = 'Cp1250'
        
        self.logger = custom_logger
        if not custom_logger:
            logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.DEBUG)
            self.logger = logging.getLogger(__name__)
        
        self.sock = None
        self.address = (ip, port)
        self.timeout = timeout
        self.ssl = use_ssl
        self.silent_log = silent_log       

    def initialize_connection(self, password):
        '''Initialize the connection with the server and authenticate'''
        self.connect(self.address[0], self.address[1], self.timeout)
        self.setup_ssl(self.ssl)
        self.authorize(password)
        self.check_connection()

    def connect(self, ip, port, timeout):
        '''Open a new socket connection with the server'''
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((ip, port))
        self.sock.settimeout(timeout)
        self.log(self.receive(), 'info')

    def disconnect(self):
        '''Close the socket connection'''
        self.sock.close()
        self.log("Client disconnected from the server", 'info')

    def setup_ssl(self, use_ssl):
        '''Setup a SSL connection or use plain connection'''
        if use_ssl:
            self.send(b"uSSL\n", prefix=False, suffix=False)
        else:
            self.send(b"plain\n", prefix=False)

        data = self.receive()
        if data == "uSSL OK":
            self.ssl = True
            self.log("Using uSSL", 'info')
            return True
        elif data == "NO uSSL":
            self.ssl = False
            self.log("Not using uSSL", 'warning')
            return False
        else:
            return

    def log(self, message, log_level):
        '''Send log messages based on the log level'''
        if self.silent_log:
            return
        if log_level == 'info':
            self.logger.info(message)
        elif log_level == 'warning':
            self.logger.warning(message)
        elif log_level == 'error':
            self.logger.error(message)
        elif log_level == 'critical':
            self.logger.critical(message)
        elif log_level == 'debug':
            self.logger.debug(message)
        else:
            self.logger.info(message)

    def authorize(self, password):
        '''Authorize with the server using a password'''
        if not password:
            self.log("No password specified", 'warning')

        pass_b = bytes(password, 'ISO-8859-1')
        m = md5(pass_b)
        output = bytearray(m.digest())
        output.append(0)
        output.append(10)
        login_success = self.send_and_read(output, prefix=False, suffix=False)

        if login_success == 'LOGIN OK':
            self.log("Login succeeded", 'info')
        elif login_success == 'LOGIN FAILED':
            self.log("Login failed", 'warning')

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
            err = e.args[0]
            if err == 'timed out':
                print('recv timed out, retry later')
                return
            else:
                print(e)
                sys.exit(1)
        except socket.error as e:
            print(e)
            sys.exit(1)
        else:
            if len(data) is 0:
                print('message is empty')
                return
            if encoding:
                data = data.decode(encoding)
            return data

    def check_connection(self):
        '''Check if the connection with the server is still alive'''
        data = self.send_and_read("ping")
        if data == '~00000000:pong':
            self.log("Connection is still active", 'info')
            return True
        else:
            self.log("Connection is not active", 'error')
            return False

    def send_and_read(self, cmd, prefix=True, suffix=True):
        '''Send a command and return the receive message'''
        self.send(cmd, prefix=prefix, suffix=suffix)
        return self.receive()

    def import_resources(self):
        '''Import all the existing resources from the server'''
        resources = {}
        self.log("Importing resources", 'info')
        for device in ImportTypes:
            iterator = 0
            while True:
                data = self.send_and_read(f"system T {device.value} {iterator} ?")
                if data != "CMD OK":
                    self.log("Something went wrong while importing devices: CMD WRONG", 'error')
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
        self.log("Finished importing resources", 'info')
        return resources
    
    def clear_server_buffer_queue(self):
        '''Clear the server buffer queue on the server'''
        self.log("Clearing the server-side buffer queue", 'info')
        while True:
            response = self.send_and_read("get")
            if response == self.NULL_RESPONSE:
                self.log("Finished clearing the server-side buffer queue", 'info')
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
