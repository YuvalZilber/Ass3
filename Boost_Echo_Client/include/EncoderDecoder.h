//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_ENCODERDECODER_H
#define BOOST_ECHO_CLIENT_ENCODERDECODER_H


#include <vector>
#include "Packet.h"

class EncoderDecoder {

private:
    std::vector<char> msg;
    Packet *decodedPacket;


public:
    EncoderDecoder();

    EncoderDecoder(const EncoderDecoder &aaa);

    EncoderDecoder &operator=(const EncoderDecoder &aaa);

    virtual ~EncoderDecoder();

    //Packet *decodeNextMessage(char);//gets bytes and turning it to a packet
    char *encode(const Packet &p);//gets packet and encoding it to bytes

    std::vector<std::string> split(std::string s, std::string delimiter);

    std::vector<char> intToBytes(int value);

};


#endif //BOOST_ECHO_CLIENT_ENCODERDECODER_H
