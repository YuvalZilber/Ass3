//
// Created by shvatm on 12/31/18.
//

#include <iostream>
#include <ConnectionHandler.h>
#include <Protocol.h>
#include <KeyboardTask.h>
#include "Client.h"
#include <boost/thread.hpp>

using namespace std;

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);

    Protocol protocol(&connectionHandler);

    if (!connectionHandler.connect()) { //handeling fail in connection
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    KeyboardTask readTask(&connectionHandler, &protocol);
    boost::thread th1(readTask);


    while (1) {
        Packet *p = connectionHandler.decodeNextMessage();
        protocol.process(p);
        if (connectionHandler.IsDisconnectApproved())
            break;
    }

    return 0;
}