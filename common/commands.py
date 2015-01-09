def get_status(src_list):
    line = """Status: good
    Work: Normal
    Temperature: 34 Grad"""
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