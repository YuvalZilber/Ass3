cmake_minimum_required(VERSION 3.13)
project(Boost_Echo_Client)

set(CMAKE_CXX_STANDARD 14)

find_package(Boost COMPONENTS system filesystem thread REQUIRED)
IF (Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIR})
endif()

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread -lboost_thread")

include_directories(bin)
include_directories(include)
include_directories(src)

add_executable(Boost_Echo_Client
        include/ConnectionHandler.h
        src/ConnectionHandler.cpp
        src/echoClient.cpp
        makefile src/EncoderDecoder.cpp
        include/EncoderDecoder.h
        src/Packet.cpp include/Packet.h
        src/NotificationPacket.cpp include/NotificationPacket.h
        src/AckPacket.cpp include/AckPacket.h src/AckFollowPacket.cpp
        include/AckFollowPacket.h src/AckUserListPacket.cpp
        include/AckUserListPacket.h src/StatAckPacket.cpp
        include/StatAckPacket.h src/ErrorPacket.cpp include/ErrorPacket.h
        src/Protocol.cpp include/Protocol.h src/KeyboardTask.cpp include/KeyboardTask.h src/Client.cpp include/Client.h)


target_link_libraries(Boost_Echo_Client LINK_PUBLIC ${Boost_LIBRARIES} )