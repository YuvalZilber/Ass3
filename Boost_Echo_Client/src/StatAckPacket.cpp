//
// Created by shvatm on 12/29/18.
//

#include "StatAckPacket.h"
//short numOfUsers;
//short numOfPosts;
//short numOfFollowers;//pepole who follows the client
//short numOfFollowing;//who the client follows

StatAckPacket::StatAckPacket():AckPacket(8),numOfPosts(0),numOfFollowers(0),numOfFollowing(0){}

StatAckPacket::StatAckPacket(short _numOfPosts, short _numOfFollowers, short _numOfFollowing):AckPacket(8) {
numOfFollowing=_numOfFollowing;
numOfFollowers=_numOfFollowers;
numOfPosts=_numOfPosts;


}

StatAckPacket::StatAckPacket(const StatAckPacket &statAckPacket):AckPacket(8) {

numOfPosts=statAckPacket.numOfPosts;
numOfFollowers=statAckPacket.numOfFollowers;
numOfFollowing=statAckPacket.numOfFollowing;
}

StatAckPacket &StatAckPacket::operator=(const StatAckPacket &statAckPacket) {

    numOfPosts=statAckPacket.numOfPosts;
    numOfFollowers=statAckPacket.numOfFollowers;
    numOfFollowing=statAckPacket.numOfFollowing;
    return *this;
}



short StatAckPacket::GetNumOfPosts() {
    return numOfPosts;
}

short StatAckPacket::GetNumOfFollowers() {
    return numOfFollowers;
}

short StatAckPacket::GetNumOfFollowing() {
    return numOfFollowing;
}
