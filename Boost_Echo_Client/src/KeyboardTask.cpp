//
// Created by shvatm on 12/31/18.
//
#include <KeyboardTask.h>

//ConnectionHandler* connectionHandler;
//    Protocol* protocol;
using namespace std;

KeyboardTask::KeyboardTask(ConnectionHandler *conHand, Protocol *aProtocol) : packet(nullptr),
                                                                              protocol(aProtocol),
                                                                              connectionHandler(conHand) {
}

unsigned long lenBytesStrNoZero(string str) {
    unsigned long strLen = str.length();
    setlocale(LC_ALL, "en_US.UTF-8");
    unsigned long u = 0;
    const char *c_str = str.c_str();
    while (u < strLen) {
        u += mblen(&c_str[u], strLen - u);
    }
    return u;
}

void KeyboardTask::operator()() {
    while (1) {// we wants to read as long as there is a connection to the server
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> words = split(line, " ");
        packet=new Packet((short)0,"",999);
        if (words[0] == "REGISTER") {
            size_t size = 2 + lenBytesStrNoZero(line) - 9 + 1;//|opcode|+restOfString-|REGISTER |+\0
            delete packet;
            packet = new Packet((short) 1, line, size);
        }
        if (words[0] == "LOGIN") {
            size_t size = 2 + lenBytesStrNoZero(line) - 6 + 1;
            delete packet;
            packet = new Packet((short) 2, line, size);

        }
        if (words[0] == "LOGOUT") {
            size_t size = 2;
            delete packet;
            packet = new Packet((short) 3, line, size);

        }
        if (words[0] == "FOLLOW") {
            size_t size = 2 + lenBytesStrNoZero(line) - 7 - 1 + 1;
            delete packet;
            packet = new Packet((short) 4, line, size);

        }
        if (words[0] == "POST") {
            size_t size = 2 + lenBytesStrNoZero(line) - 5 + 1;
            delete packet;
            packet = new Packet((short) 5, line, size);
        }
        if (words[0] == "PM") {
            size_t size = 2 + lenBytesStrNoZero(line) - 3 + 1;
            delete packet;
            packet = new Packet((short) 6, line, size);
        }
        if (words[0] == "USERLIST") {
            size_t size = 2;
            delete packet;
            packet = new Packet((short) 7, line, size);
        }
        if (words[0] == "STAT") {
            size_t size = 2 + lenBytesStrNoZero(line) - 5 + 1;
            delete packet;
            packet = new Packet((short) 3, line, size);
        }

        bool connected = connectionHandler->sendPacket(*packet);
delete packet;
        if (!connected) {//if there's no connection with the server
            break;
        }
    }
}

std::vector<std::string> KeyboardTask::split(std::string s, std::string delimiter) {
    std::vector<std::string> list;
    size_t pos = 0;
    std::string token;
    while ((pos = s.find(delimiter)) != std::string::npos) {
        token = s.substr(0, pos);
        list.push_back(token);
        s.erase(0, pos + delimiter.length());
    }
    list.push_back(s);

    return list;
}

KeyboardTask::~KeyboardTask() {
    if (packet != nullptr)
        delete packet;
}
