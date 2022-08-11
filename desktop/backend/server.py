import base64
import socket
import threading
import time
import numpy as np
import cv2
from io import BytesIO
import argparse
import gevent
from gevent import pywsgi
from geventwebsocket.handler import WebSocketHandler
from flask_restful import Api , Resource 
from flask import Flask , request
from flask_sockets import Sockets
from flask_cors import CORS
import json
from threading import Thread
import threading

app = Flask(__name__)
sockets = Sockets(app)
cors = CORS(app, resources={r"/*": {"origins": "*"}})

class Streamera:

	def __init__(self):

		self.clientConnected = False
		self.frame = None


	# def display_image(self, bytes):
		

	# 	self.frame = cv2.imdecode(np.frombuffer(bytes, dtype=np.uint8), cv2.IMREAD_UNCHANGED)

	# 	'''
	# 	####################
	# 	Do anything you want to do with the frame received
	# 	####################
	# 	'''
		
	# 	cv2.namedWindow('streamera',cv2.WINDOW_NORMAL)
	# 	cv2.imshow("streamera", self.frame)
	# 	cv2.waitKey(1)


	def main(self, server, port):
		
		try:
			server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			server_socket.bind((server, port))
			print(f'[+] streamera server created at {server}:{port}')
		except:
			print(f'[!] Could not create streamera server at {server}:{port}')
			return -1

		server_socket.listen(5)

		cli_sock, ret = server_socket.accept()
		print(f"[+] CLIENT JOINED: {cli_sock}")

		SIZE = 1500
		str_buf = b''

		while True:
			
			try:
				cli_sock.sendall(b'asdf')
				msg = cli_sock.recv(SIZE)
				self.clientConnected = True
			except:
				print('[-] Client disconnected.\n[-] Terminating server.')
				break

			if(msg):
				str_buf += msg

				# if string contains ffd9 (it means end of image)
				pos = str_buf.find(b'\xff\xd9')
				if pos >= 0:
					# call display_image()
					# Streamera.display_image(self, str_buf[:pos+2])
					self.frame = cv2.imdecode(np.frombuffer(str_buf[:pos+2], dtype=np.uint8), cv2.IMREAD_UNCHANGED)
					# replace str_buf with left over bytes
					str_buf = str_buf[pos+2:]

		server_socket.close()
		self.clientConnected = False
		# cv2.destroyAllWindows()
		
		Streamera.main(self, server, port)


	def get_ip_address():
		s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		s.settimeout(0)
		
		try:
			# doesn't even have to be reachable
			s.connect(('10.254.254.254', 1))
			IP = s.getsockname()[0]
		except Exception:
			IP = None
		finally:
			s.close()
		return IP


@app.route('/getIpAddPort', methods = ["GET"])
def def_getIpAddPort():

	server = Streamera.get_ip_address()
	print("in getIpAddPort")
	global objStreamera

	data = {
            "server" : server,
			"port" : 5000
        }

	response = app.response_class(
        json.dumps(data),
        status=200,
        mimetype='application/json'
    )
	
	return response

@sockets.route('/clientConnected')
def def_clientConnected(ws):

	global objStreamera

	while True:
		clientConnected = objStreamera.clientConnected
		outFrame = objStreamera.frame
		if outFrame is not None:
			outFrame        = cv2.imencode('.jpg', outFrame)[1].tobytes()
			image_64_encode = base64.b64encode(outFrame)
			data            = json.dumps({'clientConnected': clientConnected, 'frame': image_64_encode.decode('ascii')})
			if ws.closed == False:
				ws.send(data)
		else:
			data            = json.dumps({'clientConnected': clientConnected})
			if ws.closed == False:
				ws.send(data)
		gevent.sleep(.05)
	return


if __name__ == "__main__":
	server = Streamera.get_ip_address()
	port = 5000

	objStreamera = Streamera()
	global wsServer

	thread = Thread(target = objStreamera.main, args=[server, port])
	thread.deamon = True
	thread.start()

	print('<<-----[+] WEB SOCKET SERVER STARTED AT 127.0.0.1:8000----->>')
	wsServer = pywsgi.WSGIServer(('127.0.0.1',8000),app,handler_class = WebSocketHandler)
	wsServer.serve_forever()
