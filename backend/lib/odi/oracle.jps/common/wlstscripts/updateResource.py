
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-resourceName', '-type'])
optional = frozenset(['-displayName', '-description', '-attributes'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.updateResourceHelp()
    exit()

appStripe = argmap['appStripe']
resourceName = argmap['resourceName']
type = argmap['type']
displayName = None
description = None
attributes = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'attributes' in argmap:
        attributes = argmap['attributes']

connect()
import Opss
Opss.updateResource(appStripe=appStripe, resourceName=resourceName, type=type, displayName=displayName, description=description, attributes=attributes)
