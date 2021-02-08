# Hardware Classes
*[last updated Jan 18 2021]*

The `hardware` package contains many classes that provide additional or combined
functionality on top of the SDK hardware devices. The `Robot` class initializes
all of the hardware devices and contains instances of all of the other hardware
classes.

---

### IMU
The `IMU` class is the largest and, arguably, most important hardware class. It
wraps the SDK's `BNO055IMU` hardware device and provides some additional
functionality:

* Continuous polling and wraparound counting -- The `BNO055IMU` outputs angles
  between -180 and 180 degrees; this class makes the range fully continuous.
* Automatic calibration and status management -- This class automatically checks
  for an `imu_calibration.json` file; if it is not present, it runs the IMU's
  automatic calibration sequence and saves the result calibration file for future
  use.

#### Usage
Creating an `IMU` object only requires the `BNO055IMU` object, which can be found in
the hardware map (typically named "imu"). To initialize and start the IMU, run
the following statements:
```java
imu.setImmediateStart(true);
imu.initialize(evBus, scheduler);
```
Once the IMU is started, it will report an undefined angle. The following piece
of code waits for 0.5 seconds after the IMU starts, and then resets the IMU heading:
```java
Scheduler.Timer resetTimer = scheduler.addPendingTrigger(0.5, "Reset Delay");
evBus.subscribe(IMUEvent.class, (ev, bus, sub) -> {
    if (ev.new_state == IMU.STARTED)
        resetTimer.reset();
}, "Reset Heading -- Delay", 0);
evBus.subscribe(TimerEvent.class, (ev, bus, sub) -> {
    imu.resetHeading();
    resetFinished = true;
    // perhaps push an initialization-finished event here
}, "Reset Heading", resetTimer.eventChannel);
```

Once the IMU is set up and running, the `getHeading()`, `getRoll()`, and `getPitch()`
functions can be used to measure the current orientation of the robot. `resetHeading()`
can be used at any time to reset the heading, if necessary.

#### Method summary
* `IMU(BNO055IMU imu)` -- Create an `IMU` object that wraps the given `BNO055IMU`.
* `BNO055IMU getInternalImu()` -- Returns the internal `BNO055IMU` that this `IMU`
  object wraps.
* `void initialize(EventBus evBus, Scheduler scheduler)` -- Initialize the `IMU`.
  Uses the `Scheduler` to create a 20Hz update loop. The `IMU` will send `IMUEvent`s
  on the `EventBus` to notify other code of state changes.
* `void setImmediateStart(boolean immediateStart)` -- If this is called and set
  to `true` before `initialize()` is called, the `IMU` will automatically start
  running. By default, the `IMU` stops in the `INITIALIZED` state until `start()`
  is called manually.
* `void start()` -- Set the `IMU` state to `STARTED`. Using `setImmediateStart(true)`
  before calling `initialize()` is preferred over using this method.
* `int getStatus()` -- Get the `IMU`'s state. If it is negative, the IMU has been
  closed.
* `String getStatusString()` -- Get the `IMU`'s state as a human-readable `String`.
* `String getDetailStatus()` -- Get a human-readable `String` showing the details
  of the current state, i.e. the calibration stage or the error message.
* `double getHeading()` -- Get the heading, in degrees counterclockwise. Returns
  `0` if the `IMU` has not been initialized yet.
* `double getRoll()` -- Get the roll angle, in degrees. Returns
  `0` if the `IMU` has not been initialized yet.
* `double getPitch()` -- Get the pitch angle, in degrees. Returns
  `0` if the `IMU` has not been initialized yet.
* `void resetHeading()` -- Reset the `IMU` heading to 0
* `void stop()` -- Stop the `IMU`. This is unnecessary, as the loop will stop
  automatically when the `Scheduler` stops.

---

### Turret
The `Turret` class manages all of the hardware attached to the turret. Its main
function, however, is to control the turret's rotation.

#### Usage
The `Turret` is created by the `Robot`; it can be accessed via `Robot.turret`.
If there is an `EventBus` available, it *must* be connected to the turret via the
`connectEventBus()` function in order to receive `TurretEvent`s. Once it is set
up, the `Turret`'s `loop()` function must be called during the `OpMode`'s `loop()`.

#### Configuration
The `Turret` uses three parts of the global `config.json` file:
* `"shooter"` -- Configuration data for the shooter. See `Shooter#Configuration`
  for details.
* `"turret_cal"` -- Calibration data for the turret potentiometer. See
  `CalibratedAnalogInput` for details.
* `"turret"` -- Configuration data for the turret:
  * `"home"` -- The home position (potentiometer value between 0 and 1)
  * `"home2"` -- The secondary home position (potentiometer value between 0 and 1)
  * `"kp"` -- The turret control system proportional constant. `15` has been found
    to work best here.
  * `"min"` -- The minimum point in the turret's allowed travel (potentiometer value
    between 0 and 1)
  * `"max"` -- The maximum point in the turret's allowed travel (potentiometer value
    between 0 and 1)
  * `"maxSpeed"` -- The maximum power that can be achieved by the turret motor
    when it is moving.
  * `"pusher"` -- A JSON object containing pusher positions:
    * `"in"` -- The 'resting' position (servo position between 0 and 1)
    * `"out"` -- the 'pushing' position (servo position between 0 and 1)

#### Method Summary
* `void connectEventBus(EventBus bus)` -- Connect an `EventBus` to the `Turret`.
  This allows it to send `TurretEvent`s on that `EventBus`.
* `void rotate(double position)` -- Set the target position. Does not send a
  `TurretEvent` when the position has been reached.
* `void rotate(double position, boolean sendEvent)` -- Set the target position.
  If `sendEvent` is true, sends a `TurretEvent` when the position is reached.
* `void home()` -- Move to the nearest home position. Sends a `TurretEvent` when
  the position has been reached.
* `double getTarget()` -- Get the current target position.
* `double getPosition()` -- Get the current position (as of the last call to `update()`).
* `void update(Telemetry telemetry)` -- Update the turret control system. This
  should be called repeatedly as quickly as possible to make sure the turret is
  responsive. Displays debug info on the passed `Telemetry` object.
* `void push()` -- Move the pusher servo to the 'out' position.
* `void unpush()` -- Move the pusher servo to the 'in' position.

---

### Shooter
This class speed-controls the shooter motor. It is created by the `Turret`, which
passes it configuration data and a `DcMotor`.

#### Configuration
* `"rampTime"` -- time interval over which the power will be increased from 0 to
  `maxPower`.
* `"maxPower"` -- the maximum power to achieve
* `"powershots"` -- a list of pre-defined power levels; used for accurately aiming
  at the power-shot targets in autonomous.

#### Method summary
* `void start()` -- Start the shooter motor. Does nothing if the shooter is
  already running.
* `void stop()` -- Stop the shooter motor.
* `void update()` -- Update the shooter motor speed. Called by `Turret.update()`.
* `void powershot(int index)` -- Set the maximum power to one of the pre-defined
  'power-shot' levels. If `index` is negative, sets the maximum power to the
  default.

---

### Drivetrain
Manages all four drive motors so that external code doesn't have to manage them
externally. Instantiated by the `Robot`; can be accessed through `Robot.drivetrain`

**NOTE**: Uses `RUN_WITHOUT_ENCODER` mode on the motors.

#### Method summary
* `void resetEncoders()` -- Reset all of the motor encoders. This should be
  called on `OpMode` `init()` and/or `start()`
* `void telemove(double left_stick_y, double right_stick_y)` -- Controls the
  drivetrain with the provided joystick inputs. Assumes the joystick values have
  been inverted and scaled properly, as in:
  ```java
  drivetrain.telemove(-gamepad1.left_stick_y * speed, -gamepad1.right_stick_y * speed);
  ```
  The `left_stick_y` input controls forward/backward movement, with positive values
  resulting in forward movement.  
  The `right_stick_y` input controls turning, with positive values resulting in
  counter-clockwise rotation about the robot's center.

---

### Intake
Controls the intake and ramp motors. Instantiated from the robot; can be accessed
via `Robot.intake`.

#### Method summary
* `void intake()` -- Run the intake and ramp *in* at 100% power.
* `void outtake()` -- Run the intake and ramp *out* at 100% power.
* `void run(double speed)` -- Run the intake and ramp at the given power. Positive
  values correspond to *inward* movement, and negative values correspond to *outward*
  movement.
* `void stop()` -- Stop the intake/ramp (set the power to 0).

---

### Wobble
Controls the wobble mechanism. Instantiated from the robot; can be accessed via
`Robot.wobble`.

#### Configuration
Uses the `"wobble"` JSON object in `configuration.json`.
* `"arm_up"`: The 'up' position.
* `"arm_down"`: The 'down' position.
* `"arm_mid"`: The 'mid' position -- should clear the field wall but slope downward.
* `"claw_open"`: The 'open' claw position.
* `"claw_close"`: The 'closed' claw position.

#### Method summary
* `void up()` -- move the arm to the 'up' position.
* `void down()` -- move the arm to the 'down' position.
* `void open()` -- move the claw to the 'open' position.
* `void close()` -- move the claw to the 'closed' position.
* `void middle()` -- move the arm to the 'mid' position.

---

### CalibratedAnalogInput
Wraps the `AnalogInput` and applies an experimentally-obtained linearization curve
to remove unwanted ADC impedance effects. Takes a JSON object containing two arrays,
`"xValues"` and `"yValues"` and finds the analog input's position along the `"yValues"`
curve in terms of the `"xValues"` curve.

*[perhaps I should have an image here]*

#### Method summary
* `void get()` -- Read and return the linearized input value. Reading and calculating
  this value is relatively expensive, so it should only be computed once per loop
  cycle.
