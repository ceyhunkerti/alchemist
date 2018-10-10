
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-attributeName'])
optional = frozenset(['-displayName', '-description', '-values'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.updateAttributeHelp()
    exit()

appStripe = argmap['appStripe']
attributeName = argmap['attributeName']
displayName = None
description = None
values = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'values' in argmap:
        values = argmap['values']

connect()
import Opss
Opss.updateAttribute(appStripe=appStripe, attributeName=attributeName, displayName=displayName, description=description, values=values)
