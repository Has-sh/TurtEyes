import RPi.GPIO as GPIO
import requests
import subprocess
import time

# Set up GPIO using BCM numbering
GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.IN)  # Set GPIO 16 as an input

try:
    while True:
        if GPIO.input(16) == GPIO.HIGH:
            url = 'http://192.168.137.125/test_battery_notification'
            try:
                response = requests.get(url)
                if response.status_code == 200:
                    print("Notification sent successfully to server")
                    subprocess.run(["sudo", "systemctl", "stop", "gpio_check.service"])
                    break
                else:
                    print("Failed to send notification to server. Status code:", response.status_code)
            except requests.RequestException as e:
                print("Error sending notification:", e)
            break  # Exit the loop after sending the notification
        else:
            print("GPIO 16 is not HIGH")
        time.sleep(1)  # Check every second
finally:
    GPIO.cleanup()

