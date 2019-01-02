//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_ACKPACKET_H
#define BOOST_ECHO_CLIENT_ACKPACKET_H

#include "Packet.h"

class AckPacket : public Packet{
private:
    short msgOpcode;

public:
    AckPacket();
    AckPacket(short _msgopCode);
    AckPacket(const AckPacket& packet);
    AckPacket& operator=(const AckPacket& packet);
    ~AckPacket()= default;
    short GetMsgOpcode();
    void SetMsgOpcode(short msg);

};


#endif //BOOST_ECHO_CLIENT_ACKPACKET_H
