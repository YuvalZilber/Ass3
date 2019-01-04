//
// Created by shvatm on 12/30/18.
//

#include <Protocol.h>
#include <NotificationPacket.h>
#include <ErrorPacket.h>
#include <AckPacket.h>
#include <AckFollowPacket.h>
#include <AckUserListPacket.h>
#include <StatAckPacket.h>

#include "Protocol.h"

Protocol::Protocol(ConnectionHandler *connectionHandler_):connectionHand(connectionHandler_) {

}

Protocol::Protocol(const Protocol &aProtocol):connectionHand(aProtocol.connectionHand) {

}

Protocol &Protocol::operator=(const Protocol &aProtocol) {
    connectionHand = aProtocol.connectionHand;
    return *this;
}

void Protocol::process(Packet *p) {
    short opcode = p->GetOpcode();
    switch (opcode) {
        case 9: {
            NotificationPacket *notificationPacket = dynamic_cast<NotificationPacket *>(p);
            std::string notype = "";
            if (notificationPacket->getType() == 1)
                notype = "Public";
            else
                notype = "PM";
            std::string postingUser = notificationPacket->getPostingUser();
            std::string content = notificationPacket->getContent();

            std::cout << "NOTIFICATION " + notype + " " + postingUser + " " + content << std::endl;
            break;
        }
        case 10: {
            AckPacket *ackPacket = dynamic_cast<AckPacket *>(p);
            short msgCode = ackPacket->GetMsgOpcode();
            switch (msgCode) {
                case 3: {
                    connectionHand->ApproveDisconnect();//approving the logout
                    connectionHand->close();

                    break;
                }
                case 4: {
                    AckFollowPacket *ackFollowPacket = dynamic_cast<AckFollowPacket *>(p);
                    short numof = ackFollowPacket->GetNumOfUsers();
                    std::string userNameList = ackFollowPacket->GetUserNameList();
                    std::cout << "ACK " << msgCode << " " << numof << " " << userNameList << std::endl;

                    break;
                }
                case 7: {
                    AckUserListPacket *userListPacket = dynamic_cast<AckUserListPacket *>(p);
                    short numof = userListPacket->GetNumOfUsers();
                    std::string userNameList = userListPacket->GetUserNameList();
                    std::cout << "ACK " << msgCode << " " << numof << " " << userNameList << std::endl;
                    break;
                }
                case 8: {
                    StatAckPacket *statAckPacket = dynamic_cast<StatAckPacket *>(p);
                    short numposts = statAckPacket->GetNumOfPosts();
                    short numfollowers = statAckPacket->GetNumOfFollowers();
                    short numfollowing = statAckPacket->GetNumOfFollowing();
                    std::cout << "ACK " << msgCode << " " << numposts << " " << numfollowers << " " << numfollowing
                              << std::endl;
                    break;
                }

                default: {
                    std::cout << "ACK " << msgCode << std::endl;
                    break;
                }


            }

            break;
        }
        case 11: {
            ErrorPacket *errorPacket = dynamic_cast<ErrorPacket *>(p);
            short msgopcode = errorPacket->GetmsgOpcode();
            std::cout << "Error " << msgopcode << std::endl;
            break;
        }
        default: {
            std::string tosend = p->GetMsg();
            connectionHand->sendLine(tosend);
        }

    }

}
