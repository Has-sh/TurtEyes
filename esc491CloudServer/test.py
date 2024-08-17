import unittest
from server_old import app

class TestFlaskApp(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()

    def test_get_sensor_value(self):
        # Test if the /sensor endpoint returns a valid response
        response = self.app.get('/sensor')
        self.assertEqual(response.status_code, 200)  # Check if status code is 200 (OK)
        data = response.get_data(as_text=True)
        self.assertIsInstance(int(data), int)  # Check if response data can be converted to an integer

    def test_get_gps_data(self):
        # Test if the /gps endpoint returns GPS data
        response = self.app.get('/gps')
        self.assertEqual(response.status_code, 200)  # Check if status code is 200 (OK)
        data = response.get_json()
        self.assertIn('latitude', data)  # Check if response contains 'latitude' key
        self.assertIn('longitude', data)  # Check if response contains 'longitude' key

    def test_register_fcm_token(self):
        # Test if the /register_token endpoint can register an FCM token
        response = self.app.post('/register_token', json={'token': 'example_token'})
        self.assertEqual(response.status_code, 200)  # Check if status code is 200 (OK)
        data = response.get_json()
        self.assertEqual(data['status'], 'Token registered successfully')  # Check if response contains expected status message

    def test_register_fcm_token_missing_token(self):
        # Test if the /register_token endpoint handles missing token correctly
        response = self.app.post('/register_token', json={})
        self.assertEqual(response.status_code, 400)  # Check if status code is 400 (Bad Request)
        data = response.get_json()
        self.assertIn('error', data)  # Check if response contains an error message

    def test_test_send_notification(self):
        # Test if the /test_send_notification endpoint triggers a test notification
        response = self.app.get('/test_send_notification')
        self.assertEqual(response.status_code, 200)  # Check if status code is 200 (OK)
        data = response.get_json()
        self.assertEqual(data['status'], 'Testing send_notification')  # Check if response contains expected status message

    def test_invalid_route(self):
        # Test for a non-existing route to ensure it returns a 404 (Not Found) error
        response = self.app.get('/invalid_route')
        self.assertEqual(response.status_code, 404)  # Check if status code is 404 (Not Found)

    def test_register_fcm_token_invalid_method(self):
        # Test if the /register_token endpoint only supports POST method
        response = self.app.get('/register_token')
        self.assertEqual(response.status_code, 405)  # Check if status code is 405 (Method Not Allowed)

    def test_test_send_notification_invalid_method(self):
        # Test if the /test_send_notification endpoint only supports GET method
        response = self.app.post('/test_send_notification')
        self.assertEqual(response.status_code, 405)  # Check if status code is 405 (Method Not Allowed)

if __name__ == '__main__':
    unittest.main()
