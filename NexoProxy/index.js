require('dotenv').config();
const express = require('express')
const nexo = require('./nexo');
var cors = require('cors');

const app = express();
const port = 3000;

app.use(express.json());
app.use(cors());

var nexoClient = new nexo(process.env.NEXO_HOST, process.env.NEXO_PORT, process.env.NEXO_PASS);
nexoClient.init().then((client, connected) => {
    app.get('/', (req, res) => {
        console.log(`GET request at '/state' from ${req.get('host')}`);
        res.status(200).send(`
            <html>
                <head>
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
                    <script>
                        var switchVal = true;
                        function toggleSwitch() {
                            switchVal = !switchVal;
                            $.ajax('/state/l.stua1', {
                                method: 'POST',
                                data: JSON.stringify({ "state": switchVal ? 1 : 0 }),
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                success: function(data, status, xhr) {
                                    console.log(\`Data: \${data}, status: \${status}\`);
                                },
                                error: function(jqXhr, textStatus, errorMessage) {
                                    console.log(\`Error: \${errorMessage}\`);
                                }
                            });
                            
                            $.ajax('/state/l.stua11', {
                                method: 'POST',
                                data: JSON.stringify({ "state": switchVal ? 1 : 0 }),
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                success: function(data, status, xhr) {
                                    console.log(\`Data: \${data}, status: \${status}\`);
                                },
                                error: function(jqXhr, textStatus, errorMessage) {
                                    console.log(\`Error: \${errorMessage}\`);
                                }
                            });
                        }
                    </script>
                <head/>
                <body>
                    <button onClick=toggleSwitch()>Toggle Light</button>
                </body>
            </html>
        `);
    });

    app.get('/state/:entity', async (req, res) => {
        console.log(`GET request at '/state/${req.params['entity']}' from ${req.get('host')}`);
        await client.getState(req.params['entity']).then((val) => {
            return res.status(200).send({
                entity: req.params['entity'],
                state: val > 0 ? 1 : 0
            });
        }).catch((err) => { 
            console.log(err);
            return res.status(500).send({ error: err });
        });
    });

    app.post('/state/:entity', async (req, res) => {
        console.log(`POST request at '/state/${req.params['entity']}' from ${req.get('host')}`);
        let body = req.body;
        if (body['state'] === undefined) {
            return res.status(400).send({
                message: 'Body parameter \'state\' not found.'
            });
        }

        await client.setState(req.params['entity'], body['state']).then(() => {
            return res.status(200).send({
                entity: req.params['entity'], 
                state: body['state']
            });
        }).catch((err) => { 
            console.log(err);
            return res.status(500).send({ error: err });
        });
    });

    app.post('/state', async (req, res) => {
        console.log(`POST request at '/state' from ${req.get('host')}`);
        let body = req.body;
        console.log(body);
        if (body['entities'] === undefined || body['entities'].length === 0 || typeof body['entities'] != typeof []) {
            return res.status(400).send({
                message: 'Body parameter \'entities\' not found or is invalid.'
            });
        }

        let entities = body['entities'];
        for (let idx in entities) {
            await client.setState(entities[idx]['name'], entities[idx]['state']);
        }
        
        return res.status(200).send(body);
    });

    app.get('/entities', async (req, res) => {
        console.log(`GET request at '/entities' from ${req.get('host')}`);
        return res.status(200).send({ 
            entities: {
                lights: [
                    { entity: "Lys Korridor", name: "Entrance" },
                    { entity: "Lys Gang", name: "Hall" },
                    { entity: "Lys Kjeller", name: "Basement" },
                    { entity: "Lys Bad", name: "Bathroom" },
                    { entity: "Lys Stua 1", name: "Living Room" },
                    { entity: "Lys Stua 2", name: "Sofa" },
                    { entity: "Lys TV", name: "TV" },
                    { entity: "Lys Kjokkenskap", name: "Kitchen" }
                ],
                switches: [
                    { entity: "Vifte Bad", name: "Kitchen" },
                    { entity: "Towel Heater", name: "Kitchen" }
                ]
            }
        });
    });
}).catch((err) => {
    return;
});

app.listen(port, () => {
    console.log(`Express app listening at http://localhost:${port}`)
})