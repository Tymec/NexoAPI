import socket
import time

HOST = '192.168.1.154'
PORT = 1024

set_ssl = False
set_auth    = False

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    print("Server is running and listening for connections...")
    conn, addr = s.accept()
    with conn:
        print('Connected with ', addr)
        conn.send(b"Welcome to Nexo!")
        while True:
            data = conn.recv(1024)
            split_data = data.split(b'\n')
            if not data:
                break
            
            # Emulated responses
            for cmd in split_data:
                if cmd == b'uSSL' or cmd == b'plain':
                    print("SSL")
                    conn.send(b'NO uSSL')
                    time.sleep(1)
                    set_ssl = True
                elif set_ssl and cmd == b'A\xa6\x03w\xba\x92\t\x19\x93\x9d\x832n\xbe\xe5\xa1\x00':
                    print("Auth")
                    conn.send(b'LOGIN OK')
                    time.sleep(1)
                    set_auth = True
                elif set_auth == True and cmd == b'@00000000:ping\x00':
                    print("Ping pong")
                    conn.send(b'~00000000:pong')
                    time.sleep(1)
