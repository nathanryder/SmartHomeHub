import mysql.connector as mysql

def setDeviceStatus(deviceId, deviceStatus):
    # Connect to sql
    db = mysql.connect(
        host = "localhost",
        user = "user",
        passwd = "vTy#03dr",
        database = "fyp"
    )

    cursor = db.cursor()

    cursor.execute("UPDATE devices SET last_status='" + deviceStatus + "' WHERE deviceid='" + deviceId + "'")
    cursor.commit()