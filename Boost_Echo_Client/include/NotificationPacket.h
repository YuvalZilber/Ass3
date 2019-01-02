//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_NOTIFICATIONPACKET_H
#define BOOST_ECHO_CLIENT_NOTIFICATIONPACKET_H

#include "Packet.h"

class NotificationPacket : public Packet{
private:
std::string postingUser;
    char type;//pm (0) post(1)
std::string content;
public:
    NotificationPacket();
    NotificationPacket(std::string _postingUser, char _type,std::string _content);
    NotificationPacket(const NotificationPacket& notificationPacket);
    NotificationPacket& operator=(const NotificationPacket& notificationPacket);
    ~NotificationPacket()= default;
    std::string getPostingUser();
    std::string getContent();
    short getType();




};


#endif //BOOST_ECHO_CLIENT_NOTIFICATIONPACKET_H
