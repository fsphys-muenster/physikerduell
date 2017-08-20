#!/bin/sh
# This script adds a udev rule to /etc/udev/rules.d/ which gives all devices
# with vendor ID 1b4f the attribute ENV{ID_MM_DEVICE_IGNORE}="1".
# This is necessary because ModemManager
# (https://www.freedesktop.org/wiki/Software/ModemManager/) will open a
# connection to the serial device registered by the MaKey MaKey, keeping it
# busy so that Arduino IDE cannot connect to it for upload. The attribute
# ID_MM_DEVICE_IGNORE causes ModemManager to ignore the device. The default
# udev rules already do this for a number of vendors/Arduino devices in
# /lib/udev/rules.d/77-mm-usb-device-blacklist.rules
# but unfortunately, the MaKey MaKey is not included in this list.
# More information on this issue:
# https://bugzilla.redhat.com/show_bug.cgi?id=1261040
# https://linux-tips.com/t/prevent-modem-manager-to-capture-usb-serial-devices/284
# http://www.deferredprocrastination.co.uk/blog/2016/Sparkfun-Pro-Micro-device-busy/
# http://starter-kit.nettigo.eu/2015/serial-port-busy-for-avrdude-on-ubuntu-with-arduino-leonardo-eth/

# Vendor ID 1b4f is SparkFun.
# The MaKey MaKey has a product ID of 2b75, so in principle, this should
# also work:
# ATTRS{idVendor}=="1b4f", ATTRS{idProduct}=="2b75", ENV{ID_MM_DEVICE_IGNORE}="1"
# but it doesn’t. I’m not sure why – perhaps the MaKey MaKey registers a device
# with another product ID during the upload process?
echo \
'# MaKey MaKey
ATTRS{idVendor}=="1b4f", ENV{ID_MM_DEVICE_IGNORE}="1"' | \
	sudo tee /etc/udev/rules.d/77-makey-makey.rules
# not sure if this is necessary, seems to work without this line too
udevadm trigger

