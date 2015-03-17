#!/usr/bin/python

import os
import commands
import sys

print "cleaning... `ant clean`"

(status, output) = commands.getstatusoutput ("ant clean");
if status != 0:
	print output
	sys.exit(status)
else:
	print "Clean successful..."

print "building... `ant debug`"

(status, output) = commands.getstatusoutput ("ant debug");

if status != 0:
	print output
	sys.exit(status)	
else:
	print "Build successful..."

print "Uninstalling previous version... `/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb uninstall com.flashcard_prime`"
(status, output) = commands.getstatusoutput ("/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb uninstall com.flashcard_prime");

if status != 0:
	print output
	sys.exit(status)
else:
	print "Uninstall successful..."

print "Installing new version... `/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb install /mnt/projects/java/genetics_era/Flashcard_Prime/bin/Flashcard_Prime-debug.apk`"
(status, output) = commands.getstatusoutput ("/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb install /mnt/projects/java/genetics_era/Flashcard_Prime/bin/Flashcard_Prime-debug.apk");

if status != 0:
	print output
	sys.exit(status)
else:
	print "Install successful.."

print "Launching new app... `/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb shell am start -n com.flashcard_prime/com.flashcard_prime.HomeScreenActivity`"
(status, output) = commands.getstatusoutput ("/opt/adt-bundle-linux-x86_64/sdk/platform-tools/adb shell am start -n com.flashcard_prime/com.flashcard_prime.HomeScreenActivity");

if status != 0:
	print output
	sys.exit(status)
else:
	print "Launch successful!"


