from paho.mqtt import client as mqtt
import random

host = "192.168.15.3"
username = "mosquitto"
passwd = "mosquittopasswd"

client = None
connected = False

def publish(topic, message):
    if not connected:
        connect()

    res = client.publish(topic, message)
    print("RES: " + str(res[0]))

def connect():
    global client
    client = mqtt.Client("python-" + str(random.randint(0, 100)))

    def on_connect(client, userdata, flags, rc):
        global connected
        connected = True
    def on_disconnect(client, userdata, flags, rc):
        global connected
        connected = False

    client.username_pw_set(username, passwd)
    client.connect(host, 1883)
