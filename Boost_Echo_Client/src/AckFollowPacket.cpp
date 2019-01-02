//
// Created by shvatm on 12/29/18.
//

#include <AckFollowPacket.h>

#include "AckFollowPacket.h"

//short numOfUsers;
//    std::string userNameList;
AckFollowPacket::AckFollowPacket() : AckPacket(4),userNameList(""), numOfUsers(0) {}

AckFollowPacket::AckFollowPacket(const AckFollowPacket &ackFollowPacket) :AckPacket(4),numOfUsers(ackFollowPacket.numOfUsers) {
    userNameList = ackFollowPacket.userNameList;
}

AckFollowPacket &AckFollowPacket::operator=(const AckFollowPacket &ackFollowPacket) {
    userNameList = ackFollowPacket.userNameList;
    numOfUsers = ackFollowPacket.numOfUsers;
    return *this;
}

short AckFollowPacket::GetNumOfUsers() {
    return numOfUsers;
}

std::string AckFollowPacket::GetUserNameList() {
    return userNameList;
}

AckFollowPacket::AckFollowPacket(short _numOfUsers, std::string _userNameList) {
    numOfUsers = _numOfUsers;
    userNameList = std::to_string(_numOfUsers);
}


