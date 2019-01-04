#include <utility>

//
// Created by shvatm on 12/29/18.
//

#include "AckUserListPacket.h"

AckUserListPacket::AckUserListPacket() : AckPacket(7), userNameList(""), numOfUsers(0) {}

AckUserListPacket::AckUserListPacket(const AckUserListPacket &ackUserListPacket) : AckPacket(7), userNameList(
        ackUserListPacket.userNameList), numOfUsers(ackUserListPacket.numOfUsers) {
}

AckUserListPacket &AckUserListPacket::operator=(const AckUserListPacket &ackUserListPacket) {
    userNameList = ackUserListPacket.userNameList;
    numOfUsers = ackUserListPacket.numOfUsers;
    return *this;
}

short AckUserListPacket::GetNumOfUsers() {
    return numOfUsers;
}

std::string AckUserListPacket::GetUserNameList() {
    return userNameList;
}

AckUserListPacket::AckUserListPacket(short _numOfUsers, std::string _userNameList) : AckPacket(7),
                                                                                     userNameList(
                                                                                             std::move(_userNameList)),
                                                                                     numOfUsers(_numOfUsers) {
}
