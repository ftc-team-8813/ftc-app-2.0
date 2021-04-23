from robot_client import navigator, client
from util import logger
import sys
import time

LIFECYCLE_EVENT_INIT = 0
LIFECYCLE_EVENT_START = 1
LIFECYCLE_EVENT_STOP = 2

SHOOT_SPEED = 0.7225

def place_wobble(nav, dropit=False):
    if not dropit:
        nav.actuator('wobble', {'action': 'down'})
        time.sleep(0.5)

    nav.actuator('wobble', {'action': 'open'})
    time.sleep(0.5)

    if not dropit:
        nav.actuator('wobble', {'action': 'up'})
        time.sleep(0.5)

def pick_wobble(nav):
    nav.actuator('wobble', {'action': 'open'}) # should be already open but make sure
    nav.actuator('wobble', {'action': 'down'})
    time.sleep(0.8)
    nav.actuator('wobble', {'action': 'close'})
    time.sleep(0.5)
    nav.actuator('wobble', {'action': 'mid'})
    time.sleep(0.6)

def shoot_rings(nav, rings):
    for i in range(rings):
        nav.actuator('turret', {'action': 'push'})
        time.sleep(0.2)
        nav.actuator('turret', {'action': 'unpush'})
        time.sleep(0.4)

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
        nav.move(-84, 0)
        nav.turn(0)
    elif rings_seen == 4:
        nav.move(-105, -13, speed=0.9)
        nav.turn(25)

    nav.actuator('shooter', {'action': 'start', 'speed': SHOOT_SPEED})
    place_wobble(nav)

    if rings_seen == 4:
        nav.move(-60, 0, reverse=False, speed=0.9)

    # Line up for shooting
    nav.move(-58.5, 0, reverse=False)
    nav.actuator('turret', {'action': 'rotate', 'angle': 0.16})
    nav.turn(90)
    if rings_seen == 0:
        time.sleep(3)
    else:
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
        nav.move(-58.5, 1)
        nav.turn(3)
        shoot_rings(nav, 1)
        nav.turn(90)
    elif rings_seen == 4:
        nav.actuator('turret', {'action': 'home'})
        # nav.turn(-3)
        nav.actuator('intake', {'action': 'intake', 'speed': -0.75})
        nav.move(-46, 5, reverse=False, speed=0.85)
        nav.actuator('intake', {'action': 'intake', 'speed': 0.82})
        time.sleep(0.15)
        nav.move(-37, 12, reverse=False, speed=0.45)
        time.sleep(1)
        nav.move(-58, 2)
        nav.turn(0)
        nav.actuator('intake', {'action': 'stop'})
        shoot_rings(nav, 3)
        nav.actuator('wobble', {'action': 'down'})

    nav.actuator('shooter', {'action': 'stop'})

    #if rings_seen == 4:
        # second wobble
        # nav.move(-41, 26)
        #nav.move(-27.5, 16)
        #nav.turn(-195)
    #else:
        # second wobble
    if rings_seen == 0 or rings_seen == 1:
        nav.move(-27.5, 17)
        nav.turn(192)
        time.sleep(0.75)
    elif rings_seen == 4:
        nav.move(-27, 18)
        time.sleep(0.25)

    pick_wobble(nav)

    if rings_seen == 0:
        nav.move(-55, -10)
        nav.turn(50)
    elif rings_seen == 1:
        nav.move(-74, -1)
        nav.turn(-9)
    elif rings_seen == 4:
        # nav.move(-47, 18)
        nav.move(-103, -13, speed=0.9)
        # nav.turn(-323)
    place_wobble(nav)

    nav.move(-76, 0, reverse=(rings_seen != 4))

    log.i("Path complete")

    # Return home
    # nav.move(-6, 0, reverse=False)


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
