import sys
import atexit

def redirect_output(filename='python.log'):
    logfile = open(filename, 'w')
    sys.stdout = logfile
    sys.stderr = logfile
    atexit.register(lambda: logfile.close())