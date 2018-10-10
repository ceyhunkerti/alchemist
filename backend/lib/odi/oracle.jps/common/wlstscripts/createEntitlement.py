
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-name', '-resourceName', '-resourceType', '-actions'])
optional = frozenset(['-displayName','-description'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createEntitlementHelp()
    exit()

appStripe = argmap['appStripe']
name = argmap['name']
resourceName = argmap['resourceName']
resourceType = argmap['resourceType']
actions = argmap['actions']
displayName = None
description = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
connect()
import Opss
Opss.createEntitlement(appStripe=appStripe, name=name, resourceName=resourceName,resourceType=resourceType, actions=actions, displayName=displayName, description=description)

