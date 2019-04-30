"""Support for the Nexwell Nexo Home Automation System"""

class NexoLight(Light):
    """Representation of a Nexo light object."""

    def __init__(self, light):
        """Initialize the light."""
        self._light = light
        self._name = light.name
        self._state = None

    @property
    def name(self):
        """Return the display name of this light."""
        return self._light.name

    @property
    def is_on(self):
        """Return true if light is on."""
        return self._state

    def turn_on(self, **kwargs):
        """Turn the light on."""
        self._light.turn_on()
        self._state = True

    def turn_off(self, **kwargs):
        """Turn the light off."""
        self._light.turn_off()
        self._state = False
        
    def update(self):
        self._state = self._light.update()
