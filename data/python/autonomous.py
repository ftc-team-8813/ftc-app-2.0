from robot_client import navigator, client
from util import logger
import sys
import time

LIFECYCLE_EVENT_INIT = 0
LIFECYCLE_EVENT_START = 1
LIFECYCLE_EVENT_STOP = 2

def place_wobble(nav):
    nav.actuator('wobble', {'action': 'down'})
    time.sleep(0.5)
    nav.actuator('wobble', {'action': 'open'})
    time.sleep(0.5)
    nav.actuator('wobble', {'action': 'up'})
    time.sleep(0.5)

def shoot_rings(nav, rings):
    for i in range(rings):
        nav.actuator('turret', {'action': 'push'})
        time.sleep(0.15)
        nav.actuator('turret', {'action': 'unpush'})
        time.sleep(0.25)

def run_auto(nav):
    log = logger.Logger('Autonomous')
    log.i("Starting autonomous path")
    nav.default_speed = 0.65

    nav.actuator('turret', {'action': 'home'})

    # Request camera data
    nav.actuator('webcamDetect', {})
    time.sleep(0.5)
    data = nav.sense('ringsSeen')
    if len(data) < 1:
        log.e("No ring data!")
        return

    rings_seen = data[0]
    log.d("Got ring data: %d", rings_seen)

    # Step 1: First wobble
    if rings_seen == 0:
        nav.move(-56, 3, reverse=True)
        nav.turn(25.7)
    elif rings_seen == 1:
        nav.move(-89, -5, reverse=True)
        nav.turn(-5)
    elif rings_seen == 4:
        nav.move(-112, -3.3, reverse=True)
        nav.turn(13)
    place_wobble(nav)

    # Start shooter
    nav.actuator('shooter', {'action': 'start'})

    # Step 2: Go to shoot position
    nav.move(-56, 18)
    nav.turn(0)

    # Shoot 3 rings
    shoot_rings(nav, 3)

    # Move back
    if rings_seen > 0:
        nav.actuator('intake', {'action': 'intake'})
        time.sleep(0.5)
        if rings_seen == 4:
            nav.move(-24, 0, absolute=False, speed=0.3)
        else:
            nav.move(-18, 0, absolute=False, speed=0.3)

        # Move forward
        nav.move(-56, 18)
        nav.turn(0)

        if rings_seen == 1:
            shoot_rings(nav, 1)
        else:
            shoot_rings(nav, 3)


def main():
    logger.redirect_output()
    if len(sys.argv) < 2:
        print("not enough arguments; expected socket file")
        sys.exit(1)

    conn = client.Connection(sys.argv[1])
    conn.connect()

    nav = navigator.NavControl(conn)
    nav.wait_event('org.firstinspires.ftc.teamcode.util.event.LifecycleEvent', LIFECYCLE_EVENT_START)
    logger.set_start_time()
    run_auto(nav)

if __name__ == "__main__":
    main()
