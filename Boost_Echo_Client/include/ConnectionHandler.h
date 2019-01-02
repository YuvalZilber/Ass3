#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__
#include <EncoderDecoder.h>

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "../include/EncoderDecoder.h"

using boost::asio::ip::tcp;

class ConnectionHandler {
private:
	const std::string host_;
	const short port_;
	boost::asio::io_service io_service_;   // Provides core I/O functionality
	tcp::socket socket_;
    bool isDisconnectApproved;
    EncoderDecoder encDec;
    std::vector<char>* msg;
public:
    ConnectionHandler(std::string host, short port);
    virtual ~ConnectionHandler();

    // Connect to the remote machine
    bool connect();

    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);

	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);

    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);

	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(std::string& line);

    //gets command, translate it with the encoder and sends to the server
    //bool sendPacket(Packet& packet);
    bool sendPacket(Packet& packet);
    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(std::string& frame, char delimiter);

    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const std::string& frame, char delimiter);

    // Close down the connection properly.
    void close();

    bool IsDisconnectApproved();//returning true if client got a permission to disconnect

    void ApproveDisconnect();
    void pushToVec(std::vector<char>& v, char *topush,int len);
    Packet* decodeNextMessage();
    void pushToMsg(int len);
    short bytesToShort(char *bytesArr);
    Packet *DecodeNotification();
    Packet *DecodeAck();
}; //class ConnectionHandler
 
#endif