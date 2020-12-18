#!/usr/bin/env python3
import os
import shlex
import subprocess

# class ServoPositionProcessor:
#     def __init__(self):
#         self.positions = {}
#
#     def process(self, fname, line, name, params):
#         if len(params) < 2:
#             print("ERROR: Two parameters required: <servo> <name>")
#             return
#         servo = params[0]
#         name = params[1]
#         if not servo in self.positions:
#             self.positions[servo] = []
#         self.positions[servo].append(name)
#
#     def finish(self, adb):
#         with open('build/preset_names.txt', 'w') as f:
#             for servo in self.positions.keys():
#                 servo_esc = servo.replace(' ', '\\ ')
#                 f.write(servo_esc + ':')
#                 i = 0
#                 for pos in self.positions[servo]:
#                     f.write(pos)
#                     if i < len(self.positions[servo]) - 1:
#                         f.write(',')
#                     i += 1
#                 f.write('\n')
#         if len(adb.get_devices()) == 0:
#             print("No devices connected; not uploading servo positions")
#             return
#
#         adb.push_file('build/preset_names.txt', '/sdcard/Team8813/')

class DataFileProcessor:
    def finish(self, adb):
        if len(adb.get_devices()) == 0:
            print("No devices connected; not uploading servo positions")
            return
        for f in os.listdir('../data/'):
            adb.push_file('../data/' + f, '/sdcard/Team8813/')

class Adb:
    def __init__(self, command='adb'):
        self.command = command

    def send_command(self, command, read_stdout=True):
        cmdline = [self.command]
        cmdline.extend(command)
        print(cmdline)
        if read_stdout:
            stdout = subprocess.run(cmdline, stdout=subprocess.PIPE).stdout
            return stdout.decode('utf-8').split('\n')
        else:
            subprocess.run(cmdline)

    def get_devices(self):
        out = self.send_command(['devices'])
        return out[2:len(out)-1]

    def push_file(self, src, dest):
        self.send_command(['push', src, dest], False)

    def pull_file(self, src, dest):
        self.send_command(['pull', src, dest], False)


class Processor:
    def process(self, fname, line, name, params):
        pass

    def finish(self, adb):
        pass

def checkFile(file, processors):
    with open(file, 'r') as f:
        lineno = 0
        for line in f:
            lineno += 1   # File lines appear to start at 1
            line = line.strip()
            if line.startswith("//#"):
                split = shlex.split(line)
                name = split[0][3:]
                params = split[1:]
                if name not in processors.keys():
                    print("WARNING: Processor not found for %s (line=%d in %s)" % (name, lineno, file))
                    continue

                processors[name].process(file, lineno, name, params)

def main():
    print("Running custom file processors")
    # Register processors here
    processors = {
        # "position": ServoPositionProcessor()
        "!!!!data": DataFileProcessor()
    }


    for root, dirs, files in os.walk('src/main/java/'):
        for file in files:
            if file.endswith('.java'):
                checkFile(root + '/' + file, processors)

    adb = Adb('adb')
    for processor in processors.values():
        processor.finish(adb)

if __name__ == "__main__":
    main()