# Events
*[last updated Jan 17 2021]*

Events are created by various classes in order to notify the rest of the
program that something happened. All events must be subclasses of
`util.event.Event`, which gives them a constant `channel` variable and a
`suppressDebug` flag to disable debug logging for frequently-issued events.

### Channels
Every event has a `channel`, which allows the same event class to be used for
multiple distinct event triggers. For example, one could set up a timer that
sends `TimerEvent`s on channel `0` every second and another timer that sends
`TimerEvent`s on channel `1` every two seconds. Even though both timers send
`TimerEvent` objects, the different channels allow them to be used
simultaneously without conflicts.

Event channels are either unused (always set to `0`), pre-defined
(i.e. as different functions of the same actuator), or dynamically allocated
(see `util.event.TimerEvent`).

### Current List of Event Classes
**Hardware Events -- `hardware.events`**
* `AngleHoldEvent` -- triggered by `hardware.navigation.AngleHold`:
  * Channel `0` -- `HOLD_INITIALIZED`: IMU initialization/reset completed
  * Channel `1` -- `TARGET_REACHED`: Target heading (when set by
    `setTarget(double)`) reached
* `IMUEvent` -- triggered by `hardware.IMU` when its state changes. Holds two
  constant values, `old_state` and `new_state`.
  * Channel `0` (default): Sent for all `IMUEvent`s
* `LiftEvent` -- triggered by `hardware.SimpleLift` when a lift movement is completed.
  Holds a single flag, `up`, which shows the current lift state that has been reached.
  * Channel `0` -- `LIFT_MOVED`: Sent for all `LiftEvent`s
* `NavMoveEvent` -- used by `hardware.navigation.NavPath` to wait for the completion of
  forward/turn drivetrain movements:
  * Channel `0` -- `FORWARD_COMPLETE`: Sent when a forward move has finished
  * Channel `1` -- `TURN_COMPLETE`: Sent when a turn has finished (synonymous
    with `AngleHoldEvent`'s `TARGET_REACHED`)
  * Channel `2` -- `NAVIGATION_COMPLETE`: Sent when the entire `NavPath` has finished.
* `TurretEvent` -- used by `hardware.Turret` when a rotation is complete
  * Channel `0` -- `TURRET_MOVED`: sent after `rotate(position, true)` has been called
    and the turret has reached that target position.

**Utility Events -- `util.event`**
* `LifecycleEvent` -- Can be sent by OpModes at each program stage
  * Channel `0` -- `INIT`: To be sent when the OpMode's `init()` function is called
  * Channel `1` -- `START`: To be sent when the OpMode's `start()` function is called
  * Channel `2` -- `STOP`: To be sent when the OpMode's `stop()` function is called
* `TimerEvent` -- Sent by `util.Scheduler` timers. Event channels are dynamically
  allocated by the `Scheduler` for each `Timer` created, and the channel to use
  for each `Timer` is contained in its `eventChannel` field. Each `TimerEvent` also
  contains a `time` field, which is the time (in seconds) when the event was issued.   
  `TimerEvent`s created by repeating timers have `suppressDebug` set.
* `TriggerEvent` -- A general purpose event class. *This is deprecated*; creating
  a new event class specific to each application is much preferred.

**Miscellaneous Events**
* `vision.webcam.WebcamEvent` -- Sent by the `Webcam` when its state changes (and
  its `loop(EventBus)` function is called). The event channel corresponds to the state:
  * Channel `0` -- `NEVER_OPENED`: The camera hasn't been opened
  * Channel `1` -- `OPENING`: The camera is in the process of initializing
  * Channel `2` -- `OPENED`: The camera has initialized, but hasn't started streaming yet
  * Channel `3` -- `RUNNING`: The camera is streaming
  * Channel `-1` -- `CLOSED`: The camera has been closed normally
  * Channel `-2` -- `ERROR`: The camera has been closed due to an error. In this
    case, the `error` variable contains information about what happened.
