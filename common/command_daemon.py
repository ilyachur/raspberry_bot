from common.daemon import Daemon
from datetime import datetime
from common import routines
import sqlite3
import time

def function_result(db_name,  results):
    value = None
    for line in results[1].split('\n'):
        if len(line.split('=')) != 2:
            continue
        key, value = line.split('=')
        key = key.rstrip()
        key = key.lstrip()
        if key == 'temp':
            value = value.rstrip()
            value = value.lstrip()
            value = value.split('\'')[0]

    db = sqlite3.connect(db_name)
    db.execute('create table if not exists ' + 'proc_term232' + ' (datetime integer PRIMARY KEY, temperature integer)')

    now = datetime.now()
    if value is not None:
        db.execute('INSERT INTO ' + 'proc_temp' + '(datetime, temperature) VALUES(' + str(int(time.mktime(now.timetuple()))) + ', ' + str(value) + ')')

    db.commit()
    db.close()


class CommandDaemon(Daemon):

    def __init__(self, command, timeout=300, db_name=None, db_function=None, kill_time=None):
        Daemon.__init__(self, timeout, kill_time)
        self.command = command
        self.db_name = db_name
        self.db_function = db_function
        self.is_first = True
        #self.db.execute('create table if not exists ' + self.db_table + ' (datetime integer, temperature integer)')
        #self.db.commit()

    def stop_service(self):
        self.need_stop = True

    def run_processing(self):
        ret_info = routines.shell(self.command)
        if self.db_name:
            self.db_function(self.db_name, ret_info)
        else:
            if self.is_first and self.runner_log is not None:
                self.runner_log.info('DB function is not set!')
        self.is_first = False