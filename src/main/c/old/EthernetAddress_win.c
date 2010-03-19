/*--------------------------------------------------------------
  The portions of related to the WIN32 API were found by
  searching the internet (try looking for
  "getmac-netbios.cpp"). Borland had some examples as well.
  --------------------------------------------------------------*/

#include <jni.h>

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>

/* change following to 1 to get console output */
#define ENABLE_DEBUG_OUTPUT 0

JNIEXPORT jboolean JNICALL 
Java_com_ccg_net_ethernet_EthernetAddress_getLocalEthernet(JNIEnv *env, jobject obj, jint id, jbyteArray ea) 
{
	int lana_num = -1; /* LAN adapter number */

	struct ASTAT {
		ADAPTER_STATUS adapt;
		NAME_BUFFER NameBuff[30];
	} Adapter;
	NCB Ncb;

	/*--------------------------------------------------------------
	Java API always iterates from 0 up, however windows doesn't
	necessarily map the first adapter to 0 the next to 1 and so
	on. This section retrieves the windows "map" of its ethernet
	adapters and then picks out the one which matches the user
	request
	--------------------------------------------------------------*/

	{
		LANA_ENUM lenum;
		UCHAR uRetCode;
		int li = 0;
		int ln = 0;

		memset(&Ncb,0,sizeof(Ncb));
		Ncb.ncb_command = NCBENUM;
		Ncb.ncb_buffer = (UCHAR*)&lenum;
		Ncb.ncb_length = sizeof(lenum);
		uRetCode = Netbios(&Ncb);
		if (uRetCode != 0) return JNI_FALSE;

#if ENABLE_DEBUG_OUTPUT
	    printf("found %d adapaters\n",lenum.length);
#endif

		for (; ((li < lenum.length) && (lana_num == -1)) ; li++) {
			lana_num = lenum.lana[li];
			/* prepare to get adapter status block */
			memset(&Ncb, 0, sizeof(Ncb));
			Ncb.ncb_command = NCBRESET;
			Ncb.ncb_lana_num = lana_num;
			if (Netbios(&Ncb) != NRC_GOODRET) 
				return JNI_FALSE;

			/* OK, lets go fetch ethernet address */
			memset(&Ncb, 0, sizeof(Ncb));
			Ncb.ncb_command = NCBASTAT;
			Ncb.ncb_lana_num = lana_num;
			strcpy((char *) Ncb.ncb_callname, "*");

			memset(&Adapter, 0, sizeof(Adapter));
			Ncb.ncb_buffer = (unsigned char *)&Adapter;
			Ncb.ncb_length = sizeof(Adapter);
			/* if unable to determine, exit false */
			if (Netbios(&Ncb) != 0) 
				return JNI_FALSE;

			/* if correct type, then see if its
			the one we want to check */
			if ((Adapter.adapt.adapter_type & 0xff) == 0xfe) {
//				if (ln == id) 
//					break;
				if (ln != id) {
				/* right type, wrong number */
#if ENABLE_DEBUG_OUTPUT
				printf("skipping adapter %d - right type %x\n",
					lenum.lana[li], (Adapter.adapt.adapter_type & 0xff));
#endif
					lana_num = -1;
					ln++;
				}
			}
			else {
				lana_num = -1;		/* this one wasn't it - clear OK indicator */
#if ENABLE_DEBUG_OUTPUT
				printf("skipping adapter %d - type %x\n",
					lenum.lana[li], (Adapter.adapt.adapter_type & 0xff));
#endif
			}
		}
		if (lana_num == -1) {
		  return JNI_FALSE;
		}
	}

	/*--------------------------------------------------------------  
	  finally, transfer ethernet info
	  --------------------------------------------------------------*/

	{
		jbyte* ba = (*env)->GetByteArrayElements(env,ea,0);
		int i;
		for (i = 0; i < 6; i++) {
			ba[i] = Adapter.adapt.adapter_address[i];
		}

		(*env)->ReleaseByteArrayElements(env,ea,ba,0);
	}

	return JNI_TRUE;
}
