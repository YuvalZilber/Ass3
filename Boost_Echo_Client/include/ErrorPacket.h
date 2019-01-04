//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_ERRORPACKET_H
#define BOOST_ECHO_CLIENT_ERRORPACKET_H

#include "Packet.h"

class ErrorPacket: public Packet {
private:
    short msgOpcode;
public:
    ErrorPacket();
    ErrorPacket(short _msgopcode);
    ErrorPacket(const ErrorPacket& errorPacket);
    ErrorPacket& operator=(const ErrorPacket& errorPacket);
    virtual ~ErrorPacket()= default;
    short GetmsgOpcode();


};


#endif //BOOST_ECHO_CLIENT_ERRORPACKET_H
