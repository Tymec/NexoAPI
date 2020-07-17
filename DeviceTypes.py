from enum import Enum

class CategoryTypes(Enum):
    CATEGORY_ALARM = "alarm"
    CATEGORY_AUTOMATION = "automation"
    CATEGORY_BLINDS = "blinds"
    CATEGORY_CAMERAS = "cameras"
    CATEGORY_GATES = "gates"
    CATEGORY_GEOLOCATION = "geolocation"
    CATEGORY_LIGHT = "light"
    CATEGORY_LOGICS = "logics"
    CATEGORY_MULTIMEDIA = "multimedia"
    CATEGORY_PLACES = "places"
    CATEGORY_POLYGONS = "polygons"
    CATEGORY_SENSORS = "sensors"
    CATEGORY_TEMPERATURE = "temperature"
    CATEGORY_VIDEOPHONES = "videophones"
    
class ElTypes(Enum):
    EL_TYPE_ANALOGOUTPUT = "analogoutput"
    EL_TYPE_ANALOGOUTPUT_GROUP = "analogoutput_group"
    EL_TYPE_ANALOGSENSOR = "analogsensor"
    EL_TYPE_BLIND = "blind"
    EL_TYPE_BLIND_GROUP = "blind_group"
    EL_TYPE_CAMERA = "camera"
    EL_TYPE_CATEGORY = "category"
    EL_TYPE_DIMMER = "dimmer"
    EL_TYPE_GATE = "gate"
    EL_TYPE_GEOLOCATIONPOINT = "geolocationpoint"
    EL_TYPE_LIGHT = "light"
    EL_TYPE_LIGHT_GROUP = "light_group"
    EL_TYPE_LOGIC = "logic"
    EL_TYPE_OUTPUT = "output"
    EL_TYPE_OUTPUT_GROUP = "output_group"
    EL_TYPE_PARTITION = "partition"
    EL_TYPE_PARTITION24H = "partition24h"
    EL_TYPE_POLYGON = "polygon"
    EL_TYPE_RGBW = "rgbw"
    EL_TYPE_RGBW_GROUP = "rgbw_group"
    EL_TYPE_SCENE = "scene"
    EL_TYPE_SENSOR = "sensor"
    EL_TYPE_SET = "set"
    EL_TYPE_THERMOMETER = "thermometer"
    EL_TYPE_THERMOMETER_GROUP = "thermometer_group"
    EL_TYPE_THERMOSTAT = "thermostat"
    EL_TYPE_THERMOSTAT_GROUP = "thermostat_group"
    EL_TYPE_VENTILATOR = "ventilator"
    EL_TYPE_VIDEOPHONE = "videophone"

class DeviceState(Enum):
    ON = 65281
    OFF = 0

class ImportTypes(Enum):
    SENSOR = 1
    ANALOGSENSOR = 2
    PARTITION = 3
    PARTITION24H = 4
    OUTPUT = 5
    OUTPUT_GROUP = 6
    LIGHT = 7
    DIMMER = 8
    LIGHT_GROUP = 9
    ANALOG_OUTPUT = 10
    ANALOGOUTPUT_GROUP = 11
    RGBW = 12
    RGBW_GROUP = 13
    BLIND = 14
    BLIND_GROUP = 15
    THERMOMETER = 16
    THERMOSTAT = 17
    THERMOSTAT_GROUP = 18
    GATE = 257
    VENTILATOR = 258
