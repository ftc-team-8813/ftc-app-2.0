from robot_client import navigator, client
from util import log
import sys

LIFECYCLE_EVENT_INIT = 0
LIFECYCLE_EVENT_START = 1
LIFECYCLE_EVENT_STOP = 2

def run_auto(nav):
    pass

def main():
    log.redirect_output()
    if len(sys.argv) < 2:
        print("not enough arguments; expected socket file")
        sys.exit(1)

    conn = client.Connection(sys.argv[1])
    conn.connect()

    nav = navigator.Navigator(conn)
    nav.wait_event('org.firstinspires.ftc.teamcode.util.event.LifecycleEvent', LIFECYCLE_EVENT_START)
    run_auto(nav)

if __name__ == "__main__":
    main()
