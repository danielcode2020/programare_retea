import socket
from urllib.parse import urlparse
import re
import sys
# host = 'me.utm.md'
print("port %s" % (sys.argv[1]))
print("host %s" % (sys.argv[2]))
print("link  %s" % (sys.argv[3]))
print("filename %s" % (sys.argv[4]))
port = int((sys.argv[1]))
host = (sys.argv[2])
link = (sys.argv[3])
filename = (sys.argv[4])
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (host, port)
sock.connect(server_address)
url = urlparse(link)
request = "GET {} HTTP/1.0\r\nHost: {}\r\n\r\n".format(url.path, host)
sock.send(request.encode())
response = sock.recv(1024)
headers, image_data = response.split(b"\r\n\r\n", 1)

content_length_match = re.search(r'content-length:\s*(\d+)', headers.decode().lower())
content_length = int(content_length_match.group(1))

while len(image_data) < content_length:
    image_data += sock.recv(1024)

with open("images/"+filename, "wb") as f:
    f.write(image_data)
    f.close()
sock.close()