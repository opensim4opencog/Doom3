#ifndef __BOTNETWORK__
#define __BOTNETWORK__

/***********************************************************************

Network wrapper
From cobalt

***********************************************************************/

#define BOT_START_INDEX      16

class botNetworkSystem
{
public:
   botNetworkSystem (idNetworkSystem *networkSystem);

   void         ServerSendReliableMessage( int clientNum, const idBitMsg &msg );
   void         ServerSendReliableMessageExcluding( int clientNum, const idBitMsg &msg );
   int            ServerGetClientPing( int clientNum );
   int            ServerGetClientPrediction( int clientNum );
   int            ServerGetClientTimeSinceLastPacket( int clientNum );
   int            ServerGetClientTimeSinceLastInput( int clientNum );
   int            ServerGetClientOutgoingRate( int clientNum );
   int            ServerGetClientIncomingRate( int clientNum );
   float         ServerGetClientIncomingPacketLoss( int clientNum );

   void         ClientSendReliableMessage( const idBitMsg &msg );
   int            ClientGetPrediction( void );
   int            ClientGetTimeSinceLastPacket( void );
   int            ClientGetOutgoingRate( void );
   int            ClientGetIncomingRate( void );
   float         ClientGetIncomingPacketLoss( void );

private:
   idNetworkSystem   *network;
};

extern botNetworkSystem * networkSystem;

#endif __BOTNETWORK__
