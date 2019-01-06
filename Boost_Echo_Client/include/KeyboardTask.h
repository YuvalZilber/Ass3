//
// Created by shvatm on 12/31/18.
//

#ifndef BOOST_ECHO_CLIENT_KEYBOARDTASK_H
#define BOOST_ECHO_CLIENT_KEYBOARDTASK_H


#include <ConnectionHandler.h>
#include <Protocol.h>

class KeyboardTask {
private:
    Packet* packet;
    Protocol* protocol;
    ConnectionHandler* connectionHandler;
public:
    KeyboardTask(ConnectionHandler *conHand,Protocol* aProtocol);
    std:: vector<std::string> split(std::string s,std:: string delimiter);
    void operator()();
    virtual ~KeyboardTask();
};


#endif //BOOST_ECHO_CLIENT_KEYBOARDTASK_H
