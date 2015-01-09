from common import routines

def get_status(src_list):
    line = ''
    ret_info = routines.shell("/opt/vc/bin/vcgencmd measure_temp")
    line += ret_info[1].decode("utf-8") + '\n'
    ret_info = routines.shell("/opt/vc/bin/vcgencmd pm_get_status")
    for param in ret_info[1].decode("utf-8").split(' '):
        line += param + '\n'
    ret_info = routines.shell("df -h")
    line += ret_info[1].decode("utf-8") + '\n'


    ret_info = routines.shell("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq")
    line += "CPU_FREQ_MIN=" + ret_info[1].decode("utf-8").lstrip().rstrip() + " KHz" + '\n'
    ret_info = routines.shell("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")
    line += "CPU_FREQ_MAX=" + ret_info[1].decode("utf-8").lstrip().rstrip() + " KHz" + '\n'
    ret_info = routines.shell("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")
    line += "CPU_FREQ_CUR=" + ret_info[1].decode("utf-8").lstrip().rstrip() + " KHz" + '\n'


    ret_info = routines.shell("free -o -h")
    line += ret_info[1].decode("utf-8") + '\n'

    ret_info = routines.shell("/opt/vc/bin/vcgencmd get_mem gpu  | sed 's/[A-Za-z]*//g' | cut -c 2-")
    line += "GPU_MEM=" + ret_info[1].decode("utf-8").lstrip().rstrip() + " Mb" + '\n'
    return line

commands_list = {
    'power': '',
    'get_status': get_status,
}

def send_command(command, value):
    output = ""
    if command not in commands_list:
        output += "Command %s is not found!" % command
    else:
        print "Command %s with value %s" % (command, str(value))
        try:
            ret_val = commands_list[command](value)
            if ret_val is not None:
                output += '\n' + str(ret_val)
        except Exception as e:
            output += '\n' + str(e)
            print(str(e))
    if output == "":
        return None
    return output