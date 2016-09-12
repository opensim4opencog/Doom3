// Copyright (C) 2005 Daxtron/Logicmoo 
//
#include "../idlib/precompiled.h"
#pragma hdrstop

#ifdef DAXMOO_WWW

extern void udpprint(const char *msg);
#include "../game/Game_local.h"
#include "DaxWebServer.h"

//--------------------------------------------------------------
// global vars
//--------------------------------------------------------------

int wwwport = 81;
int wwwactive=0;
char wwwroot[MAX_PATH];
char wwwpage[MAX_PATH];
char hostname[MAX_PATH];
char weblogdir[MAX_PATH];
char irclogdir[MAX_PATH];
idStr strWebProc;

SOCKET www_server_socket;

MimeAssociation mimetypes[] = { 
    { ".yz9", "text/plain"},
    { ".txt", "text/plain"},
    { ".html","text/html"},
    { ".htm", "text/html"},
    { ".gif", "image/gif"},
    { ".jpg", "image/jpeg"}
};
#define NUM_MIME_TYPES 6

WSADATA wsaData;
int winsock_active =0;

//------------------------------------------
//
//------------------------------------------
void getAnswer( char *inputStr,  char *outputStr) {

    char linebuffer[COMM_BUFFER_SIZE];
	
    int i;  
	int lp;
	int e;
	idEntity *check;

    idEntity *ent;  
    idStr match;  
    idStr varid;  
	idStr strReply;
	idStr domain;
    int ecount=0;  
	idActor *actor;
    idMat3      axis;  
    idVec3      ent_origin;  
    idVec3      diff;  
//    int         count;  
//    size_t      size;  

    //int sessid;  
    //sessid=gen_sessid();  

    //ent = gameLocal.FindEntity( args.Argv( 1 ) );  
	udpprint("BeginGetAnswer");
 	//udpprint("FinishGetAnswer");
	//return;


	match.Empty();
	match.Append(inputStr);
	lp=match.FindText(inputStr,"/");
	domain.Empty();
	if(lp>1)
	{
		domain.Append(match.Left(lp).c_str());
		match=match.Mid(lp+1,match.Length()-lp);
	}
	else
	{
		domain.Append("entity");
	}
	sprintf(linebuffer, "domain:[%s]",domain.c_str()); udpprint(linebuffer);
	sprintf(linebuffer, "match:[%s]",match.c_str()); udpprint(linebuffer);

    match.Replace("#$","");  
    match.Replace("/","");  

	strReply.Empty();
	strReply.Append("<DOOM-ML>\n");

	//--------------------------------
	// process /command/<console_command>
	//--------------------------------
	if (domain.Icmp("command")==0)
	{
	//    cmdSystem->BufferCommandText( CMD_EXEC_NOW, va( "kick %d\n", player->entityNumber ) );
	 cmdSystem->BufferCommandText( CMD_EXEC_NOW, va( match.c_str()) );
	sprintf(linebuffer, "<execute_command> %s</execute_command>",match.c_str() );  
	udpprint(linebuffer);
	strReply.Append(linebuffer);
	
	}


	//--------------------------------
	// process /cansee/<actor>
	//--------------------------------
	if (domain.Icmp("cansee")==0)
	{
		actor=static_cast<idPlayer *>( gameLocal.FindEntity( match.c_str() ) );
		if ( actor )
		{

		for( e = 0; e < MAX_GENTITIES; e++ ) {
			check = gameLocal.entities[ e ];  

			if( !check ) {
				continue;  
			}
			if( !actor->CanSee( check, true ) ) {
				continue;  
			}

			if( !actor->name.Filter( match,false ) ) {
				continue;  
			}

			ent_origin=check->GetPhysics()->GetOrigin(); //GetWorldCoordinates( ent_origin);  

			sprintf(linebuffer,"<cansee> <actor>%s</actor> <object>%s</object> </cansee>\n", actor->name.c_str(),check->name.c_str());  
			udpprint(linebuffer);
			strReply.Append(linebuffer);
			//gameLocalPrintf( "(isa %s %s)\n", check->name.c_str(),check->GetClassname());  
			//gameLocalPrintf( "(defname %s %s)\n", check->name.c_str(),check->GetEntityDefName());  
			//gameLocalPrintf( "(locatedAtPoint-Spatial %s (Point3Fn %s))\n", check->name.c_str(),ent_origin.ToString());  
			sprintf(linebuffer,"<locatedAtPoint-Spatial> <object>%s</object> <Point3Fn>%s</Point3Fn> </locatedAtPoint-Spatial>\n",check->name.c_str(),ent_origin.ToString());  
			udpprint(linebuffer);
			strReply.Append(linebuffer);

			diff = ent_origin - actor->GetPhysics()->GetOrigin();  
			//gameLocalPrintf( "(selfRelative %s (Point3Fn %s))\n", check->name.c_str(),diff.ToString());  
			sprintf(linebuffer,"<actorRelative> <actor>%s</actor> <object>%s</object> <Point3Fn>%s</Point3Fn> </actorRelative>\n",actor->name.c_str(),check->name.c_str(),diff.ToString());  
			udpprint(linebuffer);
			strReply.Append(linebuffer);

//			count++;  
//			size += check->spawnArgs.Allocated();  
		 } 
		}
		else

		{
			sprintf(linebuffer,"<unkownActor>%s</unknowActor>\n",match.c_str());  
			udpprint(linebuffer);
			strReply.Append(linebuffer);

		}

	}
	//--------------------------------
	// default is /entity/<objectname>
	//--------------------------------

	if(domain.Icmp("entity")==0)
	{
	for(int e = 0; e < MAX_GENTITIES; e++ ) {
			ent = gameLocal.entities[ e ];  

			if( !ent ) {
				continue;  
			}
			if( !ent->name.Filter( match,false ) ) {
				continue;  
			}

			if( ent->spawnArgs.GetNumKeyVals() <=0 ) {
				continue;  
			}

			//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
			for( i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++ ) {
				const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
				//sprintf(linebuffer, "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
				sprintf(linebuffer, "<spawnargs> <ent>%s</ent> <arg>%s</arg> <value>%s</value> </spawnargs>\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
				udpprint(linebuffer);
				strReply.Append(linebuffer);
			}  
		}

	}

	//--------------------------------
	// default is /set/<objectname>:<arg>=<value>
	//--------------------------------

	if(domain.Icmp("setarg")==0)
	{
		idStr vararg;
		idStr varval;
		int chrptr;
		chrptr=match.FindChar(match.c_str(),'=');
		varval=match.Mid(chrptr+1,match.Length()-chrptr);
		match=match.Left(chrptr);
		chrptr=match.FindChar(match.c_str(),':');
		vararg=match.Mid(chrptr+1,match.Length()-chrptr);
		match=match.Left(chrptr);

		sprintf(linebuffer, "<fsetargs> <ent>%s</ent> <arg>%s</arg> <value>%s</value> </fsetargs>\n",match.c_str(), vararg.c_str(), varval.c_str() );  
		udpprint(linebuffer);
		strReply.Append(linebuffer);


	for(int e = 0; e < MAX_GENTITIES; e++ ) {
			ent = gameLocal.entities[ e ];  

			if( !ent ) {
				continue;  
			}
			if( !ent->name.Filter( match ,false) ) {
				continue;  
			}

			if( ent->spawnArgs.GetNumKeyVals() <=0 ) {
				continue;  
			}

			//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
			// report the original value
			for( i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++ ) {
				const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
				if(kv->GetKey().Filter(vararg,false)) {
				sprintf(linebuffer, "<ospawnargs> <ent>%s</ent> <arg>%s</arg> <value>%s</value> </ospawnargs>\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
				udpprint(linebuffer);
				strReply.Append(linebuffer);
				}
			}  
			// set the new one
			ent->spawnArgs.Set(vararg.c_str(),varval.c_str());
			// return the new one
			for( i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++ ) {
				const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
				if(kv->GetKey().Filter(vararg,false)) {
				sprintf(linebuffer, "<spawnargs> <ent>%s</ent> <arg>%s</arg> <value>%s</value> </spawnargs>\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
				udpprint(linebuffer);
				strReply.Append(linebuffer);
				}
			}  
		}
	}
	strReply.Append("</DOOM-ML>\n");
	strcpy(outputStr,strReply.c_str());
	udpprint("FinishGetAnswer");


}

//////////////////////////////////////////////////////////////
// INITIAL IRC CLIENT ATTEMPT
// Bolted in from Aine bot project \source\extras_old\irc_old
//
///////////////////////////////////////// ///////////////////

void decodeURLEncode(char *s) {
    int count = 0;
    int len = strlen(s);
    int i;

    for(i = 0; i < len; i++) {
        if((s[i] == '%') && (s[i + 1] == '2') && (s[i + 2] >= '0')) {
            s[count++] = 0x20 + (s[i + 2] - '0');
            i = i + 2;
        } else s[count++] = s[i];
    }
    s[count++] = 0;
}

int writeit(int server, char *fmt, ...) {
    va_list va;
    char *buf = new char[MAX_BUF_LEN];
    va_start(va,fmt);
    vsprintf(buf,fmt,va);
    va_end(va);
#ifdef WIN32
    if(send(server,buf,strlen(buf),0) == -1)
#else
    if(write(server,buf,strlen(buf)) == -1)
#endif
    {
        delete(buf);
        return(-1);
    }
    delete(buf);
    return(1);
}

char* spacefixer(char *s,char b[1]) {
    unsigned int i = 0;
    unsigned int j = strcspn(s,b) + 1;
    while(j <= strlen(s)) s[i++] = s[j++];
    s[i + 1] = 0;
    return(s);
}

int set_blocking(SOCKET sfd, int block) {  /* Not tested. */
    u_long noblock = (block ? 0L : 1L);
    int rc = ioctlsocket(sfd, FIONBIO, &noblock);

    if(rc) errno = WSAGetLastError();

    return(rc);
}

void init_winsocks() {
    int rVal = 0;

    if(winsock_active != 0) return;
    WORD version = MAKEWORD(1,1);

    // We're compatible with version 1.1 of WS2_32.dll
    WSAStartup(version, (LPWSADATA)&wsaData);
    winsock_active = 1;
}

//int call_socket (char hostname[254], unsigned short portnum)
int call_socket(char *hostname, unsigned short portnum) {
    struct sockaddr_in sa;
    struct hostent *hp;
    int s;
    int iRet;

    init_winsocks();

    if((hp = gethostbyname(hostname)) == NULL) return(-1);

    memset(&sa, 0, sizeof (sa));
    memcpy((char *) &sa.sin_addr, hp->h_addr, hp->h_length);
    sa.sin_family = hp->h_addrtype;
    sa.sin_port = htons((u_short) portnum);

    if((s = socket(hp->h_addrtype, SOCK_STREAM, 0)) < 0) return(-1);

    if(connect (s, (struct sockaddr *) &sa, sizeof(sa)) < 0) {
#ifdef WIN32
        closesocket (s);
#else
        close (s);
#endif
        return(-1);
    }

    iRet = set_blocking(s,FALSE);
    return(s);
}

//==============================================================
// Charlotte - A small Windows web server 
//
// Stuart Patterson
//==============================================================







//==============================================================
// ADAPTED FROM :
// Charlotte - A small Windows web server
//
// By Stuart Patterson
//==============================================================

//-------------------------------------------------------------

//--------------------------------------------------------------
//	DetermineHost()
//		If webserver needs to redirect user from directory to
//		default html file the server builds a full url and hence
//		needs it's full domain name for http address.
//			http://mymachine.rollins.brevard.edu/index.html
//--------------------------------------------------------------
void DetermineHost( char *hostname ) {
    IN_ADDR in;
    hostent *h;
    hostent *h2;

    gethostname(hostname,MAX_PATH);
    h = gethostbyname(hostname);
    memcpy(&in,h->h_addr,4);
    h2 = gethostbyaddr((char *)&in,4,PF_INET);
    if(h2!=NULL) {
        strcpy(hostname,h2->h_name);
    }
}   



//--------------------------------------------------------------
//	StartWebServer()
//		Creates server sock and binds to ip address and port
//--------------------------------------------------------------
SOCKET StartWebServer() {
    SOCKET s = socket(AF_INET,SOCK_STREAM,0);
    if(s == INVALID_SOCKET) {
        OutputScreenError("Error creating sock()");
        return(0);
    }

    SOCKADDR_IN si;

    si.sin_family = AF_INET;
    si.sin_port = htons(wwwport);               // port
    si.sin_addr.s_addr = htonl(INADDR_ANY);

    if(bind(s, (struct sockaddr *) &si, sizeof(SOCKADDR_IN)) == SOCKET_ERROR) {
        OutputScreenError("Error in bind()");
        closesocket(s);
        return(0);
    }

    return(s);
}


//--------------------------------------------------------------
// wwwtick()
//	looks for a pending request. If there is one then process it
//--------------------------------------------------------------
int wwwtick(char *inbuf, ClientInfo *ci, HTTPRequestHeader *requestheaderP) {

    SOCKET server_socket;
    SOCKET client_socket;
    SOCKADDR_IN client_address;
    int client_address_len = sizeof(SOCKADDR_IN);
    int iNotUsed = 0;
    int iCt;
    struct timeval timeout;
    fd_set setRead;

    server_socket = www_server_socket;
    FD_ZERO(&setRead);
    FD_SET(www_server_socket,&setRead);
    timeout.tv_sec = 0L;
    timeout.tv_usec = 0L;


    // while we are here loop while there are extern connections to service
    // while ((iCt = select(iNotUsed, &setRead, NULL, NULL, &timeout)) !=0)
    if((iCt = select(iNotUsed, &setRead, NULL, NULL, &timeout)) == 0) return(0);

    client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len);
    if(client_socket == INVALID_SOCKET) {
        OutputScreenError("Error in accept()");
        udpprint("Error in accept()");
        closesocket(server_socket);
        return(0);
    }

    // copy client ip and socket so the HandleHTTPRequest thread and process the request.
    ci->client_socket = client_socket;
    memcpy(&(ci->client_ip), &client_address.sin_addr.s_addr, 4);

    // for each request start a new thread!
    //_beginthread(HandleHTTPRequest, 0, (void *)ci);
    //udpprint("start HandleHTTPRequest");
    HandleHTTPRequest(inbuf, requestheaderP, (void *) ci);
    return(1);
}


int wwwreply(char *outbuf, ClientInfo *ci,  HTTPRequestHeader *requestheaderP) {
    SOCKET client_socket;
    //int size;
    //char receivebuffer[COMM_BUFFER_SIZE];


    FILE *in;
    char *filebuffer;
    long filesize;
    DWORD fileattrib;
    udpprint("URL");
    udpprint(requestheaderP->url);
    udpprint("filepathname");
    udpprint(requestheaderP->filepathname);
    gameLocal.Printf ("requestheaderP->method=%s \n",requestheaderP->method);
    gameLocal.Printf ("requestheaderP->url=%s \n",requestheaderP->url);
    gameLocal.Printf ("requestheaderP->filepathname=%s \n",requestheaderP->filepathname);
    gameLocal.Printf ("requestheaderP->httpversion=%s \n",requestheaderP->httpversion);

    client_socket = ci->client_socket;

	if (strlen(outbuf)>8)
	{
	//udpprint("get header buffer");
    char sendbuffer[COMM_BUFFER_SIZE];
 	//udpprint("H1");
   // send the http header and the file contents to the browser
    strcpy(sendbuffer, "HTTP/1.0 200 OK\r\n");
 	//udpprint("H2");
    strncat(sendbuffer, "Content-Type: ", COMM_BUFFER_SIZE);
 	//udpprint("H3");
    strncat(sendbuffer, mimetypes[1].mime, COMM_BUFFER_SIZE);
 	//udpprint("H4");
    sprintf(sendbuffer + strlen(sendbuffer), "\r\nContent-Length: %ld\r\n", strlen(outbuf));
 	//udpprint("H5");
    strncat(sendbuffer, "\r\n", COMM_BUFFER_SIZE);
 	//udpprint("H6");

    send(client_socket, sendbuffer, strlen(sendbuffer), 0);
 	//udpprint("H7");
    send(client_socket, outbuf, strlen(outbuf), 0);
 	//udpprint("H8");

    closesocket(client_socket);
 	//udpprint("H9");
    //delete [] sendbuffer;
 	//udpprint("H10");

    return(1);


	}

    fileattrib = GetFileAttributes(requestheaderP->filepathname);

    if(fileattrib != -1 && fileattrib & FILE_ATTRIBUTE_DIRECTORY) {
        OutputHTTPRedirect(client_socket, requestheaderP->url);
        return(0);
    }
    gameLocal.Printf ("fopen requestheaderP->filepathname=%s \n",requestheaderP->filepathname);

    in = fopen(requestheaderP->filepathname,"rb");
    if(!in) {  // read binary
        // file error, not found?
        OutputHTTPError(client_socket, 404);   // 404 - not found
        return(0);
    }

    // determine file size
    fseek(in, 0, SEEK_END);
    filesize = ftell(in);
    fseek(in, 0, SEEK_SET);

    // allocate buffer and read in file contents
    filebuffer = new char[filesize + strlen(outbuf) * 4];
    fread(filebuffer, sizeof(char), filesize, in);
    fclose(in);

    if(requestheaderP->mime_index == 0) {
        //if (strstr(filebuffer, "<BOTREPLY/>")) {
        //	filebuffer[filesize + 1] = 0; // make sure there is a null
        //	replace(filebuffer, "<BOTREPLY/>", outbuf); // insert the botreply into the HTML
        //	filesize = filesize - 11 + strlen(outbuf);
        //}
    }
    // send the http header and the file contents to the browser
	//udpprint("send the http header and the file contents to the browser");
    char sendbuffer[COMM_BUFFER_SIZE];

    strcpy(sendbuffer, "HTTP/1.0 200 OK\r\n");
    strncat(sendbuffer, "Content-Type: ", COMM_BUFFER_SIZE);
    strncat(sendbuffer, mimetypes[findMimeType(requestheaderP->filepathname)].mime, COMM_BUFFER_SIZE);
    sprintf(sendbuffer + strlen(sendbuffer), "\r\nContent-Length: %ld\r\n", filesize);
    strncat(sendbuffer, "\r\n", COMM_BUFFER_SIZE);

    send(client_socket, sendbuffer, strlen(sendbuffer), 0);
    send(client_socket, filebuffer, filesize, 0);

    closesocket(client_socket);
    filebuffer[0] = 0;
    delete [] filebuffer;
//    delete [] sendbuffer;

    return(1);
}


//--------------------------------------------------------------
//	HandleHTTPRequest()
//		Executed in its own thread to handling http transaction
//--------------------------------------------------------------
void HandleHTTPRequest(char *inbuf, HTTPRequestHeader *requestheaderP, void *data ) {
    SOCKET client_socket;
    //HTTPRequestHeader requestheader;
    int size;
    char receivebuffer[COMM_BUFFER_SIZE];
//	char sendbuffer[COMM_BUFFER_SIZE];
    //udpprint("start HandleHTTPRequest(1)");

    client_socket = ((ClientInfo *)data)->client_socket;
    requestheaderP->client_ip = ((ClientInfo *)data)->client_ip;

    //delete data;
    //udpprint("start HandleHTTPRequest(2)");

    size = SocketRead(client_socket, receivebuffer, COMM_BUFFER_SIZE);
    if(size == SOCKET_ERROR || size == 0) {
        OutputScreenError("Error calling recv()");
        closesocket(client_socket);
        return;
    }
    receivebuffer[size] = NULL;
    udpprint("start HandleHTTPRequest(3)");

    if(!ParseHTTPHeader(receivebuffer,requestheaderP)) {
        // handle bad header!
        OutputHTTPError(client_socket, 400);   // 400 - bad request
        return;
    }
    udpprint("start HandleHTTPRequest(4)");

    if(strstr(requestheaderP->method,"GET")) {
        if(strlen(requestheaderP->url) > 1) {
            char *p;
            if(p = strstr(requestheaderP->url, "?input=")) {

                //strcpy(inbuf, p + 7);
                //while (strstr(inbuf, "+")) strWebProc.Replace(replace(inbuf, "+", " ");
                strWebProc.Empty();
                strWebProc.Append(p+7);
                while(strstr(strWebProc.c_str(),"+")) strWebProc.Replace("+"," ");
                strcpy(inbuf,strWebProc.c_str());

            } else strcpy(inbuf, &requestheaderP->url[1]);

        } else strcpy(inbuf, requestheaderP->url);
    //udpprint("start HandleHTTPRequest(5)");
    //udpprint("start findMimeType(requestheaderP->url)");
    //udpprint(requestheaderP->url);

        requestheaderP->mime_index = findMimeType(requestheaderP->url);
    //udpprint("start HandleHTTPRequest(6)");

        gameLocal.Printf ("requestheaderP->mime_index=%d \n",requestheaderP->mime_index );
        if(requestheaderP->mime_index == 0) {
            // the default page


            gameLocal.Printf ("wwwroot=%s \n",wwwroot);
            gameLocal.Printf ("wwwpage=%s \n",wwwpage);

            strncpy(requestheaderP->filepathname, wwwroot, MAX_PATH);
            strncat(requestheaderP->filepathname, wwwpage, MAX_PATH);
            _fullpath(requestheaderP->filepathname, requestheaderP->filepathname, MAX_PATH);

        }
    //udpprint("start HandleHTTPRequest(7)");

        // This is where you do the security check 
        //if (strWebProc.Icmpn(requestheaderP->filepathname, wwwroot, strlen(wwwroot))) {
        //	OutputHTTPError(client_socket, 403);	// 403 - forbidden
        //	return;
        //}
    } else {
    //udpprint("start HandleHTTPRequest(8)");
        OutputHTTPError(client_socket, 501);   // 501 not implemented
        return;
    }
}


//--------------------------------------------------------------
//	SocketRead()
//		Reads data from the client socket until it gets a valid http
//		header or the client disconnects.
//--------------------------------------------------------------
int SocketRead(SOCKET client_socket, char *receivebuffer, int buffersize) {
    int size = 0;
    int totalsize = 0;

    do {
        size = recv(client_socket, receivebuffer + totalsize,buffersize - totalsize, 0);
        if(size != 0 && size != SOCKET_ERROR) {
            totalsize += size;
            if(strstr(receivebuffer,"\r\n\r\n")) break;    // are we done reading the http header?
        } else totalsize = size;                            // remember error state for return

    } while(size != 0 && size != SOCKET_ERROR);

    return(totalsize);
}


//--------------------------------------------------------------
//	OutputScreenError()
//		Writes an error message to the screen displays the socket
//		error code, clearing the error before exiting.
//--------------------------------------------------------------
void OutputScreenError(const char *errmsg) {
//	EnterCriticalSection (&output_criticalsection);
//   cout << errmsg << " - " << WSAGetLastError() << endl;
    gameLocal.Printf("%s - %s \n",errmsg,WSAGetLastError());
    WSASetLastError(0);
//	LeaveCriticalSection (&output_criticalsection);

}


//--------------------------------------------------------------
//	OutputHTTPError()
//		Sends an http header and html body to the client with
//		error information.
//--------------------------------------------------------------
void OutputHTTPError(SOCKET client_socket, int statuscode) {
    char headerbuffer[COMM_BUFFER_SIZE];
    char htmlbuffer[COMM_BUFFER_SIZE];

    sprintf(htmlbuffer, "<html><body><h2>Error: %d</h2></body></html>", statuscode);
    sprintf(headerbuffer, "HTTP/1.0 %d\r\nContent-Type: text/html\r\nContent-Length: %ld\r\n\r\n", statuscode, strlen(htmlbuffer));

    send(client_socket, headerbuffer, strlen(headerbuffer), 0);
    send(client_socket, htmlbuffer, strlen(htmlbuffer), 0);

    closesocket(client_socket);
}


//--------------------------------------------------------------
//	OutputHTTPRedirect()
//		Writes an HTTP redirect header and body to the client.
//		Called if the user requests a directory causing the redirect
//		to directory/index.html
//--------------------------------------------------------------
void OutputHTTPRedirect(SOCKET client_socket, const char *defaulturl) {
    char headerbuffer[COMM_BUFFER_SIZE];
    char htmlbuffer[COMM_BUFFER_SIZE];
    char hosturl[MAX_PATH];

    sprintf(hosturl, "http://%s", hostname);
    strncat(hosturl, defaulturl, COMM_BUFFER_SIZE);

    if(hosturl[strlen(hosturl)-1] != '/') strncat(hosturl, "/", MAX_PATH);
    strncat(hosturl, "index.html", MAX_PATH);

    sprintf(htmlbuffer, "<html><body><a href=\"%s\">%s</a></body></html>", hosturl, hosturl);
    sprintf(headerbuffer, "HTTP/1.0 301\r\nContent-Type: text/html\r\nContent-Length: %ld\r\nLocation: %s\r\n\r\n", strlen(htmlbuffer), hosturl);

    send(client_socket, headerbuffer, strlen(headerbuffer), 0);
    send(client_socket, htmlbuffer, strlen(htmlbuffer), 0);

    closesocket(client_socket);
}

//--------------------------------------------------------------
//	ParseHTTPHeader()
//		Fills a HTTPRequestHeader with method, url, http version
//		and file system path information.
//--------------------------------------------------------------
BOOL ParseHTTPHeader(char *receivebuffer, HTTPRequestHeader *requestheaderP) {
    char *pos;
    // http request header format
    // method uri httpversion

    //debugging
    //EnterCriticalSection (&output_criticalsection);
    //cout << receivebuffer << endl;
    //LeaveCriticalSection (&output_criticalsection);
    // end debugging

    pos = strtok(receivebuffer," ");
    if(pos == NULL) return(FALSE);
    strncpy(requestheaderP->method, pos, SMALL_BUFFER_SIZE);

    pos = strtok(NULL," ");
    if(pos == NULL) return(FALSE);
    strncpy(requestheaderP->url, pos, MAX_PATH);
    decodeURLEncode(requestheaderP->url);

    pos = strtok(NULL,"\r");
    if(pos == NULL) return(FALSE);
    strncpy(requestheaderP->httpversion, pos, SMALL_BUFFER_SIZE);

    // based on the url lets figure out the filename + path
    strncpy(requestheaderP->filepathname, wwwroot, MAX_PATH);
    strncat(requestheaderP->filepathname, requestheaderP->url, MAX_PATH);

    // because the filepathname can have relative references  ../ ./
    // call _fullpath to get the absolute 'real' filepath
    // _fullpath seems to handle '/' and '\'
    _fullpath(requestheaderP->filepathname, requestheaderP->filepathname, MAX_PATH);

    return(TRUE);
}

//--------------------------------------------------------------
//	findMimeType()
//		Performs linear search through mimetypes array looking for
//		matching file extension returning index of mime type
//--------------------------------------------------------------
int findMimeType(const char *filename) {
    const char *pos=0;
    int numofelements;
    int x;
	//udpprint("Enter findMimeType");
	pos = strrchr(filename,'.');
    if(pos) {
		//udpprint("pos=");
		//udpprint(pos);
		//numofelements = sizeof(mimetypes) / sizeof(MimeAssociation);
		numofelements=NUM_MIME_TYPES-1;
        for(x = 0; x < numofelements; x++) if(!stricmp(mimetypes[x].file_ext, pos)) return(x);
    }

	//udpprint("Exit findMimeType(0)");
    return(0);  // return default mimetype  'text/plain'
}

int runWWW() {
    int iRet;
    int iRetV = 0;

    char *inbuf = new char[MAX_REQSTRING];
    ClientInfo client;
    ClientInfo *clientP;
    HTTPRequestHeader requestheader;
    HTTPRequestHeader *requestheaderP;

    clientP = &client;
    requestheaderP = &requestheader;

    if(iRet = wwwtick(inbuf,clientP,requestheaderP)) {
        char *replybuf = new char[MAX_REPLY_BUF];
        char *wwwbuf=NULL; 
        if(!requestheader.mime_index) {    // the basic request
            getAnswer(inbuf,replybuf);

            // fix for html used by web server
            //strcpy(wwwbuf,replybuf);
            //while(replace(wwwbuf,"\r\n","<br/>"));
            strWebProc.Empty();
            strWebProc.Append(replybuf);
            strWebProc.Replace("\n\n","<br/>");
			wwwbuf=new char[1+strlen(strWebProc.c_str())];
			strcpy(wwwbuf,strWebProc.c_str());

        } else {                            // a file request
            *replybuf = NULL;
        }

		//udpprint("enter wwwreply");
        iRet = wwwreply(wwwbuf,clientP,requestheaderP);
		//udpprint("exit wwwreply");

        // ADD TO THE LOGS
        //char *flname = new char[MAX_PATH_LEN];
        //sprintf(flname,"%sWEB_%s.txt",weblogdir,irc_in.sendnick);
        //FILE *flog = fopen(flname,"a");
        //fprintf(flog,"<interaction>\r\n <user>%s</user>\r\n <input>%s</input>\r\n",irc_in.sendnick,inbuf);
        //fprintf(flog," <response>%s</response>\r\n</interaction>\r\n",replybuf);
        //fclose(flog);
        //delete flname;
        delete wwwbuf;
        delete replybuf;
        iRetV = 1;
    }
    delete inbuf;
    return(iRetV);
}

//--------------------------------------------------------------
//  www_init()
//		Start of application.  Initializes winsock and starts
//		server
//--------------------------------------------------------------

int wwwinit() {
    int iRet;

    // init the winsock libraries
    init_winsocks();

    // get name of webserver machine. needed for redirects
    // gethostname does not return a fully qualified host name
    DetermineHost(hostname);

    // init the webserver;
    if(www_server_socket = StartWebServer()) {
        iRet = set_blocking(www_server_socket,FALSE);

        if(listen(www_server_socket,SOMAXCONN) == SOCKET_ERROR) {
            OutputScreenError("Error in listen()");
            closesocket(www_server_socket);
        }
    }

    return(0);
}


void CheckForClientConnections() {
//	WaitForClientConnections(www_server_socket);
    if(www_server_socket==0) {
        strcpy(wwwroot,"c:/doom3/wwwroot");
        strcpy(wwwpage,"/default.txt");
        wwwinit();
        gameLocal.Printf ("Init WWW Server complete \n");
        gameLocal.Printf ("wwwroot=%s \n",wwwroot);
        gameLocal.Printf ("hostname=%s\n",hostname);
        gameLocal.Printf ("wwwport =%d\n",wwwport);
    } else {
        runWWW();
    }
}

#endif


