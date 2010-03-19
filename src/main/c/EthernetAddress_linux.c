#include <jni.h>

#include <sys/ioctl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <net/if.h>
#include <arpa/inet.h>

#include <unistd.h>
#include <stdio.h>

/* Implementation of the Ethernet MAC address access code for generic
 * Linux platform. Tested with 2.2.x and 2.4.x kernels; should be generic
 * enough to work on most all distributions and kernel versions.
 */
JNIEXPORT jboolean JNICALL 
Java_com_ccg_net_ethernet_EthernetAddress_getLocalEthernet(JNIEnv *env, jobject obj, jint id, jbyteArray ea) 
{
  int s, err;
  struct ifreq ifr;

  /* open a socket */
  s = socket(PF_INET, SOCK_DGRAM, 0);
  if (s == -1) {
    return JNI_FALSE;
  }

  sprintf(ifr.ifr_name,"eth%d",id);

  /* query information for a particular ethernet device */
  /* !!! 28-Mar-2005, TSa: Hmmh. This is a kludge, not only hard-coding the
   *   ethernet interface name, but assuming they are always consequtively
   *   numbered?
   */
  err = ioctl(s, SIOCGIFHWADDR, &ifr);
  /* 28-Mar-2005, TSa: need to close the socket in any case
   *    (as pointed out by Pekka Enberg)
   */
  close(s);

  if (err < 0) {
    return JNI_FALSE;
  }

  {
    /* transfer information into byte array passed */
    jbyte* ba = (*env)->GetByteArrayElements(env,ea,0);
    struct sockaddr* sa = (struct sockaddr *) &ifr.ifr_addr;
    int i;
    for (i = 0; i < 6; i++) {
      ba[i] = sa->sa_data[i];
    }

    (*env)->ReleaseByteArrayElements(env,ea,ba,0);
  }

  return JNI_TRUE;
}
