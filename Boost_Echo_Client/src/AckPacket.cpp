//
// Created by shvatm on 12/29/18.
//

#include <AckPacket.h>

#include "AckPacket.h"
// short msgOpcode;
AckPacket::AckPacket():Packet(10),msgOpcode(0) {}

AckPacket::AckPacket(short _msgopCode) :Packet(10),msgOpcode(_msgopCode) {}

AckPacket::AckPacket(const AckPacket &packet):Packet(10),msgOpcode(packet.msgOpcode) {}

AckPacket &AckPacket::operator=(const AckPacket &packet) {
    msgOpcode=packet.msgOpcode;
    return *this;
}

short AckPacket::GetMsgOpcode() {
    return msgOpcode;
}

void AckPacket::SetMsgOpcode(short msg) {
this->msgOpcode=msg;
}
