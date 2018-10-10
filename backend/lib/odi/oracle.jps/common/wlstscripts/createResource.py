
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-name', '-type'])
optional = frozenset(['-displayName','-description'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createResourceHelp()
    exit()

appStripe = argmap['appStripe']
name = argmap['name']
type = argmap['type']
displayName = None
description = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']

connect()
import Opss
Opss.createResource(appStripe=appStripe,name=name, type=type, displayName=displayName, description=description)

