// QuadSR.cpp : Defines the initialization routines for the DLL.
//

#include "stdafx.h"
#include "QuadTkns.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif


BEGIN_MESSAGE_MAP(CQuadTknsApp, CWinApp)
	//{{AFX_MSG_MAP(CQuadSRApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CQuadSRApp construction

CQuadTknsApp::CQuadTknsApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CQuadSRApp object

CQuadTknsApp theApp;

BOOL CQuadTknsApp::InitInstance()
{
	CWinApp::InitInstance();

	return TRUE;
}
