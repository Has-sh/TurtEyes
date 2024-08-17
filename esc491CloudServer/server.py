import json
from flask import Flask, jsonify, request
from firebase_admin import credentials, messaging
import firebase_admin
import requests
from google.auth.transport.requests import Request
from google.oauth2 import service_account
import requests

json_file_path = "/var/www/FYP/static/esc491-eb2b5-e70e4cb5e2cc.json"
cred = credentials.Certificate(json_file_path)
firebase_admin.initialize_app(cred)

app = Flask(__name__)

# talisman = Talisman(app, force_https=True)

current_sensor_value = 0  # Replace with your actual logic to get the current sensor value

# Store FCM tokens in-memory (you might want to use a database in production)
# fcm_tokens=set()

@app.route('/sensor', methods=['GET'])
def get_sensor_value():
    # Replace the following line with your logic to get sensor data from the Raspberry Pi
    global current_sensor_value
    return jsonify(current_sensor_value)

@app.route('/gps', methods=['GET'])
def get_gps_data():
    # Replace the following line with your logic to get GPS data from the Raspberry Pi
    gps_data = {'latitude': 35.247526, 'longitude': 33.024703}

    return jsonify(gps_data)

@app.route('/register_token', methods=['POST'])
def register_fcm_token():
    data = request.get_json()
    token = data.get('token')
    if token:
        if not is_token_registered(token):
            save_token_to_file(token)
        return jsonify({'status': 'Token registered successfully'})
    else:
        return jsonify({'error': 'Token not provided'}), 400

def is_token_registered(token):
    try:
        with open("/var/www/FYP/fcm_tokens.json", "r") as file:
            tokens = json.load(file)
            return token in tokens
    except FileNotFoundError:
        return False

def save_token_to_file(token):
    try:
        with open("/var/www/FYP/fcm_tokens.json", "r") as file:
            tokens = json.load(file)
    except FileNotFoundError:
        tokens = []

    if token not in tokens:
        tokens.append(token)
        with open("/var/www/FYP/fcm_tokens.json", "w") as file:
            json.dump(tokens, file)
            return True

    return False

@app.route('/test_send_notification', methods=['GET'])
def test_send_notification():
    global current_sensor_value
    current_sensor_value=1
    send_notification()
    return jsonify({'status': 'Testing send_notification'}), 200

def get_tokens_from_file():
    with open("/var/www/FYP/fcm_tokens.json", "r") as file:
        try:
            return json.load(file)
        except json.decoder.JSONDecodeError:
            return []

def send_notification():
    with open("/var/www/FYP/static/esc491-eb2b5-e70e4cb5e2cc.json", "r") as json_file:
        service_account_info = json.load(json_file)

    credentials = service_account.Credentials.from_service_account_info(
        service_account_info,
        scopes=['https://www.googleapis.com/auth/cloud-platform']
    )
    credentials.refresh(Request())
    tokens = get_tokens_from_file()

    message = "Turtles have hatched on the beach!"
    for token in tokens:
        if message:
            # Construct the FCM payload
            payload = {
                'message': {
                    'token':token,
                    'notification': {
                        'title': 'Urgent',
                        'body': message,
                    },
                    'data': {
                        'key1': 'Urgent',
                        'key2': message,
                    },
                },
            }

            # Send the HTTP request to FCM endpoint
            headers = {
                'Authorization': f'Bearer {credentials.token}',
                'Content-Type': 'application/json',
            }

            response = requests.post('https://fcm.googleapis.com/v1/projects/esc491-eb2b5/messages:send', json=payload, headers=headers, verify=False)
            print(response.json())

    return {'status': 'Notification sent successfully'}
