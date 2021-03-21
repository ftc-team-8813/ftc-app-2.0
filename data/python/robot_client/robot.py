import client
import struct
# also very WIP

# Class templates

# class Actuator:
#     def update(self, conn):
#         pass

# class Sensor:
#     def update(self, conn):
#         pass

class Robot:
    def __init__(self, conn):
        self.conn = conn
        self.actuators = {}
        self.sensors = {}

    def add_actuator(self, name, actuator):
        self.actuators[name] = actuator

    def add_sensor(self, name, sensor):
        self.sensors[name] = sensor

    def update(self):
        for actuator in self.actuators:
            actuator.update(self.conn)
        for sensor in self.sensors:
            sensor.update(self.conn)

class HardwareMap:
    packet_id = 0x01

    def __init__(self, conn):
        self.conn = conn

    def __getport(self, prefix, name):
        # Packet data
        # - u8   0x4d    'M' = get motor, 'S' = get servo, 'D' = get digital, 'A' = get analog
        # - name         [utf-8, terminated by end of packet]
        # Response data
        # [empty]        No motor with that name
        # OR
        # - u8   hub ID
        # - u8   port
        data = prefix + name.encode('utf-8')
        rdata = self.conn.send_recv(self.packet_id, data)
        if rdata is None:
            print(' -> disconnected')
            return None
        if len(rdata) == 0:
            print(' -> not found')
            return (-1, -1)

        hub_id, port = struct.unpack('>BB', rdata)
        print(' -> hub #%d port %d' % (hub_id, port))
        return (hub_id, port)

    def get_motor_port(self, name):
        print("[Hardware Map] Get Motor: '%s'" % name)
        return self.__getport(b'M', name)

    def get_servo_port(self, name):
        print("[Hardware Map] Get Servo: '%s'" % name)
        return self.__getport(b'S', name)

    def get_digital_port(self, name):
        print("[Hardware Map] Get Digital: '%s'" % name)
        return self.__getport(b'D', name)

    def get_analog_port(self, name):
        print("[Hardware Map] Get Analog: '%s'" % name)
        return self.__getport(b'A', name)