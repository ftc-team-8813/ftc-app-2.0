from robot_client import navigator, client
from util import logger
import sys
import time

LIFECYCLE_EVENT_INIT = 0
LIFECYCLE_EVENT_START = 1
LIFECYCLE_EVENT_STOP = 2

def place_wobble(nav):
    # nav.actuator('wobble', {'action': 'down'})
    # time.sleep(0.5)
    nav.actuator('wobble', {'action': 'open'})
    time.sleep(0.5)
    # nav.actuator('wobble', {'action': 'up'})
    # time.sleep(0.5)

def pick_wobble(nav):
    nav.actuator('wobble', {'action': 'open'}) # should be already open but make sure
    nav.actuator('wobble', {'action': 'down'})
    time.sleep(0.8)
    nav.actuator('wobble', {'action': 'close'})
    time.sleep(0.5)
    nav.actuator('wobble', {'action': 'up'})
    time.sleep(0.6)

def shoot_rings(nav, rings):
    for i in range(rings):
        nav.actuator('turret', {'action': 'push'})
        time.sleep(0.2)
        nav.actuator('turret', {'action': 'unpush'})
        time.sleep(0.5)

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

    # Wobble 1
    nav.move(-6,    0) # Move off the wall so we don't hit it
    if rings_seen != 0:
        # Go around the ring stack
        nav.move(-28, -12)

    if rings_seen == 0:
        nav.move(-59, -15)
        nav.turn(30)
    elif rings_seen == 1:
        nav.move(-87, 0)
        nav.turn(0)
    elif rings_seen == 4:
        nav.move(-111, -19)
        nav.turn(25)

    nav.actuator('shooter', {'action': 'start', 'speed': 0.66})
    place_wobble(nav)

    # Line up for shooting
    nav.move(-58, 1, reverse=False)
    nav.actuator('turret', {'action': 'rotate', 'angle': 0.165})
    nav.turn(90)
    time.sleep(1.5)

    shoot_rings(nav, 3)

    if rings_seen == 1:
        # Intake [experimental]
        nav.actuator('turret', {'action': 'home'})
        nav.actuator('intake', {'action': 'intake'})
        nav.turn(10)
        nav.move(-41, 5, reverse=False, speed=0.5)
        time.sleep(1)
        nav.actuator('intake', {'action': 'stop'})
        # nav.actuator('turret', {'action': 'home'})
        nav.move(-58, 1)
        nav.turn(0)
        shoot_rings(nav, 1)
        nav.turn(90)

    nav.actuator('shooter', {'action': 'stop'})

    if rings_seen == 4:
        # second wobble
        nav.move(-41, 24)
        nav.move(-25, 23)
        nav.turn(-195)
    else:
        # second wobble
        nav.move(-28.5, 16)
        nav.turn(192)
        time.sleep(0.75)

    pick_wobble(nav)

    if rings_seen == 0:
        nav.move(-55, -10)
        nav.turn(40)
    elif rings_seen == 1:
        nav.move(-80, -1)
        nav.turn(-9)
    elif rings_seen == 4:
        nav.move(-47, 22)
        nav.move(-103, -16)
        nav.turn(37)
    place_wobble(nav)

    if rings_seen != 0:
        nav.move(-76, -3, reverse=False)

    log.i("Path complete")

    # Return home
    nav.move(-6, 0, reverse=False)


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
