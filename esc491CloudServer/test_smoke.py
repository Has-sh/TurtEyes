import requests

def test_server_running():
    # Assuming your server is running on http://localhost:8080
    url = 'http://localhost:8080/sensor'
    response = requests.get(url)

    if response.status_code == 200:
        print("Test passed: Server is running")

        expected_content = '0'or '1'
        if expected_content in response.text:
            print("Test passed: Expected content found in response")
        else:
            print("Test failed: Expected content not found in response")
    else:
        print(f"Test failed: Server returned status code {response.status_code}")

test_server_running()
