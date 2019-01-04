//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_ACKUSERLISTPACKET_H
#define BOOST_ECHO_CLIENT_ACKUSERLISTPACKET_H

#include "AckPacket.h"

class AckUserListPacket :public AckPacket{
private:
    std::string userNameList;
    short numOfUsers;
public:
    AckUserListPacket();
    AckUserListPacket(short _numOfUsers,std::string _userNameList);
    AckUserListPacket(const AckUserListPacket&ackUserListPacket);
    AckUserListPacket& operator=(const AckUserListPacket&ackUserListPacket);
    virtual ~AckUserListPacket()= default;
    short GetNumOfUsers();
    std::string GetUserNameList();
};


#endif //BOOST_ECHO_CLIENT_ACKUSERLISTPACKET_H
