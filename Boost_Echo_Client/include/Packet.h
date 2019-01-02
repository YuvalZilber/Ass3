//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_PACKET_H
#define BOOST_ECHO_CLIENT_PACKET_H


#include <string>

class Packet {
private:
    short opCode;
    std::string msgcontent;
    int length;
public:
    Packet();
    Packet(short opcode);
    Packet(short _opCode,std::string msg,int plength);
    Packet(const Packet& packet);
    virtual Packet& operator=(const Packet& packet);
    ~Packet()= default;
    short GetOpcode();
    std::string GetMsg() const;
    void SetMsg(std::string toset);
 //   virtual int size()=0;
    int getSize();
};


#endif //BOOST_ECHO_CLIENT_PACKET_H
