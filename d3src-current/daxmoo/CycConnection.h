#ifndef __INC_CYCCONNECTION_H__
#define __INC_CYCCONNECTION_H__

//#include <afx.h>
//#include <afxwin.h>
//#include <afxinet.h>
#include <windows.h>
#include <winsock.h>

#if _MSC_VER > 1000
#include "../../idlib/precompiled.h" 
#pragma once
#endif // _MSC_VER > 1000

//#include "stdlib.h"
//#include "stdio.h"
//#include "string.h"
//#include "math.h"

#ifndef SUCCESS
#define SUCCESS  0
#define FAILURE -1
#endif

//#include "support.h"
#include <ctype.h>
#include <string.h>
#include <limits.h>


#define CYC_ASCII_MODE  1
#define CYC_BINARY_MODE  2
#define DEFAULT_CYC_HOSTNAME "cycserver"
#define DEFAULT_CYC_PORT 3600
#define CYC_HTTP_PORT_OFFSET  0
#define CYC_ASCII_PORT_OFFSET  1
#define CYC_CFASL_PORT_OFFSET  14

/* Error flags */
#define CYC_SOCKET_ERROR 0x01
#define CYC_SOCKET_EOF   0x02

/*
 * From chadic.h
 *     1990/12/06/Thu  Yutaka MYOKI(Nagao Lab., KUEE)
*/
#define CONS		0
#define ATOM		1
#define NIL		((cell_t *)(NULL))

#define s_tag(cell)	(((cell_t *)(cell))->tag)
#define consp(x)	(!nullp(x) && (s_tag(x) == CONS))
#define atomp(x)	(!nullp(x) && (s_tag(x) == ATOM))
#define invalidp(x)	(!nullp(x) && ((s_tag(x) != ATOM)&&(s_tag(x)!=CONS)) )
#define nullp(cell)	((cell) == NIL)
#define car_val(cell)	(((cell_t *)(cell))->value.cha_cons.cha_car)
#define cdr_val(cell)	(((cell_t *)(cell))->value.cha_cons.cha_cdr)
#define s_atom_val(cell) (((cell_t *)(cell))->value.atom)


#define COMMENTCHAR	';'
#define BPARENTHESIS	'('
#define BPARENTHESIS2	'<'
#define BPARENTHESIS3	'['
#define EPARENTHESIS	')'
#define EPARENTHESIS2	'>'
#define EPARENTHESIS3	']'
#define SCANATOM	"%[^(;) \n\t]"
#define NILSYMBOL	"NIL"
#define CELLALLOCSTEP	1024

#define _Car(cell)	(((cell_t *)(cell))->value.cons.car)
#define _Cdr(cell)	(((cell_t *)(cell))->value.cons.cdr)
#define new_cell()	(cons(NIL, NIL))
#define eq(x, y)	(x == y)


/* <cha_car> 部と <cha_cdr> 部へのポインタで表現されたセル */
typedef struct _bin_t {
    void *car;			/* address of <cha_car> */
    void *cdr;			/* address of <cha_cdr> */
} bin_t;

/* <BIN> または 文字列 を表現する完全な構造 */
typedef struct _cell {
    int tag;			/* tag of <cell> 0:cha_cons 1:atom */
    union {
	bin_t	cons;
	char	*atom;
    } value;
} cell_t;


class CycList;

#define CycConnectionBufferSize 32784

class CycConnection
{
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

public:
	CycConnection();
  ~CycConnection();
// Operations
public:
//	int COMMUNICATION_MODE = ASCII_MODE;
  /** Default host name for the OpenCyc server. */
//	char DEFAULT_HOSTNAME[32] = "localhost";
//	int DEFAULT_BASE_PORT = 3600;
//	int PERSISTENT_CONNECTION = 0;
//	int MESSAGING_MODE = 0;


  /** Default base tcp port for the OpenCyc server. */
//    int DEFAULT_BASE_PORT = 3600;

	int portnum;
	char hostname[128];
	int persistent_connection;
	int communicationMode;
	SOCKET CycSocket;
	SOCKADDR_IN serverInfo;
	
	int responseCode;
	idStr /*c*/ prm;			// A parameter to send to a socket.
	idStr /*c*/ comline;		// A line of commands we send to a socket.
	idStr /*c*/ replyline;
	//char buf[MAX_READ_LEN];
    int flags;        /* Error flags */
    int errnum;       /* Essentially, errno */
    int avail_bytes;  /* Available bytes in buffer */
	int depth;
	int c_stakdepth;
    char *bufptr;     /* Pointer to Internal buffer */
	int c_stacked[CycConnectionBufferSize]; /* ungetc char stack */
    char buf[CycConnectionBufferSize];      /* Internal buffer */
	char pad[CycConnectionBufferSize];         /* overflow zone */
// Methods

	void setConnection(const char *hname,int pnum);
	void sockError(char *str);
	int OpenConnection();
	int CloseConnection();
	int SendCommand();
	void GetReply();
	idStr /*c*/ CycConnection::connectionInfo();

	idStr /*c*/ converseString(idStr /*c*/ message);
	CycList *converseList(idStr /*c*/ message);
	bool converseBoolean(idStr /*c*/ message);
	bool isValidBinaryConnection();

	idStr /*c*/ CycConnection::convertBoolean(idStr /*c*/ message);

// emulation of socket/File stream functions
	int sgetc ();
	int sungetc (int c);
	int sputc ( int);
	char *sgets ( char *, size_t);
	int sputs ( const char * );
	int ssend ( const char *, size_t);
	int serror ();
	int seof ();
	char *socket_get_error ();
// You may be able to reuse this with a string
	// by putting a string into the buffer

	/* lisp support */
	long LineNo;
	long LineNoForError;
	int Cha_errno;
	long Cha_lineno_error;

	int CycConnection::cha_getc_server();
	int CycConnection::cha_ungetc_server(int c);
	void CycConnection::set_cha_getc_alone();
	void CycConnection::set_cha_getc_server();
	int CycConnection::ifnextchar(int i);
	int CycConnection::skip_comment();
	cell_t *CycConnection::error_in_lisp();
	int CycConnection::cha_getc();
	int CycConnection::cha_ungetc(int c);

	cell_t *CycConnection::cell_malloc_free(int isfree);
	void *CycConnection::malloc_char(int size);
	char *CycConnection::lisp_strdup(char *str);
	int CycConnection::dividing_code_p(int code);
	int CycConnection::myscanf( char *cp);
	cell_t *CycConnection::s_read_atom();
	cell_t *CycConnection::s_read_cdr();
	cell_t *CycConnection::s_read_car();
	cell_t *CycConnection::s_read_main();
	cell_t *CycConnection::s_print_cdr( cell_t *cell);
	cell_t *CycConnection::str_print_cdr(idStr /*c*/ *str, cell_t *cell);

/* lisp.c */
	void set_skip_char(int);
	int s_feof();
	void s_free(cell_t*);
	cell_t *tmp_atom(char*);
	cell_t *cons(void*, void*);
	cell_t *car(cell_t*);
	cell_t *cdr(cell_t*);
	char *s_atom(cell_t*);
	int equal(void*, void*);
	int s_length(cell_t*);
	cell_t *s_read();
	cell_t *s_readResponse(); //  (responseCode . ResponseData)
	cell_t *assoc(cell_t*, cell_t*);
	char *s_tostr(cell_t*);
	cell_t *s_print( cell_t*);
	cell_t *str_print(idStr /*c*/ *str, cell_t*);



};

class CycList
{
public:	
	CycList();
	~CycList();
public:
	cell_t *List;
	CycConnection *Link;

    idStr /*c*/ CycList::toString();
	idStr /*c*/ CycList::cellToString(cell_t *cell);
	 int    CycList::size();
	cell_t *CycList::cellN(int n);
    cell_t *CycList::cellN(int n,cell_t *slist);

  CycList *CycList::get(int n);

 idStr /*c*/ CycList::bindingLookupKey(int answerset);
 idStr /*c*/ CycList::bindingLookupValue(int answerset);
 idStr /*c*/ CycList::bindingVariablesKey(int answerset,int pair);
 idStr /*c*/ CycList::bindingVariablesValue(int answerset,int pair);
 idStr /*c*/ CycList::bindingDefinitionKey(int answerset);
 idStr /*c*/ CycList::bindingDefinitionValue(int answerset);
 idStr /*c*/ CycList::paraphrase();

};

#endif // __INC_CYCCONNECTION_H__
