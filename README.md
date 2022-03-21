<img align="right" width="200" src="./res/icon.png"></img>
NexoAPI 
[![Maintenance](https://img.shields.io/badge/Maintained%3F-no-red.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)
===
Unofficial API for controlling home automation that runs on Nexwell Nexo system
<br>
<br>
<br>
<br>
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
