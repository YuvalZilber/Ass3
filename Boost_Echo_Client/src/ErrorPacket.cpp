//
// Created by shvatm on 12/29/18.
//

#include "ErrorPacket.h"

ErrorPacket::ErrorPacket():Packet(11),msgOpcode(0) {
}

ErrorPacket::ErrorPacket(short _msgopcode) :Packet(11), msgOpcode(_msgopcode) {

}

ErrorPacket::ErrorPacket(const ErrorPacket &errorPacket):Packet(11),msgOpcode(errorPacket.msgOpcode) {}

ErrorPacket &ErrorPacket::operator=(const ErrorPacket &errorPacket) {
    msgOpcode=errorPacket.msgOpcode;
    return *this;
}

short ErrorPacket::GetmsgOpcode() {
    return msgOpcode;
}
