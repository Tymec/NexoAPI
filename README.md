<img align="right" width="200" src="./res/icon.png"></img>
NexoAPI 
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)
[![Requirements Status](https://requires.io/github/Tymec/NexoAPI/requirements.svg?branch=master)](https://requires.io/github/Tymec/NexoAPI/requirements/?branch=master)
===
Unofficial API for controlling your home automation using Nexwell Nexo

#### Usage
```py
from pyNexo import NexoWrapper, ImportTypes
nexo = NexoWrapper('1.2.3.4', 'password')           # Initialize the connection
state = nexo.get_state('kitchen-lights')            # Get current state of entity 'kitchen-lights'
nexo.set_state('kitchen-lights', 1)                 # Set state of entity 'kitchen-lights' to 'on'
resource = nexo.import_resource(ImportTypes.LIGHT)  # Imports every entity of type 'light' on the Nexo system
resources = nexo.import_resources()                 # Imports every entity from the Nexo system
nexo.disconnect()                                   # Close the connection         
    
```