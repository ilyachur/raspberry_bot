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

        self.conn = imaplib.IMAP4_SSL(GMAIL_HOST, GMAIL_PORT)

        self.user = username
        self.password = password

        try:
            self.conn.login(self.user, self.password)
        except imaplib.IMAP4.error:
            raise GMailException,"Login Failed!!!"

        #rv, mailboxes = self.conn.list()
        #if rv == 'OK':
        #    print "Mailboxes:"
        #    print mailboxes

        self.need_stop = False

    def __del__(self):
        try:
            self.conn.close()
        except:
            pass
        self.conn.logout()

    def extract_body(self, payload):
        if isinstance(payload,str):
            return payload
        else:
            return '\n'.join([self.extract_body(part.get_payload()) for part in payload])

    def pre_run(self):
        self.conn.select('INBOX')

    def post_run(self):
        self.conn.select()

    def run_processing(self):
        typ, data = self.conn.search(None, 'UNSEEN')
        for num in data[0].split():
            typ, msg_data = self.conn.fetch(num, '(RFC822)')

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

                    #subject=msg['subject']
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
                            result = commands.send_command(msg['subject'], command, value)
                            if result is not None:
                                command_execute.append("Command %s with value %s executed. Result:\n"
                                                        " %s" % (command, value, msg['subject'], result))
                            else:
                                command_execute.append("Command %s with value %s for group %s "
                                                        "executed" % (command, value, msg['subject']))
                        else:
                            command_error.append("Command %s is not found!" % command)
            if len(command_execute) + len(command_error) == 0:
                continue
            now = datetime.now()
            msg_out = 'Time: %s\n\n' % now
            msg_out += 'Your '
            if len(command_execute) + len(command_error) > 1:
                msg_out += 'commands executed '
                msg_sub = 'Commands executed'
            else:
                msg_out += 'command executed '
                msg_sub = 'Command executed'

            if len(command_error) > 0:
                if len(command_error) > 1:
                    msg_out += 'with %s errors.\n' % len(command_error)
                else:
                    msg_out += 'with error.\n'
            else:
                msg_out += 'without errors.\n'

            if len(command_execute) == 0:
                msg_out += 'Passed commands are not found!\n'
            else:
                msg_out += 'Passed commands:\n'
                for str_com in command_execute:
                    msg_out += str_com + '\n'
            msg_out += '\n\n\n'
            if len(command_error) == 0:
                msg_out += 'Failed commands are not found!\n'
            else:
                msg_out += 'Failed commands:\n'
                for str_com in command_error:
                    msg_out += str_com + '\n'
            msg_out += '\n\n\n'
            msg_out += '--\nBest regards\nRaspberry Pi (Clever home bot).\n\nTime: %s' % now
            send_mail(self.user, self.password, [ret_addr], msg_sub, msg_out)

            #typ, response = self.conn.store(num, '+FLAGS', r'(\Seen)')
            self.conn.store(num, '+FLAGS', r'(\Seen)')


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
