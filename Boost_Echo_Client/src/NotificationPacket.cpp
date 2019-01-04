#include <utility>

//
// Created by shvatm on 12/29/18.
//

#include <NotificationPacket.h>

//variables
//std::string postingUser;
//short type;//pm (0) post(1)
//std::string content;
NotificationPacket::NotificationPacket() : Packet(9), postingUser(nullptr), type(-1), content("") {}

NotificationPacket::NotificationPacket(std::string _postingUser, char _type, std::string _content) : Packet(9),
                                                                                                     postingUser(
                                                                                                             std::move(
                                                                                                                     _postingUser)),
                                                                                                     type(_type),
                                                                                                     content(std::move(
                                                                                                             _content)) {
}

NotificationPacket::NotificationPacket(const NotificationPacket &notificationPacket) : Packet(9), postingUser(
        notificationPacket.postingUser), type(notificationPacket.type), content(notificationPacket.content) {
}

NotificationPacket &NotificationPacket::operator=(const NotificationPacket &notificationPacket) {
    content = notificationPacket.content;
    postingUser = notificationPacket.postingUser;
    type = notificationPacket.type;

    return *this;
}

std::string NotificationPacket::getPostingUser() {
    return postingUser;
}

std::string NotificationPacket::getContent() {
    return content;
}

short NotificationPacket::getType() {
    return type;
}


