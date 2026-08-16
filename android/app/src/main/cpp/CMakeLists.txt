cmake_minimum_required(VERSION 3.22.1)
project(stylo_dsp_bridge LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(stylo_dsp_core SHARED IMPORTED)
set_target_properties(stylo_dsp_core PROPERTIES
    IMPORTED_LOCATION "${CMAKE_CURRENT_LIST_DIR}/../jniLibs/arm64-v8a/libstylo_dsp_core.so"
)

find_package(oboe REQUIRED CONFIG)

add_library(stylo_dsp_bridge SHARED native-lib.cpp)

target_link_libraries(stylo_dsp_bridge
    PRIVATE
    stylo_dsp_core
    oboe::oboe
    log
    android
)
