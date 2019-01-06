//
// Created by shvatm on 12/29/18.
//

#include <EncoderDecoder.h>
#include <NotificationPacket.h>
#include <sstream>
#include <AckPacket.h>
#include <AckFollowPacket.h>
#include <AckUserListPacket.h>
#include <StatAckPacket.h>
#include <cstring>
#include <bitset>

#include "../include/EncoderDecoder.h"
#include "ErrorPacket.h"

// char* msg;
//    int Counter;
//    Packet* decodedPacket;
using namespace std;

EncoderDecoder::EncoderDecoder() :msg(), decodedPacket(nullptr) {

}

EncoderDecoder::EncoderDecoder(const EncoderDecoder &encoderDecoder) :msg(), decodedPacket(nullptr) {
    msg[0] = encoderDecoder.msg[0];
    msg[1] = encoderDecoder.msg[1];
    if (encoderDecoder.decodedPacket != nullptr)
        decodedPacket = new Packet(*encoderDecoder.decodedPacket);
    else
        decodedPacket = nullptr;
}

EncoderDecoder &EncoderDecoder::operator=(const EncoderDecoder &encoderDecoder) {
    msg[0] = encoderDecoder.msg[0];
    msg[1] = encoderDecoder.msg[1];
    //if (decodedPacket != nullptr)
    //    delete decodedPacket;
    if (encoderDecoder.decodedPacket != nullptr)
        decodedPacket = new Packet(*encoderDecoder.decodedPacket);
    else
        decodedPacket = nullptr;
    return *this;
}

EncoderDecoder::~EncoderDecoder() {
    msg.clear();
}

typedef bitset<8> BYTE;

char *EncoderDecoder::encode(const Packet &p) {
    std::string msg = p.GetMsg();
    std::vector<std::string> splitmsg = split(msg, " ");//splits the client command
    std::vector<BYTE> output;
    if (splitmsg[0] == "REGISTER") {
        BYTE *opcode = new BYTE[2];
        opcode[0] = 0;
        opcode[1] = 1;
        char *username = const_cast<char *>(splitmsg[1].c_str());
        char *password = const_cast<char *>(splitmsg[2].c_str());
        output.push_back(0);
        output.push_back(1);
        for (int i = 0; i < (int)strlen(username); ++i) {
            output.push_back(username[i]);
        }
        output.push_back(0);
        for (int j = 0; j < (int)strlen(password); ++j) {
            output.push_back(password[j]);
        }
        output.push_back('\0');
        delete[] opcode;
    }

    if (splitmsg[0] == "LOGIN") {

        char *username = const_cast<char *>(splitmsg[1].c_str());
        char *password = const_cast<char *>(splitmsg[2].c_str());
        output.push_back(0);
        output.push_back(2);
        for (int i = 0; i < (int)strlen(username); ++i) {
            output.push_back(username[i]);
        }
        output.push_back('\0');
        for (int j = 0; j < (int)strlen(password); ++j) {
            output.push_back(password[j]);
        }
        output.push_back('\0');
    }


    if (splitmsg[0] == "LOGOUT") {
        output.push_back(0);
        output.push_back(3);
    }

    if (splitmsg[0] == "FOLLOW") {

        output.push_back(0);
        output.push_back(4);

        if (splitmsg[1] == "0") // pushing 1 or zero according the client's input
            output.push_back(0); //todo:'0' or '/0'
        else
            output.push_back(1);

        int fnumber = std::stoi(splitmsg[2]);
        std::vector<char> follow = intToBytes(fnumber);
        for (unsigned i = 0; i < follow.size(); ++i) {
            output.push_back(follow[i]);
        }
        for (int j = 3; j < (int)splitmsg.size(); ++j) {
            char *userNameList = const_cast<char *>(splitmsg[j].c_str());
            for (unsigned int i = 0; i < strlen(userNameList); i++) {
                output.push_back(userNameList[i]);

            }
            output.push_back(0);
        }

    }
    if (splitmsg[0] == "POST") {

        output.push_back(0);
        output.push_back(5);

        for (unsigned long i = splitmsg[0].length()+1; i<msg.length(); i++) {
            output.push_back(msg[i]);
        }
        output.push_back('\0');
    }
    if (splitmsg[0] == "PM") {
        output.push_back(0);
        output.push_back(6);
        char *userName = const_cast<char *>(splitmsg[1].c_str());
        for (unsigned long i = 0; i < strlen(userName); i++) {
            output.push_back(userName[i]);
        }
        output.push_back(0);
        char *content = const_cast<char *>(splitmsg[2].c_str());
        for (unsigned long  i = 0; i < strlen(content); i++) {
            output.push_back(content[i]);
        }
        output.push_back('\0');
    }
    if (splitmsg[0] == "USERLIST") {
        output.push_back(0);
        output.push_back(7);
    }

    if (splitmsg[0] == "STAT") {
        output.push_back(0);
        output.push_back(8);
        char *userName = const_cast<char *>(splitmsg[1].c_str());
        for (unsigned long i = 0; i < strlen(userName); i++) {
            output.push_back(userName[i]);
        }
        output.push_back('\0');
    }
    char *kkk = new char[output.size()];
    for (unsigned long k = 0; k < output.size(); ++k) {
        kkk[k] = static_cast<char>(output[k].to_ullong());
    }
    return kkk;
}



std::vector<std::string> EncoderDecoder::split(std::string s, std::string delimiter) {
    std::vector<std::string> list;
    std::vector<std::string> output;
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


std::vector<char> EncoderDecoder::intToBytes(int value) {//todo:maybe will cause probelms
    std::vector<char> result;
    result.push_back(value >> 8);
    result.push_back(value);
    return result;
}









