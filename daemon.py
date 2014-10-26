from common.gmail_worker import GMailWorker
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
    if len(sys.argv) > 1 and os.path.exists(sys.argv[1]):
        cfg = parcer_conf(sys.argv[1])
    elif os.path.exists('conf.cfg'):
        cfg = parcer_conf('conf.cfg')
    else:
        exit('Error config file cannot be found!!!')

    gmw = GMailWorker(username=cfg['bot_gmail_username'], password=cfg['bot_gmail_password'])
    gmw.run()

    while gmw.is_alive():
        pass