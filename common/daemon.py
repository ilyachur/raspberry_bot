from abc import ABCMeta, abstractmethod
from datetime import datetime
from threading import Thread
from common import routines
import logging
import time

class Daemon(Thread):
    __metaclass__ = ABCMeta

    def __init__(self, timeout=300, kill_after_h=None):
        Thread.__init__(self)
        self.timeout = timeout
        self.need_stop = False
        self.kill_after_h = kill_after_h
        self.kill_time = None
        if self.kill_after_h is not None:
            now = datetime.now()
            self.kill_time = time.mktime(now.timetuple()) + self.kill_after_h*3600
        self.runner_log = None

    @abstractmethod
    def run_processing(self):
        pass

    def pre_run(self):
        pass

    def post_run(self):
        pass

    def stop_service(self):
        self.need_stop = True
        self.runner_log.info('Service will be stopped...')

    def run(self):
        self.runner_log = routines.Logger(self.getName())
        self.runner_log.info('Started thread with command: %s. Timeout is %s. Work time is %s h.' % (self.getName(),
                                                                                                str(self.timeout),
                                                                                                str(self.kill_after_h)))
        try:
            self.pre_run()
            while not self.need_stop:
                now = datetime.now()
                if self.kill_time is not None and time.mktime(now.timetuple()) > self.kill_time:
                    break
                self.run_processing()
                time.sleep(self.timeout)
            self.post_run()
        except Exception as e:
            self.runner_log.critical(str(e))