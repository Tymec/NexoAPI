import socket
from hashlib import md5
import sys
import logging
from DeviceTypes import CategoryTypes, ElTypes


class NexoVisionClient:
    def __init__(self, ip, password, port=1024, timeout=2, silent_log=False, use_ssl=False):
        self.setup()

        self.BUFFER_SIZE = 1024
        self.COMMAND_PREFIX = b'@00000000:'
        self.COMMAND_SUFFIX = b'\000'
        self.ENCODING = 'Cp1250'

        self.sock = None
        self.ssl = False
        self.silent_log = silent_log
        
        self.resources = {}
        
        self.connect(ip, port, timeout)
        self.setup_ssl(use_ssl)
        self.authorize(password)
        self.check_connection()

    def setup(self):
        logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.DEBUG)

    def connect(self, ip, port, timeout):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((ip, port))
        self.sock.settimeout(timeout)
        self.log(self.receive(), 'info')

    def disconnect(self):
        self.sock.close()
        self.log("Client disconnected from the server", 'info')

    def setup_ssl(self, use_ssl):
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
        if self.silent_log:
            return
        if log_level == 'info':
            logging.info(message)
        elif log_level == 'warning':
            logging.warning(message)
        elif log_level == 'error':
            logging.error(message)
        elif log_level == 'critical':
            logging.critical(message)
        elif log_level == 'debug':
            logging.debug(message)
        else:
            logging.info(message)

    def authorize(self, password):
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
        if type(cmd) is str:
            cmd = bytes(cmd, self.ENCODING)
        if prefix:
            cmd = self.COMMAND_PREFIX + cmd
        if suffix:
            cmd = cmd + self.COMMAND_SUFFIX
        self.sock.sendall(cmd)

    def receive(self, encoding='utf-8'):
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
        data = self.send_and_read("ping")
        if data == '~00000000:pong':
            self.log("Connection is still active", 'info')
            return True
        else:
            self.log("Connection is not active", 'warning')
            return False

    def send_and_read(self, cmd, prefix=True, suffix=True):
        self.send(cmd, prefix=prefix, suffix=suffix)
        return self.receive()

    def import_resources(self):
        resources = {
            "sensor": [],
            "analogsensor": [],
            "partition": [],
            "partition24h": [],
            "output": [],
            "output_group": [],
            "light": [],
            "dimmer": [],
            "light_group": [],
            "analogoutput": [],
            "analogoutput_group": [],
            "rgbw": [],
            "rgbw_group": [],
            "blind": [],
            "blind_group": [],
            "thermometer": [],
            "thermostat": [],
            "gate": [],
            "ventilator": []
        }
        self.log("Importing devices...", 'info')
        for id, dev_type in enumerate(resources.keys()):
            iterator = 0
            while True:
                if iterator > 10:
                    break
                #print(f"Device: {dev_type.capitalize()} - iteration: {iterator}", end='\r')
                data = self.send_and_read(f"system T {id} {iterator} ?")
                if data != "CMD OK":
                    self.log("Something went wrong while importing devices: CMD WRONG", 'error')
                    return
                
                resp = self.send_and_read("get")
                split_resp = resp.split(' ')
                
                print(split_resp)
                #if len(split_resp) == 3:
                #    break
                if len(split_resp) > 3:
                    device_id = split_resp[3:]
                    resources[dev_type].append(' '.join(device_id))
                iterator += 1
        # print(resources)
        return resources
    
    def switch(self, name, state):
        data = self.send_and_read(f"system C '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            try:
                int(state)
                if resp == "~00000000:":
                    self.log(f"Switched {name} to {'on' if state == '1' else 'off'}", "info")
                    return
            except:
                self.log(f"Response: {resp}", "warning")

    def logic(self, name, state):
        data = self.send_and_read(f"system L '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            print(resp)
            if resp == "~00000000:":
                self.log(f"Switched {name} to {'on' if state == '1' else 'off'}", "info")
                return
            self.log(f"Current state of {name} is {resp.split(' ')[1]}", "info")

if __name__ == "__main__":
    nexo_client = NexoVisionClient('192.168.1.75', '1510')
    nexo_client.import_resources()
    nexo_client.disconnect()