import re
import subprocess


def get_cpu_info():
    m = re.search("model name\s+: (.+)", subprocess.Popen(["cat", "/proc/cpuinfo"], stdout=subprocess.PIPE).communicate()[0])
    if m:
        return m.groups()[0]
    return None