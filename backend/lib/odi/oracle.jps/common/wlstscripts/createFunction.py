
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-functionName', '-className', '-returnType'])
optional = frozenset(['-displayName','-description','-paramTypes'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createFunctionHelp()
    exit()

appStripe = argmap['appStripe']
functionName = argmap['functionName']
className = argmap['className']
returnType = argmap['returnType']
displayName = None
description = None
paramTypes = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'paramTypes' in argmap:
        paramTypes = argmap['paramTypes']

connect()
import Opss
Opss.createFunction(appStripe=appStripe, functionName=functionName, displayName=displayName, description=description, className=className, returnType=returnType, paramTypes=paramTypes)

