from datetime import datetime
from threading import Thread
#from common import routines
import routines
import sqlite3


def functon_time(db, results):
    db.execute('create table if not exists ' + 'proc_temp' + ' ("datetime" integer PRIMARY KEY, temperature integer)')
    now = datetime.now()
    db.execute('INSERT INTO ' + 'proc_temp' + '(datetime, temperature) VALUES(' + str(int(time.mktime(now.timetuple()))) + ', ' + str(2) + ')')
    db.commit()

class CommandDaemon(Thread):

    def __init__(self, command, timeout=300, db_name=None, db_function=None):
        Thread.__init__(self)
        self.timeout = timeout
        self.command = command
        self.db = None
        self.db_function = db_function
        if db_name is not None:
            self.db = sqlite3.connect(db_name)
        #self.db.execute('create table if not exists ' + self.db_table + ' (datetime integer, temperature integer)')
        #self.db.commit()

    def __del__(self):
        self.db.close()

    def run(self):
        if self.db_function:
            ret_info = routines.shell('/opt/vc/bin/vcgencmd measure_temp')
            print ret_info[1]
            for line in ret_info[1].split('\n'):
                if len(line.split('=')) != 2:
                    continue
                key, value = line.split('=')
                key = key.rstrip()
                key = key.lstrip()
                print key
                print value.split('\'')[0]
            #self.db_function(self.db, ret_info[2])

import time
if __name__=='__main__':
    tpd = CommandDaemon('cmd', db_name='../site/raspberry_site/db/db.sqlite3', db_function=functon_time)
    tpd.run()

    while tpd.is_alive():
        pass
