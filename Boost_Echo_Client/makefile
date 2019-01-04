CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_thread

all: EchoClient
	g++ -o bin/runme bin/ConnectionHandler.o bin/echoClient.o bin/EncoderDecoder.o bin/Packet.o bin/NotificationPacket.o bin/AckPacket.o bin/AckFollowPacket.o bin/AckUserListPacket.o bin/StatAckPacket.o bin/ErrorPacket.o bin/Protocol.o bin/KeyboardTask.o bin/Client.o $(LDFLAGS)

EchoClient: bin/ConnectionHandler.o bin/echoClient.o bin/EncoderDecoder.o bin/Packet.o bin/NotificationPacket.o bin/AckPacket.o bin/AckFollowPacket.o bin/AckUserListPacket.o bin/StatAckPacket.o bin/ErrorPacket.o bin/Protocol.o bin/KeyboardTask.o bin/Client.o

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/Packet.o: src/Packet.cpp
	g++ $(CFLAGS) -o bin/Packet.o src/Packet.cpp

bin/NotificationPacket.o: src/NotificationPacket.cpp
	g++ $(CFLAGS) -o bin/NotificationPacket.o src/NotificationPacket.cpp

bin/AckPacket.o: src/AckPacket.cpp
	g++ $(CFLAGS) -o bin/AckPacket.o src/AckPacket.cpp

bin/AckFollowPacket.o: src/AckFollowPacket.cpp
	g++ $(CFLAGS) -o bin/AckFollowPacket.o src/AckFollowPacket.cpp

bin/AckUserListPacket.o: src/AckUserListPacket.cpp
	g++ $(CFLAGS) -o bin/AckUserListPacket.o src/AckUserListPacket.cpp

bin/StatAckPacket.o: src/StatAckPacket.cpp
	g++ $(CFLAGS) -o bin/StatAckPacket.o src/StatAckPacket.cpp

bin/ErrorPacket.o: src/ErrorPacket.cpp
	g++ $(CFLAGS) -o bin/ErrorPacket.o src/ErrorPacket.cpp

bin/Protocol.o: src/Protocol.cpp
	g++ $(CFLAGS) -o bin/Protocol.o src/Protocol.cpp

bin/KeyboardTask.o: src/KeyboardTask.cpp
	g++ $(CFLAGS) -o bin/KeyboardTask.o src/KeyboardTask.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

clean:
	rm -f bin/*
