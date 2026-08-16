cmake_minimum_required(VERSION 3.22.1)
project(stylo_android LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(oboe REQUIRED CONFIG)

add_library(stylo_dsp_core SHARED IMPORTED)
set_target_properties(stylo_dsp_core PROPERTIES
    IMPORTED_LOCATION "${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}/libstylo_dsp_core.so"
)

add_library(stylo_android SHARED
    native-lib.cpp
)

target_link_libraries(stylo_android
    PRIVATE
    oboe::oboe
    stylo_dsp_core
    log
    android
)
