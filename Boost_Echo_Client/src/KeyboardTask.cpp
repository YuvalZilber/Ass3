//
// Created by shvatm on 12/31/18.
//

#include <KeyboardTask.h>

#include "KeyboardTask.h"
//ConnectionHandler* connectionHandler;
//    Protocol* protocol;

KeyboardTask::KeyboardTask(ConnectionHandler *conHand, Protocol* aProtocol):packet(nullptr) {
connectionHandler=conHand;
protocol=aProtocol;

}

void KeyboardTask::operator()() {
    while (1) {// we wants to read as long as there is a connection to the server
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len =line.length();
        std::vector<std::string> words=split(line," ");
        if (words[0]=="REGISTER"){
            int size=2+words[1].length()+2+words[2].length();
            packet=new Packet((short)1,line,size);


        }
        if (words[0]=="LOGIN"){
            int size=2+words[1].length()+2+words[2].length();
            packet=new Packet((short)2,line,size);

        }
        if (words[0]=="LOGOUT"){
            packet=new Packet((short)3,line,2);

        }
        if (words[0]=="FOLLOW"){
            int userListSize=0;
            for (int i = 2; i <words.size() ; ++i) {
                userListSize=userListSize+words[i].length();
            }
            int size=2+1+2+userListSize;
            packet=new Packet((short)4,line,size);

        }
        if (words[0]=="POST"){
            int size=3+words[1].length();
            packet=new Packet((short)5,line,size);

        }
        if (words[0]=="PM"){
            int size=4+words[1].length()+words[2].length();
            packet=new Packet((short)6,line,size);

        }
        if (words[0]=="USERLIST"){
            int size=7+words[3].length();
            packet=new Packet((short)7,line,size);

        }
        if (words[0]=="STAT"){
            int size=3+words[1].length();
            packet=new Packet((short)3,line,size);

        }

        bool connected=connectionHandler->sendPacket(*packet);

        if (!connected) {//if there's no connection with the server
            break;
        }
    }
}

std::vector<std::string> KeyboardTask::split(std::string s, std::string delimiter) {
    std::vector<std::string> list;
    size_t pos = 0;
    std::string token;
    while ((pos = s.find(delimiter)) != std::string::npos) {
        token = s.substr(0, pos);
        list.push_back(token);
        s.erase(0, pos + delimiter.length());
    }
    list.push_back(s);

    return list;
}
