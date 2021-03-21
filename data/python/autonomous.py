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

    conn = client.Connection(sys.argv[0])
    conn.connect()

    nav = navigator.Navigator(conn)
    nav.wait_event('org.firstinspires.ftc.teamcode.util.event.LifecycleEvent', LIFECYCLE_EVENT_START)
    run_auto(nav)

if __name__ == "__main__":
    main()
