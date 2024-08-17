from firebase_admin import db
import serial
import pynmea2

# Firebase initialization
json_file_path = "static/esc491-eb2b5-e70e4cb5e2cc.json"
cred = credentials.Certificate(json_file_path)
firebase_admin.initialize_app(cred, {'databaseURL': 'https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app/'})
ref = db.reference('/devices/device1')

def read_gps_data():
    try:
        with serial.Serial(serial_port, serial_baudrate, timeout=5) as ser:
            while True:
                sentence = ser.readline().decode('utf-8')
                if sentence.startswith('$GPGGA'):
                    data = pynmea2.parse(sentence)
                    latitude = data.latitude
                    longitude = data.longitude
                    ref.update({'Latitude': latitude,'Longitude': longitude})
                    return {'latitude': latitude, 'longitude': longitude}
    except Exception as e:
        print("Error reading GPS data:", str(e))
        return None
