# NFC4Tuya

NFC4Tuya makes it easy to sideload and run [Tuya](https://tuya.com) scenes from NDEF compatible NFC tags.

## Download

- **[Version 1.0.0](https://time2code.net/NFC4Tuya_1_0_0.apk)**

## Requirements

- Android 6.0 or later
- NFC-enabled smartphone
- Basic [Tuya Cloud](https://iot.tuya.com) license or more

# Setup

### Once

- Create a [Tuya Cloud project](https://iot.tuya.com/cloud/)
- Link your existing devices through the Link Devices section
- Make sure to subscribe to the [Tuya API products](https://iot.tuya.com/cloud/products) 
- Paste your project Client ID, Client Secret and any Device ID (not Product ID) into the credentials section of the NFC4Tuya app
- Calculate your UID and Home ID through the credentials section of the NFC4Tuya app

### Sideload a scene into an NFC tag

- Retrieve your Tuya Scenes thanks to the reload button in the Scenes section of the NFC4Tuya app
- Select a scene and get your NDEF compatible NFC tag close to your NFC-enabled smartphone

### Run a scene from an NFC tag

- Get your NFC-enabled smartphone close to your sideloaded NFC tag

## Installation

Download the binary and install it into your Android device using apps such as [Mi File Manager](https://play.google.com/store/apps/details?id=com.mi.android.globalFileexplorer).

### Contributing

Feel free to contribute if you want to enhance the experience.

### External libraries

- [OkHttp](https://square.github.io/okhttp/) - HTTP & HTTP/2 client