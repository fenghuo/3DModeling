LOCAL_PATH := $(call my-dir) 
include $(CLEAR_VARS)
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include E:\Tech\Android\adt-bundle-windows-x86_64-20131030\OpenCV-2.4.8-android-sdk\sdk\native\jni\OpenCV.mk
LOCAL_MODULE := EdgeDetect 
LOCAL_SRC_FILES := EdgeDetect.cpp 
LOCAL_LDLIBS += -llog -ldl
include $(BUILD_SHARED_LIBRARY)