import socket
import struct

def create_packet(cmd, data):
    if len(data) > 65535:
        raise ValueError("Data too large: %d bytes" % len(data))
    if cmd < 0 or cmd > 255:
        raise ValueError("Command must be between 0 and 255")
    head = bytes([
        cmd,
        (len(data) >> 8) & 0xFF,
        len(data) & 0xFF
    ])
    return head + bytes(data)

class Connection:
    def __init__(self, address):
        self.sock = socket.socket()
        self.address = address
        self.connected = False

    def connect(self):
        print("Attempting to connect to %s" % self.address)
        self.sock = socket.socket(socket.AF_UNIX)
        self.sock.connect(self.address)
        self.connected = True

    def recv(self, size):
        data = b''
        # print("Recv %d bytes" % size)
        while len(data) < size:
            chunksize = min(1024, size - len(data))
            chunk = self.sock.recv(chunksize)
            # print("<- %d bytes" % len(chunk))
            if len(chunk) == 0:
                print("[connection lost]")
                self.connected = False
                return None
            data += chunk
        return data

    def recv_packet(self):
        head = self.recv(3)
        if head is None: return None
        resp = head[0]
        payload_size = (head[1]<<8) | head[2]
        if payload_size > 0:
            payload = self.recv(payload_size)
            if payload is None: return None
        else:
            payload = b''
        return (resp, payload)

    def send_recv(self, cmd, data=b''):
        # TODO this should be a separate send_packet function for consistency
        packet = create_packet(cmd, data)
        n = 0
        # print("Send %d bytes" % len(packet))
        while n < len(packet):
            try:
                nsent = self.sock.send(packet[n:])
            except BrokenPipeError:
                print("[connection lost]")
                self.connected = False
                return None
            # print("-> %d bytes" % nsent)
            if nsent == 0:
                print("[connection lost]")
                self.connected = False
                return None
            n += nsent
        # print("Receive response")
        resp_packet = self.recv_packet()
        if resp_packet is None: return None
        resp, rdata = resp_packet
        if resp == 0xFE: # LONG response
            npackets = rdata[0]
            # print("Got LONG response (%d packets)" % npackets)
            resp = rdata[1] # overwrite response with actual response
            longdata = b''
            for i in range(npackets):
                packet = self.recv_packet()
                if packet is None: return None
                ct, data = packet
                if ct != i:
                    print("Insanity: got packet #%d when expecting #%d" % (ct, i))
                    return None
                longdata += data
            return longdata
        else:
            return rdata

    def close(self):
        if self.connected:
            self.send_recv(0xFF)
            self.sock.close()
