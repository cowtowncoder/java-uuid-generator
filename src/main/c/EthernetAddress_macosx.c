#include <jni.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <ifaddrs.h>
#include <net/if_dl.h>

/*
 * These calls should work for any of the *BSD variants that have 
 * a "getifaddrs" call.  Info gathered from the freebsd-hackers list:
 * http://docs.freebsd.org/cgi/getmsg.cgi?fetch=358524+0+archive/2001/freebsd-hackers/20010805.freebsd-hackers
 * which indicates that the code originally came from NetBSD's ifconfig.c.
 *
 * build library with:

cc -c -I/System/Library/Frameworks/JavaVM.framework/Headers EthernetAddress_macosx.c
cc -dynamiclib -o libMacOSX_ppc_EtherAddr.jnilib EthernetAddress_macosx.o -framework JavaVM

 */

JNIEXPORT jboolean JNICALL 
Java_com_ccg_net_ethernet_EthernetAddress_getLocalEthernet(JNIEnv *env, jobject obj, jint id, jbyteArray ea) 
{
  struct ifaddrs *ifap, *ifaphead;
  const struct sockaddr_dl *sdl;
  int rtnerr, alen, i;
  caddr_t ap;

  rtnerr = getifaddrs(&ifaphead);
  if (rtnerr) {
    return JNI_FALSE;
  }

  for (ifap = ifaphead; ifap; ifap = ifap->ifa_next) {
    if( ifap->ifa_addr && ifap->ifa_addr->sa_family == AF_LINK) {
      sdl = (const struct sockaddr_dl*)ifap->ifa_addr;
      ap = ((caddr_t)((sdl)->sdl_data + (sdl)->sdl_nlen));
      alen = sdl->sdl_alen;
      /* 28-Mar-2005, TSa: Fixed as suggested by Thomas Wernitz
       *    (and DJ Hagberg, Klaus Rheinwald)
       */
      if (ap && alen > 0 && --id < 1) {
        /* transfer info into java byte array */
        jbyte* ba = (*env)->GetByteArrayElements(env,ea,0);
        for (i=0; i < 6 && i < alen; i++, ap++) {
          ba[i] = 0xff&*ap;
        }
        (*env)->ReleaseByteArrayElements(env,ea,ba,0);
        freeifaddrs(ifaphead);
        return JNI_TRUE;
      }
    }
  }

  freeifaddrs(ifaphead);
  return JNI_FALSE;
}
