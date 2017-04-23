# CMake 版本信息
cmake_minimum_required(VERSION 3.4.1)

# 支持 -std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 工程路径
set(pathToProject D:/android_studio_workspace/Summary1704)
# Module 名称
set(moduleName library)

# SO 路径
set(pathToSO ${pathToProject}/${moduleName}/src/main/jniLibs)
# Native 路径
set(pathToNative ${pathToProject}/${moduleName}/src/main/cpp)

# --------编译并加载 OpenCV--------
# 配置加载 Native 依赖
include_directories(${pathToNative}/opencv_3_2_0)

# 添加待编译的文件
add_library(detection_based_tracker SHARED ${pathToNative}/opencv/DetectionBasedTracker_jni.h
                                           ${pathToNative}/opencv/DetectionBasedTracker_jni.cpp)

# 动态方式引入 SO 文件
add_library(SO_OpenCV SHARED IMPORTED)
set_target_properties(SO_OpenCV PROPERTIES IMPORTED_LOCATION ${pathToSO}/${ANDROID_ABI}/libopencv_java3.so)

# 编译并加载
target_link_libraries(detection_based_tracker
                      SO_OpenCV
                      log)

# --------编译并加载 EasyAR--------
include_directories(${pathToNative}/easyar_1_3_1)
add_library(helloarvideo SHARED ${pathToNative}/easyar/ar.hpp
                                ${pathToNative}/easyar/renderer.hpp
                                ${pathToNative}/easyar/ar.cc
                                ${pathToNative}/easyar/renderer.cc
                                ${pathToNative}/easyar/helloarvideo.cc)
add_library(SO_EasyAR SHARED IMPORTED)
set_target_properties(SO_EasyAR PROPERTIES IMPORTED_LOCATION ${pathToSO}/${ANDROID_ABI}/libEasyAR.so)
target_link_libraries(helloarvideo
                      SO_EasyAR
                      log GLESv2)

# --------编译并加载 ARToolKit--------
include_directories(${pathToNative}/artoolkit_5_3_2)
include_directories(${pathToNative}/artoolkit_5_3_2/android)
add_library(nft SHARED ${pathToNative}/artoolkit/ARMarkerNFT.c
                       ${pathToNative}/artoolkit/ARMarkerNFT.h
                       ${pathToNative}/artoolkit/color_convert_common.c
                       ${pathToNative}/artoolkit/color_convert_common.h
                       ${pathToNative}/artoolkit/nftBook.cpp
                       ${pathToNative}/artoolkit/osgPlugins.h
                       ${pathToNative}/artoolkit/trackingSub.c
                       ${pathToNative}/artoolkit/trackingSub.h
                       ${pathToNative}/artoolkit/VirtualEnvironment.c
                       ${pathToNative}/artoolkit/VirtualEnvironment.h)
add_library(SO_c++_shared SHARED IMPORTED)
set_target_properties(SO_c++_shared PROPERTIES IMPORTED_LOCATION ${pathToSO}/${ANDROID_ABI}/libc++_shared.so)
add_library(SO_nftBookNative SHARED IMPORTED)
set_target_properties(SO_nftBookNative PROPERTIES IMPORTED_LOCATION ${pathToSO}/${ANDROID_ABI}/libnftBookNative.so)
target_link_libraries(nft
                      SO_c++_shared SO_nftBookNative
                      log GLESv1_CM z)