#include <utility>

//
// Created by shvatm on 12/29/18.
//

#include <Packet.h>

#include "Packet.h"
//varaibles:
// short opCode;

Packet::Packet():opCode(-1),msgcontent(""),length(0) {}

Packet::Packet(const Packet &packet):opCode(packet.opCode),length(packet.length) {}

Packet::Packet(short _opCode,std::string msg,int plength):opCode(_opCode),msgcontent(msg),length(plength) {}

Packet &Packet::operator=(const Packet &packet) {
    opCode=packet.opCode;
    return *this;
}

short Packet::GetOpcode() {
    return this->opCode;
}

std::string Packet::GetMsg() const {
    return msgcontent;
}

void Packet::SetMsg(std::string toset) {
msgcontent= std::move(toset);
}

int Packet::getSize() {
    return length;
}

Packet::Packet(short opcode) {
opCode=opcode;
}






