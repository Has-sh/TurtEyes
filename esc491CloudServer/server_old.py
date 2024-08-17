import os
import json
from flask import Flask, jsonify, request
from firebase_admin import credentials, messaging, db
import firebase_admin
import requests
import urllib3
from google.auth.transport.requests import Request
from google.oauth2 import service_account
import time

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

json_file_path = "static/esc491-eb2b5-e70e4cb5e2cc.json"
cred = credentials.Certificate(json_file_path)
firebase_admin.initialize_app(cred, {'databaseURL': 'https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app/'})
ref = db.reference('/devices/device1')

app = Flask(__name__)

fcm_tokens = set()

@app.route('/sensor', methods=['GET'])
def get_sensor_value():
    sensor_value = ref.child('sensor').get()
    return jsonify(sensor_value)

@app.route('/gps', methods=['GET'])
def get_gps_data():
    latitude = ref.child('Latitude').get()
    longitude = ref.child('Longitude').get()
    gps_data = {'latitude': latitude, 'longitude': longitude}
    return jsonify(gps_data)

@app.route('/register_token', methods=['POST'])
def register_fcm_token():
    data = request.get_json()
    token = data.get('token')
    if token:
        fcm_tokens.add(token)
        return jsonify({'status': 'Token registered successfully'})
    else:
        return jsonify({'error': 'Token not provided'}), 400
        
@app.route('/test_send_notification', methods=['GET'])
def test_send_notification():
    ref.update({'sensor': "1"})
    send_notification()
    return jsonify({'status': 'Testing send_notification'}), 200

def send_notification():
    with open("static/esc491-eb2b5-e70e4cb5e2cc.json", "r") as json_file:
        service_account_info = json.load(json_file)

    credentials = service_account.Credentials.from_service_account_info(
        service_account_info,
        scopes=['https://www.googleapis.com/auth/cloud-platform']
    )
    credentials.refresh(Request())

    message = "Turtles have hatched on the beach!"
    send_time = int(time.time() * 1000)  # Capture the current time in milliseconds

    # Retrieve tokens from the database
    tokens_ref = db.reference('/devices/tokens')
    tokens_snapshot = tokens_ref.get()

    if tokens_snapshot:
        for _, token in tokens_snapshot.items():
            token=str(token)
            if message:
                payload = {
                    'message': {
                        'token': token,
                        'notification': {
                            'title': 'Urgent',
                            'body': message,
                        },
                        'data': {
                            'key1': 'Urgent',
                            'key2': message,
                            'send_time': str(send_time),
                        },
                    },
                }

                headers = {
                    'Authorization': f'Bearer {credentials.token}',
                    'Content-Type': 'application/json',
                }

                response = requests.post('https://fcm.googleapis.com/v1/projects/esc491-eb2b5/messages:send', json=payload, headers=headers, verify=False)
                print(response.json())

    return {'status': 'Notification sent successfully'}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
