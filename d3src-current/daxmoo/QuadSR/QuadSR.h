/*
Copyright 2004 Bert Szoghy
webmaster@quadmore.com
*/
#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "sphelper.h"
#include "SR.h"
#include "QuadmoreSR.h"
#include <sapi.h>
#include <stdio.h>
#include <string.h>
#include <atlbase.h>
#include "resource.h"
#include "quadinit.h"

static QUADSAPI::SetUpQUADSAPI setupQuadSapi;

HRESULT hr = E_FAIL;
QUADSAPI quadsapi;

QUADSAPI::SetUpQUADSAPI::SetUpQUADSAPI() {
	//AfxMessageBox("inside SetUpQUADSAPI creator");
	hr = ::CoInitialize(NULL);
}

QUADSAPI::SetUpQUADSAPI::~SetUpQUADSAPI() {
	//AfxMessageBox("inside SetUpQUADSAPI destructor");
	::CoUninitialize();
}

void QUADSAPI::UseQUADSAPI() {
	//AfxMessageBox("inside UseQUADSAPI()");
}

class CQuadSRApp : public CWinApp {
public:
	CQuadSRApp();

public:
	virtual BOOL InitInstance();

	DECLARE_MESSAGE_MAP()
};

inline HRESULT BlockForResult(ISpRecoContext * pRecoCtxt, ISpRecoResult ** ppResult) {
	HRESULT hr = S_OK;
	CSpEvent event;

	while (SUCCEEDED(hr) &&
		   SUCCEEDED(hr = event.GetFrom(pRecoCtxt)) &&
		   hr == S_FALSE) {
		hr = pRecoCtxt->WaitForNotifyEvent(INFINITE);
	}

	*ppResult = event.RecoResult();

	if (*ppResult) {
		(*ppResult)->AddRef();
	}

	return hr;
}

JNIEXPORT jstring JNICALL Java_daxclr_ext_QuadmoreSR_TakeDictation(JNIEnv *env, jobject obj) {
	if (SUCCEEDED(hr)) {
		CComPtr<ISpRecoContext> cpRecoCtxt;
		CComPtr<ISpRecoGrammar> cpGrammar;
		CComPtr<ISpVoice> cpVoice;
		hr = cpRecoCtxt.CoCreateInstance(CLSID_SpSharedRecoContext);

		if (SUCCEEDED(hr)) {
			hr = cpRecoCtxt->GetVoice(&cpVoice);

			if (cpRecoCtxt && cpVoice &&
				SUCCEEDED(hr = cpRecoCtxt->SetNotifyWin32Event()) &&
				SUCCEEDED(hr = cpRecoCtxt->SetInterest(SPFEI(SPEI_RECOGNITION), SPFEI(SPEI_RECOGNITION))) &&
				SUCCEEDED(hr = cpRecoCtxt->SetAudioOptions(SPAO_RETAIN_AUDIO, NULL, NULL)) &&
				SUCCEEDED(hr = cpRecoCtxt->CreateGrammar(0, &cpGrammar)) &&
				SUCCEEDED(hr = cpGrammar->LoadDictation(NULL, SPLO_STATIC)) &&
				SUCCEEDED(hr = cpGrammar->SetDictationState(SPRS_ACTIVE))) {
				USES_CONVERSION;
				CSpDynamicString dstrText;
				CString strGrab;
				int intGrabLenght;

				CComPtr<ISpRecoResult> cpResult;

				printf( "\nPlease begin dictation...\n");

				while (SUCCEEDED(hr = BlockForResult(cpRecoCtxt, &cpResult))) {
					cpGrammar->SetDictationState( SPRS_INACTIVE );

					if (SUCCEEDED(cpResult->GetText(SP_GETWHOLEPHRASE,SP_GETWHOLEPHRASE,TRUE,&dstrText,NULL))) {
						cpResult.Release();
						strGrab = dstrText;
						intGrabLenght = strGrab.GetLength();
						char* buf = strGrab.GetBuffer(intGrabLenght + 1);
						strGrab.ReleaseBuffer();
						return env->NewStringUTF(buf);
					} else {
						return(*env).NewStringUTF("(Reached end prematurely, could not create grammar. Ending...)");
					}

					cpGrammar->SetDictationState( SPRS_ACTIVE );
				}
			} else {
				return(*env).NewStringUTF("(Reached end prematurely, trying to get voice. Ending...)");
			}
		} else {
			return(*env).NewStringUTF("(Reached end prematurely, trying to get voice. Ending...)");
		}
	} else {
		return(*env).NewStringUTF("(Reached end prematurely, trying to initialize MFC libraries. Ending...)");
	}

	//Next line will never happen, but the compiler is giving me a warning about not
	//all control paths having a return value. I hate warnings...
	return(*env).NewStringUTF("\n");
}
