/* 
 * Copyright (c) 2005, Kino Coursey @ Daxtron Labs

 * CyN is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any later
 * version.
 *
 * CyN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.

 * You should have received a copy of the GNU General Public License along
 * with CyN; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

// CycConnection.cpp: implementation of the CycConnection class.
//
//////////////////////////////////////////////////////////////////////
//#include "stdafx.h"
#include "../../idlib/precompiled.h" 
#include "../../game/Game_local.h" 
#include "CycConnection.h"
#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
//#define new DEBUG_NEW
#endif

/*
* The CycList 
*/
CycList::CycList()
{
	Link=NULL;
	List=NULL;
}

 CycList::~CycList()
{
	 if ((Link!=NULL)&&(List !=NULL)) Link->s_free(List);
}

 idStr /*c*/ CycList::toString()
 {
	 idStr /*c*/ reply;
	 idStr /*c*/ *reply_p;
	 reply="";
	 reply_p = &reply;
	 if ((Link!=NULL)&&(List !=NULL))
		 Link->str_print(reply_p,List);
	 return reply;
 }

 int CycList::size()
 {
	 idStr /*c*/ reply;
	 reply="";
	 if ((Link!=NULL)&&(List !=NULL))
		 return Link->s_length(List);
	 return 0;
 }

 cell_t *CycList::cellN(int n)
 {
	 int i;
	 cell_t *listp;
	 
	 listp=List;
	 
	 for(i=0; ((i<n) && consp(listp)); i++)
	 {
		 //printf("cellN %d [%s]\n",i,cellToString(listp));
		 listp =(cell_t *) _Cdr(listp);
	 }

	 if (listp!=NULL) listp=(cell_t*)_Car(listp);
	 
	 return listp;
 }

  cell_t *CycList::cellN(int n,cell_t *slist)
 {
	 int i;
	 cell_t *listp;
	 
	 listp=slist;
	 
	 for(i=0; ((i<n) && consp(listp)); i++)
	 {
		 //printf("cellN %d [%s]\n",i,cellToString(listp));
		 listp =(cell_t *) _Cdr(listp);
	 }
	 if (listp!=NULL) listp=(cell_t*)_Car(listp);
	 
	 return listp;
 }


  CycList *CycList::get(int n)
  {
	CycList *retList;
	retList = new CycList;
	retList->List=cellN(n);
	return retList;
  }

 idStr /*c*/ CycList::bindingLookupKey(int answerset)
 {
	 cell_t *listp;

	 listp = cellN(answerset);
	 //printf("CycList::bindingLookupKey listp = [%s] \n",cellToString(listp));

	if (!nullp(listp) && !invalidp(listp) && !invalidp(_Car(listp)) )
		return cellToString((cell_t *)_Car(listp));
	else
		return cellToString(listp);
 }
 idStr /*c*/ CycList::bindingLookupValue(int answerset)
 {
	 cell_t *listp;
	 cell_t *candidate;
	 idStr /*c*/ x;

	 listp = cellN(answerset);
	 candidate = (cell_t *)_Cdr(_Cdr(listp));
	 //printf("CycList::bindingLookupValue listp = [%s] \n",cellToString(listp));
	 //printf("CycList::bindingLookupValue candidate = [%s] \n",cellToString(candidate));

	 if (Link->s_length(candidate)>1)
			return cellToString((cell_t *)_Car(_Cdr(candidate))); // its embedded #< >
		 else
			return cellToString((cell_t *)_Car(candidate)); // just the symbol/atom
 }


 idStr /*c*/ CycList::bindingDefinitionKey(int answerset)
 {
	 cell_t *listp;

	 listp = cellN(answerset);
	 //printf("CycList::bindingDefinitionKey listp = [%s] \n",cellToString(listp));

	if (!nullp(listp) && !invalidp(listp) && !invalidp(_Car(listp)) )	 
		return cellToString((cell_t *)_Car(listp));
	else
		return cellToString(listp);
 }
 idStr /*c*/ CycList::bindingDefinitionValue(int answerset)
 {

	/* 
	Example of list returned for "population" ....

	( ("population" . #$numberOfInhabitants)
	("population" #$SubcollectionOfWithRelationFromTypeFn #$NonNegativeInteger #$numberOfInhabitants #$GeographicalThing) 
    ("population" . #$Population) )
    
	 */

	 cell_t *listp;
	 cell_t *candidate;
	 cell_t *candidate_head;
	 idStr /*c*/ x;

	 listp = cellN(answerset);
	 if (!nullp(listp))
	 {
	 candidate_head= (cell_t *)_Car(_Cdr(listp));
	 candidate = (cell_t *)_Cdr(_Cdr(listp));
	 }
	 else
	 {
		 candidate_head=NULL;
		 candidate=NULL;
	 }


	 //printf("CycList::bindingDefinitionValue listp = [%s] \n",cellToString(listp));
	 //printf("CycList::bindingDefinitionValue candidate_head = [%s] \n",cellToString(candidate_head));
	 //printf("CycList::bindingDefinitionValue candidate = [%s] \n",cellToString(candidate));

	 // does the head have anything in it, or is it empty headed ?
	 x=cellToString(candidate_head);
	 if (x.Length()>1)
	 {
		 return cellToString((cell_t *)_Cdr(listp));
	 }

	//return cellToString((cell_t *)candidate);

	 if (Link->s_length(candidate)>1)
			return cellToString((cell_t *)candidate); // its embedded #< >
		 else
			 if (!nullp(candidate))
				return cellToString((cell_t *)_Car(candidate)); // just the symbol/atom
			 else
				return cellToString(NIL); 
 }



 idStr /*c*/ CycList::bindingVariablesKey(int answerset,int pair)
 {
	 cell_t *listp;
	 cell_t *slistp;
	 slistp = cellN(answerset);
	 listp = cellN(pair,slistp);
	 return cellToString((cell_t *)_Car(listp));
 }

 idStr /*c*/ CycList::bindingVariablesValue(int answerset,int pair)
 {
	 cell_t *listp;
	 cell_t *candidate;
	 cell_t *slistp;
	 
	 slistp = cellN(answerset);
	 listp = cellN(pair,slistp);

	 candidate = (cell_t *)_Cdr(_Cdr(listp));
	 if (Link->s_length(candidate)>1)
			return cellToString((cell_t *)_Car(_Cdr(candidate))); // its embedded #< >
		 else
			return cellToString((cell_t *)_Car(candidate)); // just the symbol/atom
 }




 idStr /*c*/ CycList::cellToString(cell_t *cell)
 {
	 idStr /*c*/ reply;
	 idStr /*c*/ *reply_p;
	 reply="";
	 reply_p = &reply;
	 if ((Link!=NULL)&&(cell != NULL))
		 Link->str_print(reply_p,cell);
	 return reply;
 }

 idStr /*c*/ CycList::paraphrase()
 {
 	idStr /*c*/ retParaphrase;
	idStr /*c*/ assertion;
	int listSize;
	int i;
	
	retParaphrase="";
	listSize=size();
	for( i=1;i<listSize;i++)
	{
		assertion = cellToString(cellN(i));
//		retParaphrase=retParaphrase+" "+getParaphrase(assertion);
	}
    return retParaphrase;

 }

//--------------------------------------------------------------
//--------------------------------------------------------------
//--------------------------------------------------------------
//--------------------------------------------------------------
//--------------------------------------------------------------


 CycConnection::CycConnection()
{
	 CycSocket=0;
	 portnum=0;
	 persistent_connection=0;
	 c_stacked[0] = EOF;
	 c_stacked[1] = EOF;
	 setConnection(DEFAULT_CYC_HOSTNAME,DEFAULT_CYC_PORT);
	 communicationMode=CYC_ASCII_MODE;
}

 CycConnection::~CycConnection()
{
}

 
// Let user know there's a problem with socket comms.
void CycConnection::sockError(char *str)
{
	char strMsg[32];			// A lousy 32 char. buffer!
	sprintf(strMsg,"%s failed!", str);	// Complete the message.

	// Tell the user :-)
	MessageBox(NULL, strMsg, "SOCKET ERROR", MB_OK);

	WSACleanup();				// Wassup???

}

void CycConnection::setConnection(const char *hname,int pnum)
{
 strcpy(hostname,hname);
 portnum=pnum;

}

int CycConnection::OpenConnection()
{

		int rVal = 0;
		int pnum;
		WORD version = MAKEWORD(1,1);
		WSADATA wsaData;

		// We're compatible with version 1.1 of WS2_32.dll
		WSAStartup(version,(LPWSADATA)&wsaData);

		// Get hostEntry for server name (don't use IP numbers here!)
		LPHOSTENT hostEntry = gethostbyname(hostname);

		if (!hostEntry) {
			sockError("gethostbyname()");
			return FAILURE;
		}

		// Create socket for us to use
		CycSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

		if (CycSocket == SOCKET_ERROR) {
			sockError("socket()");
			return FAILURE;
		}

		// Fill in the sockaddr_in struct

		serverInfo.sin_family = PF_INET;
		serverInfo.sin_addr = *((LPIN_ADDR)*hostEntry->h_addr_list);

		// The port we use to communicate
		if (communicationMode != CYC_ASCII_MODE)
			pnum=portnum;
		else
			pnum = (short)((short)portnum + (short)CYC_ASCII_PORT_OFFSET);
		
		serverInfo.sin_port = htons(pnum);

		// Connect to the socket
		rVal = connect(CycSocket,(LPSOCKADDR)&serverInfo, sizeof(serverInfo));

		if (rVal == SOCKET_ERROR) {
			sockError("connect()");
			return FAILURE;
		}

	return SUCCESS;
}

int CycConnection::CloseConnection()
{
	int rVal=0;

	// Pull the plug
	if (CycSocket) {
		closesocket(CycSocket);
		if (rVal == SOCKET_ERROR) {
			sockError("recv()");
			return FAILURE;
		}
		CycSocket=0;
	}
	// Cleanup (something?)
	WSACleanup();
	return SUCCESS;
}

bool CycConnection::isValidBinaryConnection()
{
	return false; // Binary mode
}

idStr /*c*/ CycConnection::connectionInfo() {
//	idStr /*c*/ portS;
	idStr /*c*/ Info;
	char buffer[32000];
//	 portS.Format("%d",(portnum+CYC_ASCII_PORT_OFFSET));
	idStr::snPrintf(buffer,sizeof(buffer),"host %s, asciiPort %d" , hostname,(portnum+CYC_ASCII_PORT_OFFSET));
	Info.Append(buffer);
    return Info;
  }
int CycConnection::SendCommand()
{
	int rVal=0;
	// If we have something to send...
	gameLocal.Printf("SendCommand: %s\n",comline.c_str());
	if (*comline) {
		rVal = send(CycSocket, comline, strlen(comline), 0);


		// We must send this "\r" ???
		// (I have progs that don't respond without this!)
		send(CycSocket, "\r", 1, 0);
	}

	if (rVal == SOCKET_ERROR) {
		sockError("send()");
		return FAILURE;
	}

	return SUCCESS;

}

void CycConnection::GetReply()
{
	idStr /*c*/ rcodestr;
	int rVal=0;
	replyline="";
	bool started=false;
	// Get response from socket
	int howlong = 0;
	//while ((rVal = recv(CycSocket, buf, MAX_READ_LEN, MSG_PEEK)) = 0) {	}
	while (((rVal = recv(CycSocket, buf, 3, 0)) > 0) || !started) {
		if (rVal != SOCKET_ERROR) {
			started = true;
			howlong = howlong+rVal;
			replyline += buf;
			replyline = replyline.Left(howlong);
			if (replyline.Find('\n',0,howlong)>0 || replyline.Find('\r',0,howlong)>0) break;
		} else {
			gameLocal.Printf("Socket Error %d\n",rVal);
			replyline += "Socket Error ";
			replyline += (int)rVal;
			responseCode = 500;
			return;			
		}
		
		//if (rVal < MAX_READ_LEN) break;	// Need this ;-) //naughty
	}
//	gameLocal.Printf("% GetReply: %s\n",replyline.c_str());
	if (replyline.Length()<1) {
		responseCode = 500;
		replyline = "\"No respose from cyc server\"";
		return;
	}
	//cycsystem "(cyc-query '(#$isa ?X #$BPVItem) '#$DoomCurrentStateMt)"
	if (replyline.Length()>4) {
	// Get the response code while we are here
		rcodestr=replyline.Left(4);
		sscanf(rcodestr,"%d",&responseCode);
		replyline = replyline.Mid(4,replyline.Length()-4);
	}
}

idStr /*c*/ CycConnection::converseString(idStr /*c*/ message)
{
	// put the message in the buffer	
	comline = message;


	persistent_connection = 1;

	// Open the connection if required
	if (CycSocket==0) OpenConnection();

	// Send the message and get reply if available
	if (SendCommand()==SUCCESS)
		GetReply();

	// Close it if we don't want to recycle
	if (persistent_connection==0)
	{
	 CloseConnection();

	 }

	// return the response if any
	return replyline;
}

CycList *CycConnection::converseList(idStr /*c*/ message)
{
	CycList *replyList;
	replyList = new CycList;
	replyList->Link=this;

	// put the message in the buffer	
	comline = message;

	// Open the connection if required
	if (CycSocket==0) OpenConnection();

	// Send the message and get reply if available
	if (SendCommand()==SUCCESS)
		replyList->List=s_readResponse();

	// Close it if we don't want to recycle
	if (persistent_connection==0)
	{
	 CloseConnection();

	 }

	// return the response if any
	return replyList;
}

bool CycConnection::converseBoolean(idStr /*c*/ message)
{
	CycList *ReplyList;
	idStr /*c*/ ReplyText;
	bool retVal;
	ReplyList=converseList(message);
	ReplyText= ReplyList->toString();

	retVal=false;
	if (!idStr::Cmpn(ReplyText,"(NIL . NIL)",9)) retVal=false;
	if (!idStr::Cmpn(ReplyText,"(T . T )",9)) retVal=true;
	if (!idStr::Cmpn(ReplyText,"(200 T)",8)) retVal=true;
	if (!idStr::Cmpn(ReplyText,"(200 (NIL))",9)) retVal=true;
	if (!idStr::Cmpn(ReplyText,"((#$200 NIL)",7)) retVal=false;
	if (!idStr::Cmpn(ReplyText,"((#$200 NIL)",7)) retVal=false;
	
	delete ReplyList;
	
	return retVal;
}


idStr /*c*/ CycConnection::convertBoolean(idStr /*c*/ message)
{
	idStr /*c*/ retVal;
	retVal=message;
	if (!idStr::Cmpn(message,"(NIL . NIL)",9)) retVal="FALSE";
	if (!idStr::Cmpn(message,"(T . T )",9)) retVal="TRUE";
	if (!idStr::Cmpn(message,"(200 T)",8)) retVal="TRUE";
	if (!idStr::Cmpn(message,"(200 (NIL))",9)) retVal="TRUE";
	if (!idStr::Cmpn(message,"((#$200 NIL)",7)) retVal="FALSE";
	if (!idStr::Cmpn(message,"((#$200 NIL)",7)) retVal="FALSE";

	return retVal;
}
/**

    Browed from : eMail command line SMTP client.

    Copyright (C) 2001 - 2004 email by Dean Jones
    Software supplied and written by http://www.cleancode.org

    This file is part of eMail.

    eMail is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    eMail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with eMail; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

**/


/**
 * This function will be similar to fgetc except it will
 * use the SOCKET structure for buffering.    It will read
 * up to sizeof(sd->buf) bytes into the internal buffer
 * and then let sd->bufptr point to it.    If bufptr is 
 * not NULL, then it will return a byte each time it is
 * called and advance the pointer for the next call. If
 * bufptr is NULL, it will read another sizeof(sd->buf)
 * bytes and reset sd->bufptr.
**/

int CycConnection::sgetc()
{
    int retval;
	
    //assert(sd != NULL);
    if (c_stacked[c_stakdepth] != EOF) {
	if (c_stakdepth >0) 
		retval = c_stacked[c_stakdepth];
	else
		retval = EOF;
	c_stakdepth--;
	c_stacked[c_stakdepth] = EOF;
	return retval;
    }

    /* If there are some available bytes, send them */
    if (avail_bytes > 0)
    {
       avail_bytes--;
	   if (bufptr==0) 	MessageBox(NULL, "bufptr==0 (1)", "SGETC ERROR", MB_OK);
		retval =(int) *bufptr;
		bufptr++;
	   if (bufptr==0) 	MessageBox(NULL, "bufptr==0 (2)", "SGETC ERROR", MB_OK);
        return (retval);
    }

    //retval = read(CycSocket, buf, sizeof (buf));
    //retval = recv(CycSocket, buf, sizeof (buf)-1, MSG_PEEK);
    //retval = recv(CycSocket, buf, sizeof (buf)-1, 0);
    retval = recv(CycSocket, buf, 67, 0);
	pad[0]=EOF;
    if (retval == 0)
    {
        flags |= CYC_SOCKET_EOF;
        return (EOF);
    }
    else if (retval == -1)
    {
        flags |= CYC_SOCKET_ERROR;
        return (-1);
    }

    bufptr = buf;
    avail_bytes = retval - 1;       /* because we're about to send one byte */
	if (bufptr==0) 	MessageBox(NULL, "bufptr==0 (3)", "SGETC ERROR", MB_OK);

    return (*bufptr++);
}

int CycConnection::sungetc(int c)
{
		c_stakdepth++;
	    if (c_stakdepth<256) 
				c_stacked[c_stakdepth] = c;
		else
				c_stacked[256] = c;
				
	    return(c);
}


/**
 * This function will be similar to fputc except it will
 * use the SOCKET struture instead of the FILE structure
 * to place the file on the stream.
**/

int CycConnection::sputc (int ch)
{
    char buf[2] = { 0 };

//    assert(sd != NULL);

//    snprintf(buf, 2, "%c", ch);
    sprintf(buf,"%c", ch);
    //if (write(CycSocket, buf, 1) != 1)
    if (send(CycSocket, buf, 1,0) != 1)
    {
        flags |= CYC_SOCKET_ERROR;
        return (-1);
    }

    return (0);
}

/**
 * This function will be similar to fgets except it will
 * use the Sgetc to read one character at a time.    It 
 * will NOT return \n or \r like fgets does.
**/

char * CycConnection::sgets(char *buf, size_t size)
{
    u_int i;
    int ch;

    /* start i at 1 to read size - 1 */
    for (i = 1; i < size; i++)
    {
        ch = sgetc();
        if (ch == -1)
            return (NULL);
        else if (ch == EOF)
            break;
        else if (ch == '\r')
            continue;
        else if (ch == '\n')
            break;

        *buf++ = ch;
    }

    *buf = '\0';
    return (buf);
}

/**
 * This function will take a SOCKET and perform
 * functionality similar to fputs().    If you want to 
 * know more, please read man fputs.
**/

int CycConnection::sputs(const char *buf)
{
    int bytes = 0;
    u_int current_size = 0;
    u_int size = strlen(buf);
    u_int size_left = size;

    while (current_size < size)
    {
        //bytes = write(CycSocket, buf + current_size, size_left);
        bytes = send(CycSocket, buf + current_size, size_left,0);
        if (bytes == -1)
        {
            flags |= CYC_SOCKET_ERROR;
            return (-1);
        }

        current_size += bytes;
        size_left -= bytes;
    }

    return (0);
}

/**
 * Will test the flags for a SOCKET_ERROR 
**/

int CycConnection::serror()
{
    return (flags & CYC_SOCKET_ERROR);
}

/**
 * Will test the flags for a SOCKET_EOF
**/

int CycConnection::seof()
{
    return (flags & CYC_SOCKET_EOF);
}

/*
 * From : lisp.c - utility functions like LISP
 *
 * Copyright (c) 1996,1997 Nara Institute of Science and Technology
 *
 * Author: 1990/11/12/Mon Yutaka MYOKI(Nagao Lab., KUEE)
 *         1990/12/16/Mon Last Modified
 *         special thanks to Itsuki NODA
 *         A.Kitauchi <akira-k@is.aist-nara.ac.jp>, Apr. 1997
 */



static int (*cha_getc)(), (*cha_ungetc)();

static int is_bol = 1;
//static int c_stacked = EOF;

int CycConnection::cha_getc()
{
    int c;

//    if (c_stacked != EOF) {
//	c_stakdepth--;
//	c = c_stacked;
//	c_stacked = EOF;
//    } else
      c = sgetc();

	if (c=='\r') c='\n'; // khc

    /* skip '\r' */
    if (c == '\r')
      c = sgetc();

    if (c == '.' && is_bol) {
	/* skip '\r' */
	if ((c = sgetc()) == '\r')
	  c = sgetc();
	//below khc
	if (c == '\n')
	  c = EOF;
   }

    is_bol = c == '\n' ? 1 : 0;

 	if (c <0)
	  c = EOF;

    return c;
}

int CycConnection::cha_ungetc(int c)
{
//	c_stakdepth++;

//    c_stacked = c;
//	return c;
	return sungetc(c);
}

void CycConnection::set_cha_getc_alone()
{
//    int fgetc(), ungetc();

//    cha_getc = fgetc;
//    cha_ungetc = ungetc;
}

void CycConnection::set_cha_getc_server()
{
//    cha_getc = cha_getc_server;
//    cha_ungetc = cha_ungetc_server;
}
/* dmiles commented due to infinate loop
int CycConnection::cha_getc_server()
{
	return cha_getc_server();
}

int CycConnection::cha_ungetc_server(int c)
{
    return cha_ungetc_server(c);
}
*/
/*
------------------------------------------------------------------------------
	local error processing
------------------------------------------------------------------------------
*/
cell_t *CycConnection::error_in_lisp()
{
//    if (Cha_server_mode < 2) {
//	cha_exit_file(-1, "premature end of file or string\n");
//	exit(1);
//    }
//    Cha_errno = 1;
    return NIL;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<ifnextchar>: if next char is <c> return 1, otherwise return 0
------------------------------------------------------------------------------
*/

 int CycConnection::ifnextchar(int i)
{
    int c;

    do {
	c = cha_getc();
	if (c == '\n') LineNo++;
    } while (c == ' ' || c == '\t' || c == '\n');

	if (c<0) return EOF; //khc

    if (c == EOF)
      return EOF;

    if (i == c) 
      return TRUE;
    else {
		// khc
		if ((i==EPARENTHESIS) && ( c<0||c == ' ' || c == '\t' || c == '\n'))
			return TRUE;
	cha_ungetc(c);
	return FALSE;
    }
}

/*
 * skip comment lines
 */
 int CycConnection::skip_comment()
{
    int n, c;

    while ((n = ifnextchar( (int)COMMENTCHAR)) == TRUE) {
	while ((c = cha_getc()) != '\n')
	  if (c == EOF)
	    return c;
	LineNo++;
    }

    return n;
}

int CycConnection::s_feof()
{
    int c;

    /* init the pointer to output functions */
    //if (&cha_getc == NULL)
    //  set_cha_getc_alone();

    for (;;) {
	if (skip_comment() == EOF)
	  return TRUE;
	if ((c = cha_getc()) == '\n')
	    LineNo++;
	else if (c == ' ' || c == '\t')
	  ;
	else {
	    cha_ungetc(c);
	    return FALSE;
	}	
    }
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<make_cell>: make a new cell
------------------------------------------------------------------------------
*/

 cell_t *CycConnection::cell_malloc_free(int isfree)
{
/*    static cell_t *ptr[1024*16];
    static int ptr_num = 0;
    static int idx = CELLALLOCSTEP;

    if (isfree) {
	// free 
	if (ptr_num > 0) {
	    while (ptr_num > 1)
	      free(ptr[--ptr_num]);
	    idx = 0;
	}
	return NULL;
    } else {
	if (idx == CELLALLOCSTEP) {
	    if (ptr_num == 1024*16)
	    //  cha_exit(1, "Can't allocate memory");
		return NULL;

	    ptr[ptr_num++] = (cell_t *)malloc(sizeof(cell_t) * idx);
	    idx = 0;
	}
	return ptr[ptr_num - 1] + idx++;
    }
*/
#if 1
    return (cell_t *)malloc(sizeof(cell_t));
#endif
}

#define CHA_MALLOC_SIZE (1024 * 64)
 void *CycConnection::malloc_char(int size)
{
/*    static char *ptr[128];
    static int ptr_num = 0;
    static int idx = CHA_MALLOC_SIZE;

    if (size < 0) {
	// free 
	if (ptr_num > 0) {
	    while (ptr_num > 1)
	      free(ptr[--ptr_num]);
	    idx = 0;
	}
	return NULL;
    } else {
	if (idx + size >= CHA_MALLOC_SIZE) {
	    if (ptr_num == 128)
	    //  cha_exit(1, "Can't allocate memory");
		return NULL;

	    ptr[ptr_num++] =(char *) malloc(CHA_MALLOC_SIZE);
	    idx = 0;
	}
	idx += size;
	return ptr[ptr_num - 1] + idx - size;
    }
*/
	return malloc((size_t)size);	
}

 char *CycConnection::lisp_strdup(char *str)
{
    char *newstr;

    newstr = (char *) malloc_char(strlen(str) + 1);
    strcpy(newstr, str);

    return newstr;
}

void CycConnection::s_free(cell_t *cell)

{
#if 0
    cell_malloc_free(1);
    malloc_char(-1);
#else
    if (atomp(cell)) {
	free(cell->value.atom);
    } else if (consp(cell)) {
	s_free(car(cell));
	s_free(cdr(cell));
    } else {
	return;
    }

    free(cell);
#endif
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<tmp_atom>: use <TmpCell>
------------------------------------------------------------------------------
*/

cell_t *CycConnection::tmp_atom(char *atom)
{
    static cell_t _TmpCell;
    static cell_t *TmpCell = &_TmpCell;

    s_tag(TmpCell) = ATOM;
    s_atom_val(TmpCell) = atom;

    return TmpCell;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<cons>: make <cons> from <car> & <cdr>
------------------------------------------------------------------------------
*/

cell_t *CycConnection::cons(void *car,void *cdr)
{
    cell_t *cell;

    cell = cell_malloc_free(0);
    s_tag(cell) = CONS;
	_Car(cell) = (cell_t *)NULL;
    _Cdr(cell) = (cell_t *)NULL;

    _Car(cell) = (cell_t *)car;
    _Cdr(cell) = (cell_t *)cdr;

    return cell;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<car>: take <car> from <cons>
------------------------------------------------------------------------------
*/

cell_t *CycConnection::car(cell_t *cell)
{
    if (consp(cell))
      return (cell_t *)_Car(cell);

    if (nullp(cell))
      return NIL;

    /* error */
//    if (Cha_server_mode < 2) {
//	cha_exit_file(-1, "");
//	s_print(stderr, cell);
//	fprintf(stderr, " is not list\n");
//	exit(1);
//    }
    Cha_errno = 1;
    return NIL;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<cdr>: take <cdr> from <cons>
------------------------------------------------------------------------------
*/

cell_t *CycConnection::cdr(cell_t *cell)
{
    if (consp(cell))
      return (cell_t *)_Cdr(cell);

    if (nullp(cell))
      return NIL;

    /* error */
//    if (Cha_server_mode < 2) {
//	cha_exit_file(-1, "");
//	s_print( cell);
//	fprintf(stderr, " is not list.\n");
//	exit(1);
//    }
    Cha_errno = 1;
    return NIL;
}

char *CycConnection::s_atom(cell_t *cell)
{
    if (atomp(cell))
      return s_atom_val(cell);

    /* error */
//    if (Cha_server_mode < 2) {
//	cha_exit_file(-1, "`");
//	s_print(stderr, cell);
//	fprintf(stderr, "' is not atom\n");
//	exit(1);
//    }
    Cha_errno = 1;
    return NILSYMBOL;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<equal>:
------------------------------------------------------------------------------
*/

int CycConnection::equal(void *x, void *y)
{
    if (eq(x, y)) return TRUE;
    if (nullp(x) || nullp(y)) return FALSE;
    if (s_tag(x) != s_tag(y)) return FALSE;
    if (s_tag(x) == ATOM) return !strcmp(s_atom_val(x), s_atom_val(y));
    if (s_tag(x) == CONS)
      return (equal(_Car(x), _Car(y)) && equal(_Cdr(x), _Cdr(y)));
    return FALSE;
}

int CycConnection::s_length(cell_t *list)
{
    int i;

    for (i = 0; consp(list); i++)
      list =(cell_t *) _Cdr(list);

    return i;
}

 int CycConnection::dividing_code_p(int code)
{
	if (code<1) return 1;
    switch (code) {
      case '\n': case '\t': case ';': 
      case '(': case ')': case ' ':  
	  //case '.':
	return 1;
      default:
	return 0;
    }
}

 int CycConnection::myscanf( char *cp)
{
    int code;

    code = cha_getc();
    if (dividing_code_p(code) || code == EOF)
      return 0;

    if (code == '"') {
		//*cp++=code; // we want string in quotes to be strings in quotes
	while (1) {
	    if ((code = cha_getc()) == EOF)
	      return 0;
	    else if (code == '"')
		{
			//*cp++=code;// we want string in quotes to be strings in quotes
			break;
		}
	    else if (code == '\\') {
		*cp++ = code;
		if ((code = cha_getc()) == EOF) 
		  return 0;
		*cp++ = code;
	    }	       
	    else
	      *cp++ = code;
	}
    }
    else {
	while (1) {
	    *cp++ = code;
	    code = cha_getc();
	    if (dividing_code_p(code) || code == EOF) {
		cha_ungetc(code);
		break;
	    }
	}
    }

    *cp = '\0';
    return 1;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<s_read>: read S-expression
------------------------------------------------------------------------------
*/

 cell_t *CycConnection::s_read_atom()
{
    cell_t *cell;
    char buffer[BUFSIZ];

    skip_comment();

    /* changed by kurohashi. */
    if (myscanf( buffer) == 0)
	
      return error_in_lisp();
	//printf("buffer[%s]\n",buffer);
    if (!strcmp(buffer, NILSYMBOL))
      return NIL;

    cell = new_cell();
    s_tag(cell) = ATOM;
    s_atom_val(cell) = lisp_strdup(buffer);

    return cell;
}

//static cell_t *CycConnection::s_read_cdr();
//static cell_t *CycConnection::s_read_main();

 cell_t *CycConnection::s_read_car()
{
    cell_t *cell;
	char *bufp1;
	char *bufp2;
	char *bufp3;

 	bufp1=bufptr;
   skip_comment();
 	bufp1=bufptr;
    switch (ifnextchar((int)EPARENTHESIS)) {
      case TRUE:
	return NIL;
      case FALSE:
	cell = new_cell();
		bufp1=bufptr;

	_Car(cell) = s_read_main();
	bufp2=bufptr;
	_Cdr(cell) = s_read_cdr();
	bufp3=bufptr;
	return cell;
      default: /* EOF */
	return error_in_lisp();
    }
}

 cell_t *CycConnection::s_read_cdr()
{
	char *bufp;
	 cell_t *retVal;

    skip_comment();
	
	depth++;
	if (depth>9)
	{
//		return NIL;
	}
	bufp=bufptr;
    switch (ifnextchar((int)EPARENTHESIS)) {
    
	case TRUE:
		{ 
			depth--; 
			return NIL;
		}
    
	case FALSE:
		{
				bufp=bufptr;
			retVal=s_read_car();
			depth--;
				bufp=bufptr;

			return retVal;
	}
	default: /* EOF */
	return error_in_lisp();
    }
}

 cell_t *CycConnection::s_read_main()
{
	char *bufp;
	char *bufp2;

	cell_t *retVal;
	bufp=bufptr;

    switch (ifnextchar( (int)BPARENTHESIS)) {
      case TRUE:
		  {
			bufp=bufptr;
			retVal= s_read_car();
			bufp2=bufptr;
			if (bufp2<bufp)
			{
//				printf("ALERT1!!!\n");
			}

			return retVal;
		  }
      case FALSE:
		  {
			bufp=bufptr;
			retVal= s_read_atom();
			bufp2=bufptr;
			if (bufp2<bufp)
			{
//				printf("ALERT2!!!\n");
			}
			return retVal;
		  }
      default: /* EOF */
	return error_in_lisp();
    }
}

cell_t *CycConnection::s_read()

{
    /* init the pointer to output functions */
//    if ((&cha_getc) == NULL)
 //     set_cha_getc_alone();

    if (LineNo == 0)
      LineNo = 1;
    LineNoForError = LineNo;

    return s_read_main();
}




cell_t *CycConnection::s_readResponse()

{
	char *bufp;
    cell_t *cell;
	depth=0;
	c_stakdepth=0;
	cell = new_cell();
	bufp=bufptr;
	_Car(cell) = s_read_main(); // get the response code
	bufp=bufptr;
	_Cdr(cell) = s_read_main();  // get the list/atom/thingy
	return cell;
}

/*
------------------------------------------------------------------------------
	FUNCTION
	<assoc>:
------------------------------------------------------------------------------
*/

cell_t *CycConnection::assoc(cell_t *item,cell_t *alist)
{
    while (!nullp(alist) && !equal(item, (car(car(alist)))))
      alist = cdr(alist);
    return car(alist);
}

/*
------------------------------------------------------------------------------
	PROCEDURE
	<s_print>: pretty print S-expression
------------------------------------------------------------------------------
*/

 cell_t *CycConnection::s_print_cdr( cell_t *cell)
{
    if (!nullp(cell)) {
	if (consp(cell)) {
	    sputc(' ');
	    s_print((cell_t *) _Car(cell));
	    s_print_cdr((cell_t *) _Cdr(cell));
	} else {
	    sputc(' ');
	    s_print( cell);
	}
    }

    return cell;
}

cell_t *CycConnection::s_print( cell_t *cell)
{
    if (nullp(cell))
      sputs(NILSYMBOL);
    else {
	switch (s_tag(cell)) {
	  case CONS:
	    sputc(BPARENTHESIS);
	    s_print((cell_t *) _Car(cell));
	    s_print_cdr((cell_t *) _Cdr(cell));
	    sputc(EPARENTHESIS);
	    break;
	  case ATOM:
	    sputs(s_atom_val(cell));
	    break;
	  default:
	    sputs("INVALID_CELL");
	}
    }

    return cell;
}


 cell_t *CycConnection::str_print_cdr(idStr /*c*/ *str, cell_t *cell)
{
    if (!nullp(cell)) {
	if (consp(cell)) {
	    *str=*str+" ";
	    str_print(str,(cell_t *) _Car(cell));
	    str_print_cdr(str,(cell_t *) _Cdr(cell));
	} else {
	    *str=*str+" ";
	    str_print(str, cell);
	}
    }

    return cell;
}

cell_t *CycConnection::str_print(idStr /*c*/ *str, cell_t *cell)
{
    if (nullp(cell))
      *str=*str+NILSYMBOL;
    else {
	switch (s_tag(cell)) {
	  case CONS:
	    *str=*str+BPARENTHESIS;
	    str_print(str,(cell_t *) _Car(cell));
	    str_print_cdr(str,(cell_t *) _Cdr(cell));
	    *str=*str+EPARENTHESIS;
	    break;
	  case ATOM:
	    *str=*str+s_atom_val(cell);
	    break;
	  default:
	    *str=*str+"INVALID_CELL";
	}
    }

    return cell;
}
