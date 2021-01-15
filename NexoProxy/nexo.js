const net = require('net');
var crypto = require('crypto');
var iconv = require('iconv');

class NexoJS {
    constructor(host, port, pass, timeout=2, ssl=false) {
        this.host = host;
        this.port = port;
        this.pass = pass;
        this.timeout = timeout;
        this.ssl = ssl;
        
        this.cmdPrefix = '@00000000:';
        this.cmdSuffix = '00';
        this.nullResponse = '~00000000:';
        this.encoding = 'Cp1250';
        
        this.isConnected = null;
        this.isUsingSSL = null;
        this.isAuthenticated = null;
        this.isAlive = null;
        this.isBusy = false;

        this.busyTimer = null;
        this.socket = new net.Socket();
    }

    async init() {
        let client = this;

        return new Promise((resolve, reject) => {
            client.socket.connect(client.port, client.host, () => {
                console.log('[NEXO] Socket connection was established');
            });
            
            client.socket.on('data', (data) => {
                let data_str = data.toString();
                console.log(`[NEXO] Received: ${data_str}`);

                if (data_str.includes('Welcome')) {
                    console.log('[NEXO] Connected to Nexo');
                    client.isConnected = true;
                    client.setupSSL();

                } else if (data_str === 'uSSL OK') {
                    console.log('[NEXO] Using SSL');

                    client.auth();
                    client.isUsingSSL = true;

                } else if (data_str === 'NO uSSL') {
                    console.log('[NEXO] Not using SSL');

                    client.auth();
                    client.isUsingSSL = false;

                } else if (data_str === 'LOGIN OK') {
                    console.log('[NEXO] Authentication was successful');

                    client.checkConnection();
                    client.isAuthenticated = true;

                } else if (data_str === 'LOGIN FAILED') {
                    console.log('[NEXO] Authentication failed');
                    
                    client.isAuthenticated = false;

                } else if (data_str === '~00000000:pong') {
                    console.log('[NEXO] Connection is alive');

                    if (client.isAlive === null) {
                        client.clearServerBuffer();
                        setInterval(() => { client.checkConnection(); }, 1000 * 5);
                        resolve(client, true);
                    }
                    client.isAlive = true;

                } else if (data_str === 'CMD OK') {
                    console.log('[NEXO] Command was successful');
                
                } else if (data_str.startsWith('~00000000:')) {
                    console.log('[NEXO] Null response');

                } else {
                    console.log(`[NEXO] Unknown command: ${data_str}`);
                }
            });
            
            client.socket.on('close', function() {
                console.log('[NEXO] Socket connection closed');
                reject('Connection closed');
            });
        });
    }

    send(cmd, prefix=true, suffix=true) {
        console.log(`[NEXO] Sent: ${cmd}`);
        let client = this;
        let _cmd = Buffer.from(cmd, 'utf-8');;
        if (prefix) {
            let buffer_prefix = Buffer.from(client.cmdPrefix, 'utf-8');
            _cmd = Buffer.concat([buffer_prefix, _cmd]);
        }
        if (suffix) {
            let buffer_suffix = Buffer.from(client.cmdSuffix, 'hex');
            _cmd = Buffer.concat([_cmd, buffer_suffix]);
        }

        client.socket.write(_cmd);
    }

    setupSSL() {
        let client = this;

        let useSsl = this.ssl ? 'uSSL\n' : 'plain\n';
        client.send(useSsl, false, false);
    }

    auth() {
        let client = this;

        if (!client.pass) 
            console.log('No password specified.');

        let icv = new iconv.Iconv('UTF-8', 'ISO-8859-1');
        let buffer = icv.convert(Buffer.from(client.pass));
        let hashed = crypto.createHash('md5').update(buffer).digest();
        let buffer_eof = Buffer.from('000A', 'hex');
        let _pass = Buffer.concat([hashed, buffer_eof]);
        
        client.send(_pass, false, false);
    }

    checkConnection() {
        let client = this;

        if (!client.isBusy)
            client.send('ping');
    }

    async clearServerBuffer() {
        let client = this;

        await client._sendAndRead('get').then(async (data) => {
            if (data === "~00000000:") {
                return;
            }
            await client.clearServerBuffer();
        });
    }

    disconnect() {
        let client = this;

        client.socket.destroy();
    }

    getState(name) {
        let client = this;

        client.isBusy = true;
        if (client.busyTimer) {
            clearTimeout(client.busyTimer);
        }

        return new Promise(async (resolve, reject) => {
            await client._sendAndRead(`system C '${name}' ?`).then(async (success) => {
                if (success === 'CMD OK') {
                    await client._sendAndRead('get').then((data) => {
                        client.busyTimer = setTimeout(() => { client.isBusy = false; }, 5000)

                        let splitData = data.split(':');
                        if (splitData[1] === '')
                            reject(`Invalid entity: ${name}`);
                        resolve(splitData[1].split(' ')[1]);
                    });
                }
                reject('Invalid command');
            });
        });
    }

    async _sendAndRead(cmd) {
        let client = this;

        client.send(cmd);
        return new Promise((resolve, reject) => {
            client.socket.once('data', (data) => {
                resolve(data.toString());
            });
        });
    }

    setState(name, value) {
        let client = this;

        client.isBusy = true;
        if (client.busyTimer) {
            clearTimeout(client.busyTimer);
        }

        let val = value > 0 ? 1 : 0;
        return new Promise(async (resolve, reject) => {
            await client._sendAndRead(`system C '${name}' ${val}`).then((success) => {
                if (success === 'CMD OK') {
                    client.busyTimer = setTimeout(() => { client.isBusy = false; }, 5000)
                    resolve();
                }
                reject('Invalid command');
            });
        });
    }
};

module.exports = NexoJS;