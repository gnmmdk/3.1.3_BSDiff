cmake_minimum_required(VERSION 3.4.1)

#include_directories是用来提供找头文件路径
include_directories(${CMAKE_SOURCE_DIR}/bzip2)
# 查找当前目录下的所有源文件
# 并将名称保存到 bzip2_src 变量
aux_source_directory(${CMAKE_SOURCE_DIR}/bzip2/ bzip2_src)

add_library(
        native-lib
        SHARED
        native-lib.cpp
        bspatch.c
        ${bzip2_src}
)

target_link_libraries(
        native-lib
        log)