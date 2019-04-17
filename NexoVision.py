import socket
from hashlib import md5
import atexit
import sys
import logging


class NexoVisionClient:
    def __init__(self, ip, port=1024, timeout=2, silent_log=False, use_ussl=False):
        self.setup()
    
        self.BUFFER_SIZE = 1024
        self.COMMAND_PREFIX = b'@00000000:'
        self.ussl = False
        self.silent_log = silent_log
        
        self.sock = self.connect(ip, port, timeout)
        self.log(self.recieve(), 'info')
        self.setup_ussl(use_ussl)
    
    def setup(self):
        logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.DEBUG)
        atexit.register(self.on_exit)
    
    def connect(self, ip, port, timeout):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((ip, port))
        sock.settimeout(timeout)
        return sock
    
    def close(self):
        self.sock.close()
    
    def setup_ussl(self, use_ussl=False):
        if use_ussl:
            self.send(b"uSSL\n", prefix=False)
        else:
            self.send(b"plain\n\000", prefix=False)
        
        data = self.recieve()
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
        
        output = []
        pass_b = bytes(password, 'ISO-8859-1')
        m = md5(pass_b)
        output = bytearray(m.digest())
        output.append(10)
        output.append(0)
        
        self.send(output, prefix=False)
        
        login_success = self.recieve()
        if login_success == 'LOGIN OK':
            self.log("Login succeeded", 'info')
        elif login_success == 'LOGIN FAILED':
            self.log("Login failed", 'warning')
        
    def send(self, cmd, prefix=True):
        if prefix:
            cmd = self.COMMAND_PREFIX + cmd
        self.sock.sendall(cmd)
    
    def recieve(self):
        data = None
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
            return data.decode('utf-8')
    
    def check_connection(self):
        self.send(b'ping\000')
        data = self.recieve()
        if data == '~00000000:pong':
            self.log("Connection is still active", 'info')
            return True
        else:
            self.log("Connection is not active", 'warning')
            return False
    
    def switch(self):
        self.send(b'get Home')
        data = self.recieve()
        print(data)
    
    def on_exit(self):
        self.close()
    
if __name__ == "__main__":
    nexo_client = NexoVisionClient('192.168.1.75')
    nexo_client.authorize('1510')
    nexo_client.check_connection()
    nexo_client.switch()
    nexo_client.close()