# NavPath Objects
*[last updated Jan 29 2021]*

The vast majority of the autonomous program is controlled by the `NavPath` class
in `hardware.navigation`. `NavPath` objects represent a sequence of actions that
are defined in a configuration JSON file, which is loaded from the robot's
internal storage.

### Creating a NavPath
Creating a `NavPath` requires several inputs:
* `File jsonFile`: The input file to load path data from. Use
  `Storage.getFile(path)` to get the full file path.
* `EventBus bus`: The OpMode's global `EventBus`
* `Scheduler scheduler`: A `Scheduler` connected to the above `EventBus`
* `Robot robot`: The global `Robot` instance.
* `JsonObject navConfig`: A `JsonObject` containing two keys:
  * `dist_kp` -- the proportional constant for forward movements
  * `angle_kp` -- the proportional constant for rotational movements

### Using the NavPath
Before running the path, any actuators/conditions (see below, `NavPath JSON Format`)
must be registered.

To register an actuator, use `addActuator(name, actuator)`. The `actuator` parameter
should be a lambda function that takes a single `JsonObject` containing the actuator
parameters and returns nothing.

To register a condition producer, use `addCondition(name, producer)`. The `producer`
parameter should be a lambda function that takes no parameters and returns a `double`
containing the value to compare.

Once all of the actuators and conditions have been registered, call `load()` to
read the path JSON.

After the file has been loaded, simply run `start()` (preferably during
OpMode `start`) to start the path. Run `loop(Telemetry, false)` during the
OpMode's `init_loop` to show telemetry without actually moving, and run
`loop(Telemetry, true)` during the OpMode `loop` to allow the `NavPath` to drive
the robot.

##### Utility functions
`getNumOrConstant(JsonElement elem)` takes a `JsonElement` that is either a
`String` or a number. If it is a number, the function returns that number.
Otherwise, if it is a `String` that names a constant defined in the JSON, it
returns that constant value. If the element is a `String` but is not a name of
a defined constant value (or `load()` has not been called), this function throws
an `IllegalArgumentException`.

`getConstant(String name)` returns a constant value from the JSON. If a constant
with the name specified by `name` does not exist (or `load()` has not been called),
this function throws an `IllegalArgumentException`.

`complete()` returns `true` if the path has been completed; `false` otherwise.

## NavPath JSON Format
The `NavPath` loads path data from a configuration JSON file. Unspecified keys
are allowed (and ignored by the loader), but if they are used, they should always
start with an `_` character for readability and future-compatibility. (i.e.,
comments in the file should be written as `"_comment": "..."`).

The root object contains a few important values:
* `"versionCode"`: The loader version that this file was written for. As of the
  writing of this document, this should be `"1.0.1"`. The loader is compatible
  with files with the same first two version numbers.
* `"defaultSpeed"`: The default speed that the robot executes forward moves at,
  unless otherwise specified. Generally, `0.5` is a good value.
* `"timers"`: *[Optional]* An object containing all the delay timers used throughout the path.
  Stored as `"name": delay` pairs.
* `"constants"`: *[Optional]* An object containing `"name": value` pairs. These
  names can then be used in place of direct numbers, but only when specified.
  *[New in version 1.0.1]*
* `"path"`: A JSON array containing *one or more* path entry objects

#### Path entry
* `"type"`: The path type; must be one of `"forward"`, `"turn"`, `"actuator"`, or `"nop"`.
  * `"forward"` paths move the robot forward some distance before running the next entry.
  * `"turn"` paths rotate the robot some angle before running the next entry.
  * `"actuator"` paths run a registered actuator with some specified parameters.
  * `"nop"` paths do nothing directly.  
* `"trigger"`: *[Optional]* When present, execution waits until the specified event is triggered.
  Must be an object with the following values:
  * `"class"`: The full name of the event class (i.e.
    `"org.firstinspires.ftc.teamcode.util.event.LifecycleEvent"`)
  * `"channel"`: *[If `"timer"` not present]* The event channel to listen for
  * `"timer"`: *[If `"channel"` not present]* The name of the timer to wait for.
    If this is present, the event class *must* be
    `"org.firstinspires.ftc.teamcode.util.event.TimerEvent"`. The timer will be
    started immediately after the previous event completes (or when the path is
    started). The timer name *must* be defined in the root object's `"timers"`.
* `"condition"`: *[Optional]* When present, the path checks the specified
  condition; if it evaluates to 'true', execution jumps to the entry at the specified
  index. Must be an object with the following values:
  * `"name"`: Name of a registered condition producer
  * `"cond"`: Must be one of [`"=="`, `"!="`, `"<"`, `">"`, `"<="`, `>=`]
  * `"value"`: Value to compare the result of the condition producer against.
    *Can be replaced by a constant*.
  * `"jumpTrue"`: The index of the path entry to execute next if the comparison
    evaluates to 'true'.
  * `"jumpFalse"`: *[Optional]* The index of the path entry to execute next if
    the comparison evaluates to 'false'.  
  * **Note**: It is recommended to register a `"0"` condition that always evaluates
    to `0` so that conditions can *always* jump execution to some specified position.
    For example, the following condition:
    ```js
    "condition": {
      "name": "0",
      "cond": "==",
      "value": 0,
      "jumpTrue": 0
    }
    ```
    could be used to create an infinite loop. *This may change in the future.*

##### `"forward"` path entries
If the path entry's type is `"forward"`, it must contain the following:

* `"dist"`: The distance to move forward, in encoder ticks.
  *Can be replaced by a constant*.
* `"speed"`: *[Optional]* The motor power; must be between 0 and 1. If not
  present, defaults to the `NavPath`'s `"defaultSpeed"`.
  *Can be replaced by a constant*
* `"absolute"`: *[Optional]* If present and `true`, *sets* the target position
  to `"dist"`, instead of adding it to the current target.
* ~~`"ensure"`~~: *[Optional]* *[Currently not implemented]* If present and `true`,
  take extra time to make sure that the robot's position is nearly exact.

##### `"turn"` path entries
If the path entry's type is `"turn"`, it must contain the following:

* `"rotation"`: The angle to turn, in degrees. *Can be replaced by a constant*.
* ~~`"speed"`~~: *[Optional]* *[Currently not implemented]* The motor power; must be
  between 0 and 1. *Currently hard-coded to 0.5*. (*Can be replaced by a constant*.)
* `"absolute"`: *[Optional]* If present and `true`, *sets* the target angle
  to `"rotation"`, instead of adding it to the current target.
* ~~`"ensure"`~~: *[Optional]* *[Currently not implemented]* If present and `true`,
  take extra time to make sure that the robot's position is nearly exact.

##### `"actuator"` path entries
If the path's entry type is `"actuator"`, it must contain the following:

* `"actuator"`: An object containing the following values:
  * `"name"`: The name of an actuator from the actuators list. An actuator with
    this name must have been added by `addActuator()` before `load()` is called.
  * `"params"`: An object containing parameters that are passed to the actuator.
    If the actuator takes no parameters, set `"params"` to `{}`.
    * **NOTE**: Numbers in `"params"` generally *cannot* be constant values,
      unless the actuator internally uses `NavPath.getNumOrConstant(elem)`. For
      example, to read a number or constant value from parameter `"foo"`, the actuator must use
      ```java
      double val = path.getNumOrConstant(params.get("foo"));
      ```
      instead of the more typical
      ```java
      double val = params.get("foo").getAsDouble();
      ```

##### `"nop"` path entries
`"nop"` entries have no additional properties. Instead, they are intended to be
used along with the `"trigger"` and/or `"condition"` for flow control and delays.
