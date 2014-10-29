import subprocess
import sys

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