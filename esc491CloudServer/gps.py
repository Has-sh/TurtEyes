from firebase_admin import db
import serial
import subprocess
import requests

json_file_path = "static/esc491-eb2b5-e70e4cb5e2cc.json"
cred = credentials.Certificate(json_file_path)
firebase_admin.initialize_app(cred, {'databaseURL': 'https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app/'})
ref = db.reference('/devices/device1')

def parse_gprmc(sentence):
    data = sentence.decode().split(',')
    if data[0] == '$GNRMC' and data[2] == 'A':  # Check for valid data and active mode
        lat = _parse_latitude(data[3], data[4])  # Latitude
        lon = _parse_longitude(data[5], data[6])  # Longitude
        return lat, lon
    else:
        return None

def _parse_latitude(lat_str, lat_dir):
    # Convert latitude from NMEA format (ddmm.mmmm) to decimal degrees
    lat_deg = float(lat_str[:2])
    lat_min = float(lat_str[2:])
    lat = lat_deg + lat_min / 60.0
    if lat_dir == 'S':
        lat = -lat
    return lat

def _parse_longitude(lon_str, lon_dir):
    # Convert longitude from NMEA format (dddmm.mmmm) to decimal degrees
    lon_deg = float(lon_str[:3])
    lon_min = float(lon_str[3:])
    lon = lon_deg + lon_min / 60.0
    if lon_dir == 'W':
        lon = -lon
    return lon

port = "/dev/ttyAMA0"
ser = serial.Serial(port, baudrate=9600, timeout=0.5)

while True:
    newdata = ser.readline()
    if newdata.startswith(b'$GNRMC'):
        gps_data = parse_gprmc(newdata)
        if gps_data:
            lat, lon = gps_data
            print(f'Latitude: {lat}, Longitude: {lon}')
            subprocess.run(["sudo", "systemctl", "stop", "gps.service"])
            ref.update({'Latitude': lat, 'Longitude': lon})
