// Copyright (C) 2005 Daxtron/Logicmoo 

#ifndef _DAXWEBSERVER_H_
#define	_DAXWEBSERVER_H_


#include <windows.h>
#include <process.h>
#include <iostream.h>
#include <fstream.h>
#include <stdio.h>
#include <winsock.h>

//--------------------------------------------------------------
// manifest constants
//--------------------------------------------------------------
#define COMM_BUFFER_SIZE 1024
#define SMALL_BUFFER_SIZE 10

//--------------------------------------------------------------
// global vars
//--------------------------------------------------------------
#define MAX_REPLY_BUF 65536
#define MAX_BUF_LEN 512
#define MAX_REQSTRING 1024
#define MAX_PATH_LEN 256

//--------------------------------------------------------------
// structures
//--------------------------------------------------------------
struct HTTPRequestHeader {
    char method[SMALL_BUFFER_SIZE];
    char url[MAX_PATH];
    char filepathname[MAX_PATH];
    char httpversion[SMALL_BUFFER_SIZE];
    IN_ADDR client_ip;
    int mime_index;
};

struct ClientInfo {
    SOCKET client_socket;
    IN_ADDR client_ip;
};

struct MimeAssociation {
    char *file_ext;
    char *mime;
};


extern void gameLocalPrintf( const char *fmt, ... );
//--------------------------------------------------------------
// prototypes
//--------------------------------------------------------------
SOCKET StartWebServer();
//int WaitForClientConnections(SOCKET server_socket);
void HandleHTTPRequest(char *inbuf, HTTPRequestHeader *requestheaderP, void *data );
int findMimeType(const char *extension);
BOOL ParseHTTPHeader(char *receivebuffer, HTTPRequestHeader *requestheaderP);
void OutputHTTPError(SOCKET client_socket, int statuscode );
void OutputHTTPRedirect(SOCKET client_socket, const char *defaulturl);
void OutputScreenError(const char *errmsg);
int SocketRead(SOCKET client_socket, char *receivebuffer, int buffersize);
void DetermineHost( char *hostname );
void CheckForClientConnections();


#endif	/* !_DAXWEBSERVER_H_ */
