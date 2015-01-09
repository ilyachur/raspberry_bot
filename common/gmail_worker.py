from common.daemon import Daemon
from send_mail import send_mail
from datetime import datetime
import commands
import imaplib
import email
import re

GMAIL_HOST = "imap.gmail.com"
GMAIL_PORT = 993

## @class GMailException - This is a class for exceptions from Gmail.<br>
# It is a child for class Exception.
class GMailException(Exception):
    def __init__(self, msg):
        self.msg = msg

    def __str__(self):
        return self.msg

class GMailWorker(Daemon):

    def __init__(self, username, password, timeout = 1, kill_time = None):
        Daemon.__init__(self, timeout, kill_time)

        self.user = username
        self.password = password

        #rv, mailboxes = conn.list()
        #if rv == 'OK':
        #    print "Mailboxes:"
        #    print mailboxes

        self.need_stop = False

    def extract_body(self, payload):
        if isinstance(payload,str):
            return payload
        else:
            return '\n'.join([self.extract_body(part.get_payload()) for part in payload])

    def run_processing(self):
        conn = imaplib.IMAP4_SSL(GMAIL_HOST, GMAIL_PORT)

        try:
            conn.login(self.user, self.password)
        except imaplib.IMAP4.error:
            raise GMailException,"Login Failed!!!"

        conn.select('INBOX')

        typ, data = conn.search(None, '(UNSEEN)')
        for num in data[0].split():
            typ, msg_data = conn.fetch(num, '(RFC822)')

            command_execute = []
            command_error = []
            for response_part in msg_data:
                if isinstance(response_part, tuple):
                    msg = email.message_from_string(response_part[1])

                    ret_addr = msg['Return-Path']
                    ret_addr = ret_addr.rstrip()
                    ret_addr = ret_addr.lstrip()
                    if re.compile('^<').search(ret_addr):
                        ret_addr = re.compile('^<').sub('', ret_addr)
                    if re.compile('>$').search(ret_addr):
                        ret_addr = re.compile('>$').sub('', ret_addr)

                    subject=msg['subject']
                    #print(subject)
                    payload=msg.get_payload()
                    body = self.extract_body(payload)

                    commands_arr = body.split('\n')
                    for command_line in commands_arr:
                        if len(command_line.split('=')) != 2:
                            continue
                        command, value = command_line.split('=')
                        command, value = self.check_command_and_value(command, value)

                        if command in commands.commands_list.keys():
                            result = commands.send_command(command, value)
                            if result is not None:
                                command_execute.append("%s" % (str(result)))
                            else:
                                command_execute.append("Command %s with value %s "
                                                        "executed." % (command, value))
                        else:
                            command_error.append("Command %s is not found!" % command)
            if len(command_execute) + len(command_error) == 0:
                continue
            now = datetime.now()
            msg_out = ''

            if len(command_error) > 0:
                msg_out += 'Execution status: failed\n'
            else:
                msg_out += 'Execution status: passed\n'

            if len(command_execute):
                for str_com in command_execute:
                    msg_out += str_com + '\n'
            msg_out += '\n\n\n'
            if len(command_error):
                msg_out += 'Failed commands:\n'
                for str_com in command_error:
                    msg_out += str_com + '\n'
            send_mail(self.user, self.password, [ret_addr], subject + " executed", msg_out)

            #typ, response = conn.store(num, '+FLAGS', r'(\Seen)')
            conn.store(num, '+FLAGS', r'(\Seen)')
        try:
            conn.close()
            conn.logout()
        except:
            pass


    def check_command_and_value(self, command, value):
        command = command.rstrip()
        command = command.lstrip()
        command = command.lower()

        value = value.rstrip()
        value = value.lstrip()
        value = value.lower()

        if value == "off" or value == "false":
            value = False
        elif value == "on" or value == "true":
            value = True

        return command, value

if __name__=='__main__':
    gmw = GMailWorker()

    gmw.run()

    while gmw.is_alive():
        pass
