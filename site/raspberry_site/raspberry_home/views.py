from datetime import datetime
from django.template import loader, Context
from django.http import HttpResponse

from raspberry_home.models import CPUTemperature


# Create your views here.
def index(request):
    data = cpu_temp_db()
    # if request.user.is_authenticated():
    t = loader.get_template("index.html")
    gpio_data = check_gpio()
    gpio_data['data_temp'] = data['data']
    gpio_data['proc_temp'] = data['temp']
    c = Context(gpio_data)
    return HttpResponse(t.render(c))

def cpu_temp_db():
    big_data = {}
    big_data['data'] = []
    big_data['temp'] = []

    data_i = 1
    for str_temp in CPUTemperature.objects.all():
        big_data['temp'].append(float(str(str_temp).split(' ')[1]))
        big_data['data'].append([data_i, str(datetime.fromtimestamp(int(str(str_temp).split(' ')[0])).strftime('%H:%M:%S %d/%m/%Y'))])
        data_i += 1
    return big_data

def check_gpio():
    hash = {}
    numbers = [3, 5, 7, 8, 10, 11, 12, 13, 15, 16, 18, 19, 21, 22, 23, 24, 26, 29, 31, 32, 33, 35, 36, 37, 38, 40]
    try:
        import RPi.GPIO as GPIO
        GPIO.setmode(GPIO.BOARD)

        for gpio_num in numbers:
            GPIO.setup(gpio_num, GPIO.OUT)
            if not GPIO.input(gpio_num):
                hash['gpio_' + str(gpio_num)] = 'on'
            else:
                hash['gpio_' + str(gpio_num)] = 'off'

        GPIO.cleanup()
    except Exception as e:
        print e
        for gpio_num in numbers:
            hash['gpio_' + str(gpio_num)] = 'off'
    return hash
