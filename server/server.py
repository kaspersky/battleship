from socket import *
import random
import thread

HOST = '0.0.0.0'
PORT = 50050

clients = []

def readline_from_socket(sock):
    ret = ''
    while True:
        c = sock.recv(1)
        if c == '\n':
            return ret
        if c == '':
            return ''
        ret += c

class Client:
    def __init__(self, params):
        self.sock, self.addr = params
    def set_enemy(self, enemy):
        self.en_sock, self.en_addr = enemy
    def get_move(self):
        data = readline_from_socket(self.sock)
        if not data:
            self.sock.close()
            return False

        info = data.strip().split(':')
        if info[0] == 'move':
            return int(info[1]), int(info[2])

        self.sock.close()
        return False
    def send_move(self, x, y):
        self.sock.send("move:%s:%s\n" % (x, y))
    def get_response(self):
        data = readline_from_socket(self.sock)
        if not data:
            self.sock.close()
            return False
        info = data.strip().split(':')
        if info[0] == 'response':
            return ':'.join(info[1:])
        if info[0] == 'result':
            self.sock.close()
            return info[1]

        self.sock.close()
        return False
    def send_response(self, response):
        self.sock.send('response:%s\n' % response)
    def send_enemy_info(self, enemy_name, first_to_move):
        self.sock.send('enemy:%s:%s\n' % (enemy_name, first_to_move))
    def send_win(self):
        self.sock.send('result:win\n')
        self.sock.close()

    def get_self_info(self):
        data = readline_from_socket(self.sock)
        if not data:
            self.sock.close()
            return False

        info = data.strip().split(':')
        if info[0] == 'name':
            self.name = info[1]
            return info[1]

        self.sock.close()
        return False

def move(client1, client2):
    coord = client1.get_move()
    if coord is False:
        return False
    x, y = coord
    client2.send_move(x, y)
    res = client2.get_response()
    if res is False:
        return True
    client1.send_response(res)
    return res.split(':')[0]

def handler(client_param1, client_param2):
    client1 = Client(client_param1)
    client2 = Client(client_param2)
    client1.set_enemy(client_param2)
    client2.set_enemy(client_param1)

    c1 = client1.get_self_info()
    c2 = client2.get_self_info()

    ftm = random.randint(0, 1)

    client1.send_enemy_info(client2.name, ftm)
    client2.send_enemy_info(client1.name, 1 - ftm)

    if ftm == 0:
        client1, client2 = client2, client1

    while True:
        res = move(client1, client2)
        if res is True:
            client1.send_win()
            break
        elif res is False:
            client2.send_win()
            break
        elif res == 'missed':
            client1, client2 = client2, client1
        elif res == 'lost':
            client1.send_win()
            break

if __name__=='__main__':
    ADDR = HOST, PORT
    serversock = socket(AF_INET, SOCK_STREAM)
    serversock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    serversock.bind(ADDR)
    serversock.listen(5)
    while True:
        print 'waiting for connection... listening on port', PORT
        clientsock, addr = serversock.accept()
        print '...connected from:', addr
        clients.append((clientsock, addr))
        if len(clients) == 2:
            thread.start_new_thread(handler, (clients[0], clients[1]))
            clients = []
