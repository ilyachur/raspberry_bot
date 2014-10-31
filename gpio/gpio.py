
def check_gpio():
    hash = {}
    numbers = [3, 5, 7, 8, 10, 11, 12, 13, 15, 16, 18, 19, 21, 22, 23, 24, 26, 29, 31, 32, 33, 35, 36, 37, 38, 40]
    try:
        import RPi.GPIO as GPIO
        GPIO.setmode(GPIO.BOARD)

        for gpio_num in numbers:
            GPIO.setup(gpio_num, GPIO.OUT)
            if GPIO.input(gpio_num):
                hash['gpio_' + str(gpio_num)] = 'on'
            else:
                hash['gpio_' + str(gpio_num)] = 'off'

        GPIO.cleanup()
    except Exception as e:
        print e
        for gpio_num in numbers:
            hash['gpio_' + str(gpio_num)] = 'off'
    return hash