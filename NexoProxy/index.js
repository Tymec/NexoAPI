require('dotenv').config();
const express = require('express')
const nexo = require('./nexo');

const app = express();
const port = 3000;

app.use(express.json());

var nexoClient = new nexo(process.env.NEXO_HOST, process.env.NEXO_PORT, process.env.NEXO_PASS);
nexoClient.init().then((client, connected) => {
    app.get('/', (req, res) => {
        res.status(200).send(`
            <html>
                <head>
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
                    <script>
                        var switchVal = true;
                        function toggleSwitch() {
                            switchVal = !switchVal;
                            $.ajax('/state/l.gang11', {
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
                            
                            $.ajax('/state/l.gang1', {
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
        let body = req.body;
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
}).catch((err) => {
    return;
});

app.listen(port, () => {
    console.log(`Express app listening at http://localhost:${port}`)
})