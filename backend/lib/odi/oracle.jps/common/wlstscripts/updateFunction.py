
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-functionName'])
optional = frozenset(['-displayName', '-description', '-className', '-returnType', '-paramTypes'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.updateFunctionHelp()
    exit()

appStripe = argmap['appStripe']
functionName = argmap['functionName']
displayName = None
description = None
className = None
returnType = None
paramTypes = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'className' in argmap:
        className = argmap['className']
if 'returnType' in argmap:
        returnType = argmap['returnType']
if 'paramTypes' in argmap:
        paramTypes = argmap['paramTypes'] 

connect()
import Opss
Opss.updateFunction(appStripe=appStripe, functionName=functionName, displayName=displayName, description=description, className=className, returnType=returnType, paramTypes=paramTypes)
