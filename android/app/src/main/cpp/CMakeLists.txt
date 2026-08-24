cmake_minimum_required(VERSION 3.22.1)
project(stylo_android)
add_library(stylo_android SHARED native-lib.cpp)
target_link_libraries(stylo_android log)
