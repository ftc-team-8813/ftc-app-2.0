from robot_client import client
import sys
import time
import atexit

def main():
    logfile = open('python.log', 'w')
    sys.stdout = logfile
    sys.stderr = logfile
    atexit.register(lambda: logfile.close())

    if len(sys.argv) < 2:
        sys.exit(1)
    conn = client.Connection(sys.argv[1])
    conn.connect()
    print("Hello world; connected to server!")

    last_ping = -1
    while True:
        s = 'Last Echo Time=%.3fs' % last_ping
        start = time.perf_counter()
        r = conn.send_recv(0x01, s.encode('utf-8'))
        if r is None: break

        last_ping = time.perf_counter() - start
        time.sleep(0.5)

if __name__ == "__main__":
    main()