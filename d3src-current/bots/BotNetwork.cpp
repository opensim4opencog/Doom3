#include "../../idlib/precompiled.h"
#pragma hdrstop

#ifdef MOD_BOTS
#ifdef D3V_D3XP
#include "../game/Game_local.h"
#else 
#include "../d3xp/Game_local.h"
#endif
/***********************************************************************

Network wrapper
From cobalt

***********************************************************************/

botNetworkSystem::botNetworkSystem(idNetworkSystem *networkSystem)
{
   network = networkSystem;
}

float botNetworkSystem::ClientGetIncomingPacketLoss()
{
   return network->ClientGetIncomingPacketLoss();
}

int botNetworkSystem::ClientGetIncomingRate()
{
   return network->ClientGetIncomingRate();
}

int botNetworkSystem::ClientGetPrediction()
{
   return network->ClientGetPrediction();
}

int botNetworkSystem::ClientGetTimeSinceLastPacket()
{
   return network->ClientGetTimeSinceLastPacket();
}

int botNetworkSystem::ClientGetOutgoingRate()
{
   return network->ClientGetOutgoingRate();
}

float botNetworkSystem::ServerGetClientIncomingPacketLoss(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0;
   return network->ServerGetClientIncomingPacketLoss(clientNum);
}

int botNetworkSystem::ServerGetClientIncomingRate(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0;
   return network->ServerGetClientIncomingRate(clientNum);
}

int  botNetworkSystem::ServerGetClientOutgoingRate(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0;
   return network->ServerGetClientOutgoingRate(clientNum);
}


int botNetworkSystem::ServerGetClientPing(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 50;   // Steve:: Can we make this random to emulate bot latency?
   return network->ServerGetClientPing(clientNum);
}

int botNetworkSystem::ServerGetClientTimeSinceLastInput(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0; // Steve:: What are the effects of this?
   return network->ServerGetClientTimeSinceLastInput(clientNum);
}


int botNetworkSystem::ServerGetClientTimeSinceLastPacket(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0; // Steve:: What are the effects of this?
   return network->ServerGetClientTimeSinceLastPacket(clientNum);
}


int botNetworkSystem::ServerGetClientPrediction(int clientNum)
{
   if (clientNum >= BOT_START_INDEX)
      return 0;
   return network->ServerGetClientPrediction(clientNum);
}

void botNetworkSystem::ServerSendReliableMessage(int clientNum, const idBitMsg &msg)
{
   if (clientNum >= BOT_START_INDEX)
      return;
   network->ServerSendReliableMessage(clientNum, msg);
}

void botNetworkSystem::ServerSendReliableMessageExcluding(int clientNum, const idBitMsg &msg)
{
   if (clientNum >= BOT_START_INDEX)
      return;
   network->ServerSendReliableMessageExcluding(clientNum, msg);
}

void botNetworkSystem::ClientSendReliableMessage(const idBitMsg &msg)
{
   network->ClientSendReliableMessage(msg);
}
#endif // MOD_BOTS
