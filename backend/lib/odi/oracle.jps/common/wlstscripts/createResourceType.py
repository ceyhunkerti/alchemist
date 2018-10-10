
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-resourceTypeName'])
optional = frozenset(['-provider', '-matcher', '-delimiter', '-allowedActions', '-displayName', '-description'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createResourceTypeHelp()
    exit()

appStripe = argmap['appStripe']
resourceTypeName = argmap['resourceTypeName']
displayName = None
description = None
allowedActions = None
provider = None
matcher = None
delimiter = None
if 'displayName' in argmap:
    displayName = argmap['displayName']
if 'description' in argmap:
    description = argmap['description']
if 'provider' in argmap:
    provider = argmap['provider']
if 'matcher' in argmap:
    matcher = argmap['matcher']
if 'delimiter' in argmap:
    delimiter = argmap['delimiter']
if 'allowedActions' in argmap:
    allowedActions = argmap['allowedActions']
connect()
import jpsWlstCmd
jpsWlstCmd.createResourceType(appStripe=appStripe, resourceTypeName=resourceTypeName, displayName=displayName, description=description, provider=provider, matcher=matcher, allowedActions=allowedActions, delimiter=delimiter)

