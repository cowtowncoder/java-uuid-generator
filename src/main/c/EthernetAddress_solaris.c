#include <jni.h>

#include <arpa/inet.h>
#include <netdb.h>
#include <net/if.h>
#include <net/if_arp.h>
#include <netinet/in.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/sockio.h>
#include <sys/types.h>

#include <unistd.h>
#include <stdio.h>
#include <string.h>

JNIEXPORT jboolean JNICALL 
Java_com_ccg_net_ethernet_EthernetAddress_getLocalEthernet(JNIEnv *env, jobject obj, jint id, jbyteArray ea) 
{
  struct hostent hostentBuf;
  struct hostent *phost;
  char **paddrs;
  struct arpreq        ar;
  struct sockaddr_in * psa;
  int s,i,herr;
  char name[MAXHOSTNAMELEN];
  char hbuf[512];

  /* !!! 28-Mar-2005, TSa: Hmmh. This is not right, actually; won't return
   *   anything but the first interface's MAC address?
   */
  if ((id != 0) || gethostname(name,sizeof(name))) {
    return JNI_FALSE;
  }

  /* get this host name */
  phost = gethostbyname_r(name, &hostentBuf, hbuf, sizeof(hbuf), &herr);
  if (phost == 0) return JNI_FALSE;

  /* open a socket */
  s = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
  if (s == -1) return JNI_FALSE;

  paddrs = phost->h_addr_list;
  psa    = ( struct sockaddr_in * )&( ar.arp_pa );
  memset( &ar, 0, sizeof( struct arpreq ) );
  psa->sin_family = AF_INET;
  memcpy( &( psa->sin_addr ), *paddrs, sizeof( struct in_addr ) );
  if ( ioctl( s, SIOCGARP, &ar ) == -1 ) {
    perror("ioctl");
    close(s);
    return JNI_FALSE;
  }
  close(s);
  {				
    /* transfer information into byte array passed */
    jbyte* ba = (*env)->GetByteArrayElements(env,ea,0);
    int i;
    for (i = 0; i < 6; i++) {
      ba[i] = ar.arp_ha.sa_data[i];
    }

    (*env)->ReleaseByteArrayElements(env,ea,ba,0);
  }

  return JNI_TRUE;
}
