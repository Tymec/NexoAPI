import socket
from hashlib import md5
import sys
import logging


class NexoVisionClient:
    def __init__(self, ip, password, port=1024, timeout=2, silent_log=False, use_ussl=False):
        self.setup()

        self.BUFFER_SIZE = 1024
        self.COMMAND_PREFIX = b'@00000000:'
        self.COMMAND_SUFFIX = b'\000'
        self.ENCODING = 'Cp1250'

        self.sock = None
        self.ussl = False
        self.silent_log = silent_log

        self.connect(ip, port, timeout)
        self.setup_ussl(use_ussl)
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

    def setup_ussl(self, use_ussl):
        if use_ussl:
            self.send(b"uSSL\n", prefix=False, suffix=False)
        else:
            self.send(b"plain\n", prefix=False)

        data = self.receive()
        if data == "uSSL OK":
            self.ussl = True
            self.log("Using uSSL", 'info')
            return True
        elif data == "NO uSSL":
            self.ussl = False
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
        resources = {}
        dev_type_list = [
            "sensor",
            "analogsensor",
            "partition",
            "partition24h",
            "output",
            "output_group",
            "light",
            "dimmer",
            "light_group",
            "analogoutput",
            "analogoutput_group",
            "rgbw",
            "rgbw_group",
            "blind",
            "blind_group",
            "thermometer",
            "thermostat",
            "gate",
            "ventilator"
        ]

        for dev_type in dev_type_list:
            iterator = 1
            while True:
                if iterator > 21:
                    break
                data = self.send_and_read(f"system T {dev_type} {iterator} ?")
                if data == "CMD OK":
                    resp = self.send_and_read("get")
                    if resp == "~00000000:":
                        continue
                    elif resp.startswith("~00000000:~T"):
                        print(resp)
                iterator += 1

    def switch(self, name, state):
        data = self.send_and_read(f"system C '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            if resp == "~00000000:":
                self.log(f"Switched {name} to {'on' if state == '1' else 'off'}", "info")
                return
            self.log(f"Current state of {name} is {'on' if resp.split(' ')[1] == '65281' else 'off'}", "info")

    def logic(self, name, state):
        data = self.send_and_read(f"system L '{name}' {state}")

        if data == "CMD OK":
            resp = self.send_and_read("get")
            print(resp)
            if resp == "~00000000:":
                self.log(f"Switched {name} to {'on' if state == '1' else 'off'}", "info")
                return
            self.log(f"Current state of {name} is {'on' if resp.split(' ')[1] == '65281' else 'off'}", "info")


if __name__ == "__main__":
    nexo_client = NexoVisionClient('192.168.1.75', '1510', use_ussl=False)
    nexo_client.logic('Rec.soverom 1', '?')
    # nexo_client.import_resources()
    nexo_client.disconnect()
