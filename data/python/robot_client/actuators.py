import struct
# very WIP

class DcMotor:
    packet_id = 0x02
    RUN_WITHOUT_ENCODER    = 0
    RUN_USING_ENCODER      = 1
    RUN_TO_POSITION        = 2
    STOP_AND_RESET_ENCODER = 3

    ZERO_POWER_UNKNOWN     = 0
    ZERO_POWER_BRAKE       = 1
    ZERO_POWER_FLOAT       = 2

    DIRECTION_FORWARD      = 0
    DIRECTION_REVERSE      = 1

    def __init__(self, port):
        self.__port = port
        self.__updated = False
        self.__modified = False
        self.__run_mode = 0
        self.__zero_behavior = 0
        self.__target_position = 0
        self.__busy = False
        self.__current_position = 0
        self.__direction = 0

    def update(self, conn):
        # Send updates if modified
        if self.__modified:
            send_packet = struct.pack('>BB')


