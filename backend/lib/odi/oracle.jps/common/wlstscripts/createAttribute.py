
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-attributeName', '-type', '-category'])
optional = frozenset(['-displayName','-description','-isSingle', '-values'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createAttributeHelp()
    exit()

appStripe = argmap['appStripe']
attributeName = argmap['attributeName']
type = argmap['type']
category = argmap['category']
displayName = None
description = None
isSingle = None
values = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'isSingle' in argmap:
        isSingle = argmap['isSingle']
if 'values' in argmap:
        values = argmap['values']

connect()
import Opss
Opss.createAttribute(appStripe=appStripe, attributeName=attributeName, displayName=displayName, description=description, type=type, category=category, isSingle=isSingle, values=values)
