//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_ACKFOLLOWPACKET_H
#define BOOST_ECHO_CLIENT_ACKFOLLOWPACKET_H

#include "AckPacket.h"

class AckFollowPacket: public AckPacket {
private:
    short numOfUsers;
    std::string userNameList;
public:
    AckFollowPacket();
    AckFollowPacket(short _numOfUsers, std::string _userNameList);
    AckFollowPacket(const AckFollowPacket&ackFollowPacket);
    AckFollowPacket& operator=(const AckFollowPacket&ackFollowPacket);
    virtual ~AckFollowPacket()= default;
    short GetNumOfUsers();
    std::string GetUserNameList();
    //int size() override;
};


#endif //BOOST_ECHO_CLIENT_ACKFOLLOWPACKET_H
