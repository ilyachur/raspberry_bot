from common.command_daemon import function_result
from common.command_daemon import CommandDaemon
from common.gmail_worker import GMailWorker
from common import routines
import argparse
import sys
import os


def parcer_conf(conf_name):
    ret_hash = {}
    with open(conf_name, 'r') as f:
        for line in f:
            if len(line.split('=')) != 2:
                continue
            key, value = line.split('=')

            key = key.rstrip()
            key = key.lstrip()

            value = value.rstrip()
            value = value.lstrip()
            ret_hash[key] = value
    return ret_hash

if __name__=='__main__':
    os.chdir(os.path.abspath(os.path.dirname(sys.argv[0])))

    if not os.path.isdir('logs'):
        os.mkdir('logs')
    os.environ['LOGS_DIR'] = routines.process_path(os.getcwd()) + 'logs/'

    #os.environ['LOGS_LEVEL'] = 'CRITICAL'

    log = routines.Logger('main')
    log.delete()


    log.info('Parsing arguments...')
    parser = argparse.ArgumentParser()
    parser.add_argument('--conf', dest='conf', action='store', help='Configure file')
    args = parser.parse_args()
    if args.conf:
        log.info('Open config file from arguments %s...' % args.conf)
        cfg = parcer_conf(args.conf)
    elif os.path.exists('conf.cfg'):
        log.info('Open default config file conf.cfg from root directory...')
        cfg = parcer_conf('conf.cfg')
    else:
        log.critical('Error config file cannot be found!!!')
        exit('Error config file cannot be found!!!')

    # Processor temperature statistic thread
    ptd = CommandDaemon(command='/opt/vc/bin/vcgencmd measure_temp', timeout=30,db_name='./site/raspberry_site/db/db.sqlite3', db_function=function_result)
    log.info('Running processor temperature daemon...')
    ptd.setName('proc_temp')
    ptd.start()
    print('Running processor temperature daemon...')

    # Gmail client
    gmw = GMailWorker(username=cfg['bot_gmail_username'], password=cfg['bot_gmail_password'])
    log.info('Running gmail worker...')
    gmw.setName('gmail_worker')
    gmw.start()
    print('Running gmail worker...')

    while gmw.is_alive() or ptd.is_alive():
        pass