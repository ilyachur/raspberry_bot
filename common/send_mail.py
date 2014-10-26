import smtplib


def send_mail(user, pwd, to_mail, subject, text):
    gmail_user = user + "@gmail.com"
    FROM = gmail_user
    TO = to_mail                    # must be a list
    SUBJECT = subject
    TEXT = text

    # Prepare actual message
    message = """\From: %s\nTo: %s\nSubject: %s\n\n%s
        """ % (FROM, ", ".join(TO), SUBJECT, TEXT)
    try:
        server = smtplib.SMTP('smtp.gmail.com', 587)
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(gmail_user, pwd)
        server.sendmail(FROM, TO, message)
        #server.quit()
        server.close()
        print '\n\nsuccessfully sent the mail\n\n'
    except:
        print "\n\nfailed to send mail\n\n"