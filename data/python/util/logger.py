import sys
import atexit
import time

def redirect_output(filename='/sdcard/Team8813/python.log'):
    logfile = open(filename, 'w')
    sys.stdout = logfile
    sys.stderr = logfile
    atexit.register(lambda: logfile.close())

start_time = None

def set_start_time():
    global start_time
    start_time = time.perf_counter()

class Logger:
    def __init__(self, tag):
        self.tag = tag

    def log(self, level, fmt, *args):
        message = fmt % args
        prefix = time.strftime('[%Y-%m-%d %H:%M:%S] ')
        if start_time is not None:
            prefix += '[%7.3fs] ' % (time.perf_counter() - start_time)
        prefix += '%s/%s: %s' % (self.tag, level, message)
        print(prefix)

    def v(self, fmt, *args):
        self.log('VERBOSE', fmt, *args)

    def d(self, fmt, *args):
        self.log('DEBUG', fmt, *args)

    def i(self, fmt, *args):
        self.log('INFO', fmt, *args)

    def w(self, fmt, *args):
        self.log('WARN', fmt, *args)

    def e(self, fmt, *args):
        self.log('ERROR', fmt, *args)

    def f(self, fmt, *args):
        self.log('FATAL', fmt, *args)
