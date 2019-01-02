//
// Created by shvatm on 12/30/18.
//

#ifndef BOOST_ECHO_CLIENT_PROTOCOL_H
#define BOOST_ECHO_CLIENT_PROTOCOL_H


#include <Packet.h>
#include "ConnectionHandler.h"



class Protocol {
private:
    ConnectionHandler* connectionHand;
public:
    Protocol(ConnectionHandler* connectionHandler);
    Protocol(const Protocol& aProtocol);
    Protocol& operator=(const Protocol& aProtocol);
    void process(Packet* p);
};


#endif //BOOST_ECHO_CLIENT_PROTOCOL_H
