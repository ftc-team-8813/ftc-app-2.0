import struct
import json

NAV_MOVE_COMPLETE = 2
NAV_TURN_COMPLETE = 1

class NavControl:
    def __init__(self, conn):
        self.conn = conn
        self.default_speed = 0.5

    def __check_rval(self, rval):
        if rval == 0xff:
            raise RuntimeError("Command processor instantation error")
        elif rval == 0xfe:
            print("[call interrupted]")

    def wait_event(self, ev, channel):
        print("Wait event: '%s' on channel %d" % (ev, channel))
        data = struct.pack('>i', channel) + ev.encode('utf-8')
        resp = self.conn.send_recv(0xfd, data)
        if resp is None: raise RuntimeError("disconnected")
        rval = resp[0]
        self.__check_rval(rval)
        if rval == 0x01:
            raise ValueError("Class '%s' not found" % ev)
        elif rval == 0x02:
            raise ValueError("Class '%s' not an event class" % ev)
        return rval

    def move(self, x, y, absolute=True, reverse=True, speed=-1, wait=True):
        if speed < 0: speed = self.default_speed
        print("Move: (%.3f, %.3f) abs=%s rev=%s speed=%.3f" % (x, y, absolute, reverse, speed))
        flags = (wait << 2) | (reverse << 1) | absolute

        data = struct.pack('>fffB', x, y, speed, flags)
        resp = self.conn.send_recv(0xfc, data)
        if resp is None: raise RuntimeError("disconnected")
        rval = resp[0]
        self.__check_rval(rval)
        return rval

    def turn(self, r, absolute=True, speed=-1, wait=True):
        if speed < 0: speed = self.default_speed
        print("Turn: %.3f abs=%s speed=%.3f" % (r, absolute, speed))
        flags = (wait << 1) | absolute

        data = struct.pack('>ffB', r, speed, flags)
        resp = self.conn.send_recv(0xfb, data)
        if resp is None: raise RuntimeError("disconnected")
        rval = resp[0]
        self.__check_rval(rval)
        return rval

    def actuator(self, name, params):
        print("Actuator: %s, params=%s" % (name, params))
        pstr = json.dumps(params)

        data = name.encode('utf-8') + b'\0' + pstr.encode('utf-8')
        resp = self.conn.send_recv(0xfa, data)
        if resp is None: raise RuntimeError("disconnected")
        rval = resp[0]
        self.__check_rval(rval)
        if rval == 0x01:
            raise ValueError("Bad JSON: %s" % pstr)
        elif rval == 0x02:
            raise ValueError("'%s' is not an actuator" % name)
        return rval

    def sense(self, name):
        print("Sense: %s" % name)

        data = name.encode('utf-8')
        resp = self.conn.send_recv(0xf9, data)
        if resp is None: raise RuntimeError("disconnected")
        rval = resp[0]
        self.__check_rval(rval)
        if rval == 0x01:
            raise ValueError("'%s' is not a sensor" % name)
        elif rval != 0:
            raise RuntimeError("Sense error: %d" % rval)
        else:
            return resp[1:]