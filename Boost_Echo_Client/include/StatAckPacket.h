//
// Created by shvatm on 12/29/18.
//

#ifndef BOOST_ECHO_CLIENT_STATACKPACKET_H
#define BOOST_ECHO_CLIENT_STATACKPACKET_H

#include "AckPacket.h"

class StatAckPacket :public AckPacket{
private:

short numOfPosts;
short numOfFollowers;//pepole who follows the client
short numOfFollowing;//who the client follows

public:
    StatAckPacket();
    StatAckPacket(short _numOfPosts, short _numOfFollowers, short _numOfFollowing);
    StatAckPacket(const StatAckPacket&statAckPacket);
    StatAckPacket& operator=(const StatAckPacket&statAckPacket);
    ~StatAckPacket()= default;
    short GetNumOfPosts();
    short GetNumOfFollowers();
    short GetNumOfFollowing();
};


#endif //BOOST_ECHO_CLIENT_STATACKPACKET_H
