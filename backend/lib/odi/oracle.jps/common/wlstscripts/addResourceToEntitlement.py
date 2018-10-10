
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-name', '-resourceName', '-resourceType', '-actions'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.addResourceToEntitlementHelp()
    exit()

appStripe = argmap['appStripe']
name = argmap['name']
resourceName = argmap['resourceName']
resourceType = argmap['resourceType']
actions = argmap['actions']

connect()
import Opss
Opss.addResourceToEntitlement(appStripe=appStripe, name=name, resourceName=resourceName, resourceType=resourceType, actions=actions)

