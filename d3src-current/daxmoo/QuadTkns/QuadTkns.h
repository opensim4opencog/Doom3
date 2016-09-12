/*
Quadmore JAVA to SAPI bridge
Version 3, um, alpha
Copyright 2004 Bert Szoghy
webmaster@quadmore.com
*/
#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "sphelper.h"
#include "TokensTTS.h"
#include <sapi.h>
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <atlbase.h>
#include "resource.h"
#include "quadinit.h"

using namespace std;

static QUADSAPI::SetUpQUADSAPI setupQuadSapi;

//Yes I know this is not pretty:
CString strVoiceSelected = "";

HRESULT hr = E_FAIL;
QUADSAPI quadsapi;

QUADSAPI::SetUpQUADSAPI::SetUpQUADSAPI()
{
	//AfxMessageBox("inside SetUpQUADSAPI creator");
	hr = ::CoInitialize(NULL);
}

QUADSAPI::SetUpQUADSAPI::~SetUpQUADSAPI()
{
	//AfxMessageBox("inside SetUpQUADSAPI destructor");
	::CoUninitialize();
}

void QUADSAPI::UseQUADSAPI()
{
	//AfxMessageBox("inside UseQUADSAPI()");
}

class CQuadTknsApp : public CWinApp
{
public:
	CQuadTknsApp();

public:
	virtual BOOL InitInstance();

	DECLARE_MESSAGE_MAP()
};

JNIEXPORT jstring JNICALL Java_daxclr_ext_TokensTTS_getVoiceToken(JNIEnv *env, jobject obj)
{
	CString strConcatenateXML = "<?xml version=\"1.0\"?>";
	int intLenght;

	ISpVoice * pVoice = NULL;

	HRESULT hr = CoCreateInstance(CLSID_SpVoice, NULL, CLSCTX_ALL, IID_ISpVoice, (void **)&pVoice);
	if(SUCCEEDED(hr))
	{
		WCHAR   **m_ppszTokenIds;
		USES_CONVERSION;
		CComPtr<IEnumSpObjectTokens>    cpEnum;
		CSpDynamicString*				szDescription;
		ISpObjectToken                  *pToken = NULL;
		CComPtr<ISpObjectToken>         cpVoiceToken; //the token is the voice
		CComPtr<ISpVoice>               cpVoice;
		ULONG                           ulCount = 0;

		if(SUCCEEDED(hr))
		{
			hr = cpVoice.CoCreateInstance( CLSID_SpVoice );

			if(SUCCEEDED(hr))
			{
				WCHAR *pszCurTokenId = NULL;
				ULONG ulIndex = 0, ulNumTokens = 0, ulCurToken = -1;

				hr = SpEnumTokens(SPCAT_VOICES, NULL, NULL, &cpEnum);

				if (hr == S_OK)
				{
					hr = cpEnum->GetCount( &ulNumTokens );

					if (SUCCEEDED(hr) && ulNumTokens != 0)
					{
						szDescription = new CSpDynamicString [ulNumTokens];
						m_ppszTokenIds = new WCHAR* [ulNumTokens];

						ZeroMemory(m_ppszTokenIds, ulNumTokens * sizeof(WCHAR *));

						UINT i =0;
						while (cpEnum->Next(1, &pToken, NULL) == S_OK)
						{
							//Don't care about return value in next line:
							hr = SpGetDescription(pToken, &szDescription[ulIndex]);
							ulIndex++;

							strConcatenateXML += "<voice>" + CString(szDescription[i]) + "</voice>";

							pToken->Release();
							pToken = NULL;
							i++;
						}

						delete [] szDescription;
					}
					else
					{
						strConcatenateXML = "No voice found. (5)";
					}
				}
				else
				{
					strConcatenateXML = "No voice found. (4)";
				}
			}
			else
			{
				strConcatenateXML = "No voice found. (3)";
			}
		}
		else
		{
			strConcatenateXML = "No voice found. (2)";
		}
	}
	else
	{
		strConcatenateXML = "No voice found. (1)";
	}

	intLenght = strConcatenateXML.GetLength();
	char* buf = strConcatenateXML.GetBuffer(intLenght + 1);
	strConcatenateXML.ReleaseBuffer();
	return env->NewStringUTF(buf);
}
