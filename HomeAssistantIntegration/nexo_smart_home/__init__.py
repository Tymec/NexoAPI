from NexoAPI import NexoVisionWrapper

_LOGGER = logging.getLogger(__name__)
DOMAIN = "nexo_smart_home"


def setup(hass, config):
    """Setup our skeleton component."""
    # States are in the format DOMAIN.OBJECT_ID.
    hass.states.set('nexo_smart_home.Nexo', 'Works!')
    
    host = config.get(CONF_HOST)
    password = config.get(CONF_PASSWORD)
    
    hub = NexoWrapper(host, password, custom_logger=_LOGGER)

    # Return boolean to indicate that initialization was successfully.
    return True