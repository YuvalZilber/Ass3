#include <ConnectionHandler.h>
#include <EncoderDecoder.h>
#include <ErrorPacket.h>
#include <NotificationPacket.h>
#include <AckPacket.h>
#include <AckFollowPacket.h>
#include <AckUserListPacket.h>
#include <StatAckPacket.h>
#include "../include/EncoderDecoder.h"

using boost::asio::ip::tcp;

using namespace std;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_), encDec(),
                                                                isDisconnectApproved(false),msg() {}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string &line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string &line) {
    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
        do {
            getBytes(&ch, 1);
            frame.append(1, ch);
        } while (delimiter != ch);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string &frame, char delimiter) {
    bool result = sendBytes(frame.c_str(), frame.length());
    if (!result) return false;
    return sendBytes(&delimiter, 1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

void ConnectionHandler::pushToVec(std::vector<char>& v, char *topush, int len) {
    for (int i = 0; i < len; ++i)
        v.push_back(topush[i]);
}

short ConnectionHandler::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

Packet *ConnectionHandler::DecodeNotification() {

    char type = (*msg)[2];

    vector<char> postingBytes = vector<char>();
    for (int i = 3; (*msg)[i] != '0'; ++i) {
        postingBytes.push_back((*msg)[i]);
    }
    std::string postingUser(postingBytes.begin(), postingBytes.end());

    vector<char> contentBytes =  vector<char>();
    for (int j = 3 + 1 + postingUser.length(); (*msg)[j] != '0'; ++j) {
        contentBytes.push_back((*msg)[j]);
    }
    std::string content(contentBytes.begin(), contentBytes.end());

    NotificationPacket *notificationPacket = new NotificationPacket(postingUser, type, content);
    return notificationPacket;

}

Packet *ConnectionHandler::decodeNextMessage() {//translating bytes into packet for the benefit of the client
msg=new vector<char>();
    char *opcode = new char[2];
    getBytes(opcode, 2);
    pushToVec(*msg, opcode, 2);
    switch (bytesToShort(opcode)) {

       // default: {

            //decoding cases from server
            case 9:
                pushToMsg(1);
            pushToMsg(-2);

            return DecodeNotification();
            case 10:
                pushToMsg(2);
            return DecodeAck();
            case 11: {
                pushToMsg(2);
                char* arr=new char[2];
                arr[0] = (*msg)[2];
                arr[1] = (*msg)[3];
                ErrorPacket *errorPacket = new ErrorPacket(bytesToShort(arr));
                return errorPacket;
            }


      //  }
    }
}


Packet *ConnectionHandler::DecodeAck() {
    char *msgopcode = new char[2];
    msgopcode[0] = (*msg)[2];
    msgopcode[1] = (*msg)[3];
    short msgOpcode = bytesToShort(msgopcode);
    switch (msgOpcode) {
        case 4: {
            char *numusers = new char[2];
            numusers[0] = (*msg)[4];
            numusers[1] = (*msg)[5];
            short numOfUsers = bytesToShort(numusers);
          string userNameList="";
            for (int i = 6; i < (*msg).size(); ++i)
                userNameList += (*msg)[i] == 0 ? ' ': (*msg)[i];

            AckFollowPacket *ackFollowPacket = new AckFollowPacket(numOfUsers, userNameList);
            ackFollowPacket->SetMsgOpcode(4);
            return ackFollowPacket;
        }
        case 7: {
            char *numusers = new char[2];
            numusers[0] = (*msg)[4];
            numusers[1] = (*msg)[5];
            short numOfUsers = bytesToShort(numusers);
            std::string userNameList="";
            for (int i = 6; i < (*msg).size(); ++i)
                userNameList += (*msg)[i] == 0 ? ' ': (*msg)[i];
            AckUserListPacket *listPacket = new AckUserListPacket(numOfUsers, userNameList);
            listPacket->SetMsgOpcode(7);
            return listPacket;
        }

        case 8: {
            char *postsNum = new char[2];
            postsNum[0] = (*msg)[4];
            postsNum[1] = (*msg)[5];
            short postNumber = bytesToShort(postsNum);
            char *follownum = new char[2];
            follownum[0] = (*msg)[6];
            follownum[1] = (*msg)[7];
            short follownumber = bytesToShort(follownum);
            char *followingnum = new char[2];
            followingnum[0] = (*msg)[8];
            followingnum[1] = (*msg)[9];
            short followingnumber = bytesToShort(followingnum);

            StatAckPacket *statAckPacket = new StatAckPacket(postNumber, follownumber, followingnumber);
            statAckPacket->SetMsgOpcode(8);
            return statAckPacket;
        }
        default: {
            AckPacket *ackPacket = new AckPacket(msgOpcode);
            return ackPacket;
        }
    }

}

bool ConnectionHandler::sendPacket(Packet &packet) {
    char *toSend = encDec.encode(packet);//converting packet to bytes
    int length = packet.getSize();
    for (int i = 0; i < length; ++i) {
        std::cout<<(int)toSend[i]<<", ";
    }
    std::cout<<endl;
    bool isDone = sendBytes(toSend, length);
    return isDone;
}

void ConnectionHandler::ApproveDisconnect() {
    isDisconnectApproved = true;
}

bool ConnectionHandler::IsDisconnectApproved() {
    return isDisconnectApproved;
}

void ConnectionHandler::pushToMsg(int len) {
    if (len > 0) {
        char *bytes = new char[len];
        this->getBytes(bytes, (unsigned) len);
        pushToVec(*msg, bytes, len);
    }
    while (len < 0) {
        char *bytes = new char[1];
        this->getBytes(bytes, 1);
        char byte = bytes[0];
        if (byte == 0) {
            len++;
            if (len != 0)
                (*msg).push_back(' ');
        } else
            (*msg).push_back(byte);
    }

}