from datetime import datetime
import subprocess
import sys
import os

LOG_LEVELS = {
    'ALL':   0,
    'TRACE': 1,
    'DEBUG': 2,
    'INFO':  3,
    'WARN':  4,
    'ERROR': 5,
    'CRITICAL': 6,
}

class Logger():
    def __init__(self, log_name):
        self.log_name = log_name
        if 'LOGS_DIR' in os.environ:
            self.base_path = process_path(os.environ['LOGS_DIR'])
        else:
            self.base_path = process_path(os.getcwd()) + 'logs/'
        if not os.path.isdir(self.base_path):
            os.mkdir(self.base_path)

        if 'LOGS_LEVEL' in os.environ:
            self.log_level_priority = os.environ['LOGS_LEVEL']
        else:
            self.log_level_priority = 'INFO'

    def set_level_priority(self, name):
        global LOG_LEVELS
        if name not in LOG_LEVELS:
            self.log_level_priority = 'ALL'
        else:
            if LOG_LEVELS[name] > LOG_LEVELS['CRITICAL']:
                self.log_level_priority = 'CRITICAL'
            else:
                self.log_level_priority = name

    def is_writable(self, name):
        global LOG_LEVELS
        if name not in LOG_LEVELS:
            return True
        else:
            if LOG_LEVELS[name] >= LOG_LEVELS[self.log_level_priority]:
                return True
        return False

    def is_level(self, name):
        global LOG_LEVELS
        if name not in LOG_LEVELS:
            return False
        return True

    def add_new_level(self, name, priority):
        global LOG_LEVELS
        if name not in LOG_LEVELS:
            LOG_LEVELS[name] = priority
        else:
            self.warning('Log level %s already exists!')

    def delete(self):
        if os.path.exists(self.base_path + self.log_name + '.log'):
            os.unlink(self.base_path + self.log_name + '.log')

    def write(self, log_level, msg):
        if not self.is_writable(str(log_level)):
            return
        if os.path.exists(self.base_path + self.log_name + '.log'):
            log_file = open(self.base_path + self.log_name + '.log', 'a', 0)
        else:
            log_file = open(self.base_path + self.log_name + '.log', 'w', 0)
        now = datetime.now()

        log_file.write(str(now) + ': ' + str(log_level) + ': ' + str(msg) + '\n')
        log_file.close()

    def trace(self, msg):
        self.write('TRACE', msg)

    def debug(self, msg):
        self.write('DEBUG', msg)

    def info(self, msg):
        self.write('INFO', msg)

    def warning(self, msg):
        self.write('WARN', msg)

    def error(self, msg):
        self.write('ERROR', msg)

    def critical(self, msg):
        self.write('CRITICAL', msg)


def shell(cmd, env=None):
    """Execute a command.

    return (return code, stderr, stdout)
    """
    #print(cmd)
    if sys.platform.startswith('linux') or sys.platform == 'darwin':
        p = subprocess.Popen(['/bin/bash', '-c', cmd], env=env, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    else:
        p = subprocess.Popen(cmd, env=env, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    (stdin, stdout) = p.communicate()
    return p.returncode, stdin, stdout

## Function for getting normal path
#  @param path - path
#  @return normal path
def process_path(path):
    if len(path) > 1:
        if path[-1:] == '/':
            return path
        else:
            return path + '/'
    else:
        return ''