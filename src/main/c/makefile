# ---------------------------------------------------------- -*- Makefile -*- #
# $Id: makefile,v 1.2 2001/10/11 21:13:28 pkb Exp $
# --------------------------------------------------------------------------- #

# Note: does not contain MacOS build

ALL.M4 := $(wildcard *.m4)

ALL.JAVA := $(wildcard *.java)

ALL.DIRS = doc-files-src

JAVA_CLASS_DIR = ../../build/classes

# Set this manually if JAVA_HOME not defined...?
JDKHOME = $(JAVA_HOME)

# --------------------------------------------- #
# Common stuff for EthernetAddress native library
# --------------------------------------------- #

all:	native

EthernetAddress.h::	makefile
	javah -classpath ${JAVA_CLASS_DIR} -jni com.ccg.net.ethernet.EthernetAddress

ARCH=x86
ifdef HOSTTYPE
ifeq ($(findstring 86,$(HOSTTYPE)),)
ARCH=$(HOSTTYPE)
endif
endif

OS=linux
ifdef OSTYPE
ifeq ($(findstring inux,$(OSTYPE)),)
OS=$(OSTYPE)
endif
endif

ifneq ($(findstring ind,$(OSTYPE)),)
LIB_NAME=Win_$(ARCH)_EtherAddr.dll
else
LIB_NAME=lib$(OS)_$(ARCH)_EtherAddr.so
endif

COMMON_DEP_FILES=com_ccg_net_ethernet_EthernetAddress.h

# GNU/Linux gcc compiler
#
# gcc -O2 -shared -I$JDKHOME/include -I$JDKHOME/include/linux MachineId.c -o libMachineId.so

LINUX_FILES=EthernetAddress_linux.c
LINUX_LIB=liblinux_$(ARCH)_EtherAddr.so
LINUX_DEP_FILES=$(LINUX_FILES) $(COMMON_DEP_FILES)

$(LINUX_LIB)::	$(LINUX_DEP_FILES)
	gcc -O2 -shared -I$(JDKHOME)/include $(LINUX_FILES) -o $@

# Sparc compiler:
# cc -G -I$(JDKHOME)/include EthernetAddress.c -o libEthernetAddress.so

SOLARIS_FILES=EthernetAddress_solaris.c
SOLARIS_LIB=libsolaris_$(ARCH)_EtherAddr.so
SOLARIS_DEP_FILES=$(SOLARIS_FILES) $(COMMON_DEP_FILES)

$(SOLARIS_LIB)::	$(SOLARIS_DEP_FILES)
	cc -O2 -G -I$(JDKHOME)/include $(SOLARIS_FILES) -o $@

# Windows: 
# cl /Ox /G3 -I%JDKHOME%\include -I%JDKHOME%\include\win32 -LD EthernetAddress.c -FeEthernetAddress_win_x86.dll netapi32.lib

WINDOWS_FILES=EthernetAddress_win.c
WINDOWS_LIB=win_$(ARCH)_EtherAddr.dll
WINDOWS_DEP_FILES=$(WINDOWS_FILES) $(COMMON_DEP_FILES)

$(WINDOWS_LIB)::	$(WINDOWS_DEP_FILES)
	cl -O -LD -I%JDKHOME%\include -I%JDKHOME%\include\win32 \
		$(WINDOWS_FILES) -Fe$@ netapi32.lib


native::	$(LIB_NAME)

help::
	@echo	native - build native library $(LIB_NAME) os [$(OS)] arch [$(ARCH)] hosttype [$(HOSTTYPE)] ostype [$(OSTYPE)] oscap [$(OSCAP)]
