
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe'])
optional = frozenset(['-resourceTypeName', '-resourceName'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listEntitlementsHelp()
    exit()

appStripe = argmap['appStripe']
resourceTypeName = None
resourceName = None

if 'resourceName' in argmap:
    resourceName = argmap['resourceName']
if 'resourceTypeName' in argmap:
    resourceTypeName = argmap['resourceTypeName']

connect()
import Opss
Opss.listEntitlements(appStripe=appStripe, resourceTypeName=resourceTypeName, resourceName=resourceName)

