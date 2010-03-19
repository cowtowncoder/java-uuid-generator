#include <jni.h>

#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include <iphlpapi.h>

/* change following to 1 to get console output */
#define ENABLE_DEBUG_OUTPUT 0

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
	return JNI_VERSION_1_1;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
}


JNIEXPORT jboolean JNICALL
Java_com_ccg_net_ethernet_EthernetAddress_getLocalEthernet(JNIEnv *env, jobject obj, jint id, jbyteArray ea) {

	PIP_ADAPTER_INFO pAdapterInfo, pAdapter;
	ULONG ulOutBufLen;

	jboolean rc = JNI_FALSE;


 	pAdapterInfo = (IP_ADAPTER_INFO *) malloc( sizeof(IP_ADAPTER_INFO) );
	ulOutBufLen = sizeof(IP_ADAPTER_INFO);

	// Make an initial call to GetAdaptersInfo to get
	// the necessary size into the ulOutBufLen variable
	if (GetAdaptersInfo( pAdapterInfo, &ulOutBufLen) == ERROR_BUFFER_OVERFLOW) {
  		free(pAdapterInfo);
  		pAdapterInfo = (IP_ADAPTER_INFO *) malloc (ulOutBufLen);
	}

	if (GetAdaptersInfo( pAdapterInfo, &ulOutBufLen) == NO_ERROR) {

		// We have now a linked list of adapters. Go through that list...
		int adapter = 0;
		pAdapter = pAdapterInfo;
		while (pAdapter && adapter < id) {
			pAdapter = pAdapter->Next;
			adapter++;
		}

		 if (pAdapter && adapter == id) {

#if ENABLE_DEBUG_OUTPUT
			printf("\tAdapter Name: \t%s\n", pAdapter->AdapterName);
			printf("\tAdapter Desc: \t%s\n", pAdapter->Description);
			printf("\tAdapter Addr: \t%02X:%02X:%02X:%02X:%02X:%02X\n",
				pAdapter->Address[0], pAdapter->Address[1], pAdapter->Address[2],
				pAdapter->Address[3], pAdapter->Address[4], pAdapter->Address[5]);
#endif

			/* Transfer ethernet info */
			jbyte* ba = (*env)->GetByteArrayElements(env,ea,0);
			memcpy(ba,pAdapter->Address,6);
			(*env)->ReleaseByteArrayElements(env,ea,ba,0);

			rc = JNI_TRUE;
		}
	}

	free(pAdapterInfo);

#if ENABLE_DEBUG_OUTPUT
		printf("Returning: %i\n", rc);
#endif

	return rc;
}
